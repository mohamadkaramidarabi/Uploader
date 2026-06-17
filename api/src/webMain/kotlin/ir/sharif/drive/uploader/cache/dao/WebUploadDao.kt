package ir.sharif.drive.uploader.cache.dao

import ir.sharif.drive.uploader.cache.entity.ILinkEntity
import ir.sharif.drive.uploader.cache.entity.IUploadEntity
import ir.sharif.drive.uploader.cache.entity.IUploadWithLinks
import ir.sharif.drive.uploader.cache.entity.UploadItem
import ir.sharif.drive.uploader.cache.entity.WebLinkEntity
import ir.sharif.drive.uploader.cache.entity.WebUploadEntity
import ir.sharif.drive.uploader.cache.entity.WebUploadWithLinks
import ir.sharif.drive.uploader.cache.entity.createWebUploadWithLinks
import ir.sharif.drive.uploader.cache.indexeddb.IndexedDbStore
import ir.sharif.drive.uploader.models.States
import ir.sharif.drive.uploader.source.file.WebFileRegistry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val META_NEXT_UPLOAD_ID = "nextUploadId"
private const val META_NEXT_LINK_ID = "nextLinkId"

internal object WebUploadDao :
    IUploadDao<IUploadEntity, ILinkEntity, IUploadWithLinks<IUploadEntity, ILinkEntity>> {

    private val mutex = Mutex()
    private var nextUploadId = 1L
    private var nextLinkId = 1L
    private val uploads = mutableMapOf<Long, WebUploadEntity>()
    private val links = mutableMapOf<Long, WebLinkEntity>()
    private val revision = MutableStateFlow(0)
    private var loaded = false

    private fun bump() {
        revision.value++
    }

    private fun <T> observe(block: () -> T): Flow<T> = revision.map { block() }

    private fun uploadWithLinks(upload: WebUploadEntity): WebUploadWithLinks {
        val uploadLinks = links.values
            .filter { it.uploadId == upload.id }
            .sortedBy { it.id }
        return WebUploadWithLinks(upload, uploadLinks)
    }

    private fun allUploadWithLinks(): List<WebUploadWithLinks> =
        uploads.values.sortedBy { it.id }.map(::uploadWithLinks)

    private fun assignUploadId(entity: WebUploadEntity): WebUploadEntity {
        val id = if (entity.id == 0L) nextUploadId++ else entity.id
        nextUploadId = maxOf(nextUploadId, id + 1)
        return entity.copy(id = id)
    }

    private fun assignLinkId(entity: WebLinkEntity): WebLinkEntity {
        val id = if (entity.id == 0L) nextLinkId++ else entity.id
        nextLinkId = maxOf(nextLinkId, id + 1)
        return entity.copy(id = id)
    }

    private suspend fun ensureLoaded() {
        if (loaded) return
        IndexedDbStore.open()
        WebFileRegistry.restore()
        val storedUploads = IndexedDbStore.loadUploads()
        val storedLinks = IndexedDbStore.loadLinks()
        mutex.withLock {
            uploads.clear()
            links.clear()
            storedUploads.forEach { uploads[it.id] = it }
            storedLinks.forEach { links[it.id] = it }
            nextUploadId = maxOf(
                IndexedDbStore.getMetaLong(META_NEXT_UPLOAD_ID, 1L),
                storedUploads.maxOfOrNull { it.id }?.plus(1) ?: 1L,
            )
            nextLinkId = maxOf(
                IndexedDbStore.getMetaLong(META_NEXT_LINK_ID, 1L),
                storedLinks.maxOfOrNull { it.id }?.plus(1) ?: 1L,
            )
            loaded = true
        }
        bump()
    }

    private suspend fun persistCounters() {
        IndexedDbStore.saveMetaLong(META_NEXT_UPLOAD_ID, nextUploadId)
        IndexedDbStore.saveMetaLong(META_NEXT_LINK_ID, nextLinkId)
    }

    override suspend fun insertAll(vararg uploadEntity: IUploadEntity) {
        ensureLoaded()
        val persisted = mutex.withLock {
            uploadEntity.map { entity ->
                assignUploadId(entity as WebUploadEntity).also { uploads[it.id] = it }
            }
        }
        persisted.forEach { IndexedDbStore.saveUpload(it) }
        persistCounters()
        bump()
    }

    override suspend fun update(uploadEntity: IUploadEntity) {
        ensureLoaded()
        val upload = uploadEntity as WebUploadEntity
        mutex.withLock {
            uploads[upload.id] = upload
        }
        IndexedDbStore.saveUpload(upload)
        bump()
    }

    override suspend fun updateLink(updateLink: ILinkEntity) {
        ensureLoaded()
        val link = updateLink as WebLinkEntity
        mutex.withLock {
            links[link.id] = link
        }
        IndexedDbStore.saveLink(link)
        bump()
    }

    override suspend fun updateWithLinks(uploadWithLinks: IUploadWithLinks<IUploadEntity, ILinkEntity>) {
        ensureLoaded()
        val upload = uploadWithLinks.upload as WebUploadEntity
        val persistedLinks = mutex.withLock {
            uploads[upload.id] = upload
            uploadWithLinks.links.map { linkEntity ->
                assignLinkId(linkEntity as WebLinkEntity).also { links[it.id] = it }
            }
        }
        IndexedDbStore.saveUpload(upload)
        persistedLinks.forEach { IndexedDbStore.saveLink(it) }
        persistCounters()
        bump()
    }

    override fun getUploadWithLinksFlow(state: States.UploadInfo.State): Flow<IUploadWithLinks<IUploadEntity, ILinkEntity>?> =
        observe {
            uploads.values
                .sortedBy { it.id }
                .firstOrNull { it.state == state }
                ?.let { createWebUploadWithLinks(it, uploadWithLinks(it).links) }
        }

    override fun getUploadInfosFlow(state: States.UploadInfo.State): Flow<List<IUploadWithLinks<IUploadEntity, ILinkEntity>>> =
        observe {
            allUploadWithLinks()
                .filter { it.upload.state == state }
                .map { createWebUploadWithLinks(it.upload, it.links) }
        }

    override suspend fun deleteLinksByUploadId(uploadId: Long) {
        ensureLoaded()
        val removedIds = mutex.withLock {
            links.filterValues { it.uploadId == uploadId }.keys.toList().also { ids ->
                ids.forEach { links.remove(it) }
            }
        }
        removedIds.forEach { IndexedDbStore.deleteLink(it) }
        bump()
    }

    override fun getUploadByStateFlow(state: States.UploadInfo.State): Flow<IUploadEntity?> =
        observe {
            uploads.values.sortedBy { it.id }.firstOrNull { it.state == state }
        }

    override suspend fun getUploadById(id: Long): IUploadWithLinks<IUploadEntity, ILinkEntity>? {
        ensureLoaded()
        return mutex.withLock {
            val upload = uploads[id] ?: return null
            createWebUploadWithLinks(upload, uploadWithLinks(upload).links)
        }
    }

    override fun allFlow(): Flow<List<IUploadWithLinks<IUploadEntity, ILinkEntity>>> =
        observe {
            allUploadWithLinks().map { createWebUploadWithLinks(it.upload, it.links) }
        }

    override fun firstInQueueOrFailedLink(runningLimitCount: Int): Flow<ILinkEntity?> =
        observe {
            val activeUploadStates = setOf(
                States.UploadInfo.State.UPLOADING,
                States.UploadInfo.State.STARTED,
            )
            val runningCount = links.values.count { link ->
                link.state == States.Link.State.RUNNING &&
                    uploads[link.uploadId]?.state in activeUploadStates
            }
            links.values
                .sortedBy { it.id }
                .firstOrNull { link ->
                    if (uploads[link.uploadId]?.state !in activeUploadStates) {
                        return@firstOrNull false
                    }
                    when (link.state) {
                        States.Link.State.IN_QUEUE -> runningCount < runningLimitCount
                        States.Link.State.FAILED -> true
                        else -> false
                    }
                }
        }

    override fun firstLinkByState(state: States.Link.State): Flow<ILinkEntity?> =
        observe {
            links.values.sortedBy { it.id }.firstOrNull { it.state == state }
        }

    override fun firstUploadInfoWithStateAndAllLinkWithState(
        state: States.UploadInfo.State,
        linkState: States.Link.State,
    ): Flow<IUploadEntity?> = observe {
        uploads.values.firstOrNull { upload ->
            upload.state == state &&
                upload.chunkCount != null &&
                links.values.count { it.uploadId == upload.id && it.state == linkState } == upload.chunkCount
        }
    }

    override suspend fun cancel(id: Long) {
        deleteLinksByUploadId(id)
        updateUploadStateById(id, States.UploadInfo.State.CANCELED)
    }

    override suspend fun updateUploadStateById(id: Long, state: States.UploadInfo.State) {
        ensureLoaded()
        val upload = mutex.withLock {
            uploads[id]?.copy(state = state)?.also { uploads[id] = it }
        } ?: return
        IndexedDbStore.saveUpload(upload)
        bump()
    }

    override suspend fun init() {
        ensureLoaded()
        val changedUploads = mutableListOf<WebUploadEntity>()
        val changedLinks = mutableListOf<WebLinkEntity>()
        mutex.withLock {
            links.keys.toList().forEach { linkId ->
                val link = links[linkId] ?: return@forEach
                if (link.state == States.Link.State.RUNNING) {
                    val updated = link.copy(state = States.Link.State.IN_QUEUE)
                    links[linkId] = updated
                    changedLinks += updated
                }
            }
            uploads.keys.toList().forEach { uploadId ->
                val upload = uploads[uploadId] ?: return@forEach
                val newState = when (upload.state) {
                    States.UploadInfo.State.STARTING -> States.UploadInfo.State.PREPARED
                    States.UploadInfo.State.COMPLETING -> States.UploadInfo.State.ALL_PUT_DONE
                    else -> null
                }
                if (newState != null) {
                    val updated = upload.copy(state = newState)
                    uploads[uploadId] = updated
                    changedUploads += updated
                }
            }
        }
        changedUploads.forEach { IndexedDbStore.saveUpload(it) }
        changedLinks.forEach { IndexedDbStore.saveLink(it) }
        bump()
    }

    override suspend fun updateStates(
        id: Long,
        preState: States.UploadInfo.State,
        state: States.UploadInfo.State,
        preLinkState: States.Link.State,
        linkState: States.Link.State,
    ) {
        ensureLoaded()
        val changedUploads = mutableListOf<WebUploadEntity>()
        val changedLinks = mutableListOf<WebLinkEntity>()
        mutex.withLock {
            uploads[id]?.let { upload ->
                if (upload.state == preState) {
                    val updated = upload.copy(state = state)
                    uploads[id] = updated
                    changedUploads += updated
                }
            }
            links.keys.toList().forEach { linkId ->
                val link = links[linkId] ?: return@forEach
                if (link.uploadId == id && link.state == preLinkState) {
                    val updated = link.copy(state = linkState)
                    links[linkId] = updated
                    changedLinks += updated
                }
            }
        }
        changedUploads.forEach { IndexedDbStore.saveUpload(it) }
        changedLinks.forEach { IndexedDbStore.saveLink(it) }
        bump()
    }

    override suspend fun pause(id: Long) {
        ensureLoaded()
        val changedUploads = mutableListOf<WebUploadEntity>()
        val changedLinks = mutableListOf<WebLinkEntity>()
        mutex.withLock {
            uploads[id]?.let { upload ->
                val updated = upload.copy(state = States.UploadInfo.State.PAUSED)
                uploads[id] = updated
                changedUploads += updated
            }
            links.keys.toList().forEach { linkId ->
                val link = links[linkId] ?: return@forEach
                if (link.uploadId == id &&
                    (link.state == States.Link.State.RUNNING || link.state == States.Link.State.IN_QUEUE)
                ) {
                    val updated = link.copy(state = States.Link.State.PAUSED)
                    links[linkId] = updated
                    changedLinks += updated
                }
            }
        }
        changedUploads.forEach { IndexedDbStore.saveUpload(it) }
        changedLinks.forEach { IndexedDbStore.saveLink(it) }
        bump()
    }

    override fun uploadItems(): Flow<List<UploadItem>> = observe {
        uploads.values.sortedBy { it.id }.map(::toUploadItem)
    }

    override fun uploadItemsByState(state: States.UploadInfo.State): Flow<List<UploadItem>> = observe {
        uploads.values
            .filter { it.state == state }
            .sortedBy { it.id }
            .map(::toUploadItem)
    }

    override suspend fun deleteAll() {
        ensureLoaded()
        mutex.withLock {
            uploads.clear()
            links.clear()
            nextUploadId = 1L
            nextLinkId = 1L
        }
        IndexedDbStore.clearUploadsAndLinks()
        IndexedDbStore.clearFiles()
        IndexedDbStore.saveMetaLong(META_NEXT_UPLOAD_ID, 1L)
        IndexedDbStore.saveMetaLong(META_NEXT_LINK_ID, 1L)
        WebFileRegistry.clear()
        bump()
    }

    private fun toUploadItem(upload: WebUploadEntity): UploadItem {
        val successCount = links.values.count {
            it.uploadId == upload.id && it.state == States.Link.State.SUCCESS
        }
        val progress = if (upload.chunkCount != null && upload.chunkCount > 0) {
            successCount / upload.chunkCount
        } else {
            0
        }
        return UploadItem(
            id = upload.id,
            name = upload.fileName,
            progress = progress,
            size = upload.fileSize,
            state = upload.state,
        )
    }
}
