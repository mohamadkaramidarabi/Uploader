package ir.sharif.drive.uploader.concrete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eygraber.uri.toKmpUriOrNull
import ir.sharif.drive.uploader.api.IUploader
import ir.sharif.drive.uploader.models.ChunkCount.Companion.chunkCount
import ir.sharif.drive.uploader.models.ChunkSize.Companion.chunkSize
import ir.sharif.drive.uploader.models.CloudKey.Companion.cloudKey
import ir.sharif.drive.uploader.models.CompleteUploadRequest
import ir.sharif.drive.uploader.models.FilePath
import ir.sharif.drive.uploader.models.StartUploadResponse
import ir.sharif.drive.uploader.models.States
import ir.sharif.drive.uploader.models.UploadId.Companion.uploadId
import ir.sharif.drive.uploader.models.UploadInfo
import ir.sharif.drive.uploader.models.UploadRequest
import ir.sharif.drive.uploader.source.cache.IUploadCache
import ir.sharif.drive.uploader.source.file.FileReader
import ir.sharif.drive.uploader.source.file.createFileReader
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile

internal class Uploader private constructor(
    private val startUpload: suspend (size: Long) -> StartUploadResponse,
    private val putChunk: suspend (String, ByteArray, Long) -> String,
    private val completeUpload: suspend (CompleteUploadRequest) -> Unit,
    private val fileReader: FileReader,
) : IUploader, ViewModel() {
    companion object {
        @Volatile
        private var instance: Uploader? = null
        private val LOCK = SynchronizedObject()
        fun getInstance(
            startUpload: suspend (Long) -> StartUploadResponse,
            putChunk: suspend (String, ByteArray, Long) -> String,
            completeUpload: suspend (CompleteUploadRequest) -> Unit,
            fileReaderContext: Any,
        ): IUploader {
            return instance ?: synchronized(lock = LOCK) {
                val fileReader = createFileReader(fileReaderContext)
                val newInstance =
                    instance ?: Uploader(startUpload, putChunk, completeUpload, fileReader)
                newInstance.init()
                instance = newInstance
                newInstance
            }
        }

        private const val SIZE = "size"
        private const val PART_NUMBER = "partNumber"
    }

    val uploadCache: IUploadCache by lazy {
        IUploadCache()
    }

    private suspend fun readChunkFromFile(
        filePath: FilePath,
        chunkIndex: Int,
        chunkSize: Long
    ): ByteArray? {
        return fileReader.readChunk(filePath, chunkIndex, chunkSize)
    }

    private var rootJob: Job? = null
    private val putJobs = mutableMapOf<Long, ArrayList<Job>>()
    private fun init() {
        rootJob?.cancel(CancellationException("init new root job for uploader"))
        while (rootJob?.isCompleted == false) {
            continue
        }
        rootJob = viewModelScope.launch {
            uploadCache.init()
            println("uploader initialization started")
            launch {
                uploadCache.allUploadsItem.stateIn(this)
                    .collect {
                        println(it)
                    }
            }
            launch {
                uploadCache.getUploadInfoByState(States.UploadInfo.State.IN_QUEUE)
                    .stateIn(this)
                    .collect { uploadInfo ->
                        if (uploadInfo == null) return@collect
                        prepare(uploadInfo)
                    }
            }
            launch {
                uploadCache.getUploadInfoByState(States.UploadInfo.State.PREPARED)
                    .stateIn(this)
                    .collect { uploadInfo ->
                        if (uploadInfo == null) return@collect
                        start(uploadInfo)
                    }
            }
            launch {
                uploadCache.firstInQueueOrFailedLink
                    .stateIn(this)
                    .collect { link ->
                        if (link == null) return@collect
                        if (link.state == States.Link.State.FAILED) {
                            fail(link.uploadId)
                            return@collect
                        }
                        println("put : $link")
                        putLink(link)
                    }
            }
            launch {
                uploadCache.firstAllPutDone
                    .stateIn(this)
                    .collect {
                        if (it == null) return@collect
                        uploadCache.update(uploadInfo = it.copy(state = States.UploadInfo.State.ALL_PUT_DONE))
                    }
            }
            launch {
                uploadCache
                    .getUploadInfoWitLinksByState(States.UploadInfo.State.ALL_PUT_DONE)
                    .stateIn(this)
                    .collect { uploadInfo ->
                        if (uploadInfo == null) return@collect
                        completeUpload(uploadInfo)
                    }
            }
        }
    }

    private suspend fun completeUpload(uploadInfo: UploadInfo) {
        uploadCache.update(uploadInfo.copy(state = States.UploadInfo.State.COMPLETING))
        val result = runCatching {
            completeUpload(
                CompleteUploadRequest(
                    key = uploadInfo.key?.value.orEmpty(),
                    name = uploadInfo.name.value,
                    parent = uploadInfo.folderId?.value,
                    uploadId = uploadInfo.uploadId?.value.orEmpty(),
                    parts = uploadInfo.links
                        .mapIndexed { index, link ->
                            val uri = link.url.toKmpUriOrNull()
                            CompleteUploadRequest.Part(
                                eTag = link.eTag.orEmpty(),
                                partNumber = uri
                                    ?.getQueryParameter(key = PART_NUMBER)
                                    ?.toIntOrNull()
                                    ?: (index + 1)
                            )
                        },
                    versionGroup = uploadInfo.versionGroup,
                )
            )
        }
        result.getOrNull()?.let {
            uploadCache.update(uploadInfo.copy(state = States.UploadInfo.State.SUCCESS))
        }
            ?: uploadCache.update(uploadInfo.copy(state = States.UploadInfo.State.FAILED))
    }

    private fun putLink(link: UploadInfo.Link) {
        putJobs[link.uploadId] =
            (putJobs[link.uploadId] ?: arrayListOf()).apply {
                add(
                    viewModelScope.launch {
                        uploadCache.updateLink(link.copy(state = States.Link.State.RUNNING))
                        val uploadInfo =
                            uploadCache.getUploadInfoById(link.uploadId) ?: return@launch
                        if (uploadInfo.state !in listOf(
                                States.UploadInfo.State.UPLOADING,
                                States.UploadInfo.State.STARTED
                            )
                        ) return@launch
                        val uri = link.url.toKmpUriOrNull()
                        val result = runCatching {
                            if (uploadInfo.chunkSize != null) {
                                val chunkIndex = uri?.getQueryParameter(PART_NUMBER)
                                    ?.toIntOrNull()
                                    ?.minus(1)
                                    ?: 0
                                if (chunkIndex >= 0) {
                                    val chunkData = readChunkFromFile(
                                        filePath = uploadInfo.path,
                                        chunkIndex = chunkIndex,
                                        chunkSize = uploadInfo.chunkSize.value
                                    )
                                    if (chunkData != null) {
                                        uploadCache.update(
                                            uploadInfo
                                                .copy(state = States.UploadInfo.State.UPLOADING)
                                        )
                                        putChunk(link.url, chunkData, link.size)
                                    } else {
                                        throw Exception("Failed to read chunk")
                                    }
                                } else {
                                    throw Exception("Chunk index not found")
                                }
                            } else {
                                throw Exception("UploadInfo or chunkSize not found")
                            }
                        }
                        val mLink = result.getOrNull()?.let {
                            link.copy(eTag = it, state = States.Link.State.SUCCESS)
                        } ?: run {
                            if (result.exceptionOrNull() is CancellationException) {
                                link
                            } else {
                                link.copy(state = States.Link.State.FAILED)
                            }
                        }
                        uploadCache.updateLink(mLink)
                    }
                )
            }
    }

    private suspend fun start(uploadInfo: UploadInfo) {
        uploadCache.update(uploadInfo.copy(state = States.UploadInfo.State.STARTING))
        val result = runCatching { startUpload(uploadInfo.size.value) }
        result.getOrNull()?.let { result ->
            uploadCache.updateWithLinks(
                uploadInfo.copy(
                    uploadId = result.uploadId.uploadId,
                    key = result.key.cloudKey,
                    chunkSize = result.chunkSize.chunkSize,
                    chunkCount = result.links.size.chunkCount,
                    state = States.UploadInfo.State.STARTED,
                    links = result.links.map {
                        val uri = it.toKmpUriOrNull()
                        UploadInfo.Link(
                            uploadId = uploadInfo.id,
                            eTag = null,
                            state = States.Link.State.IN_QUEUE,
                            retryCount = 0,
                            id = 0,
                            size = uri?.getQueryParameter(key = SIZE)
                                ?.toLongOrNull()
                                ?: result.chunkSize,
                            url = it
                        )
                    }
                ))
        }
    }

    private suspend fun fail(uploadId: Long) {
        uploadCache.getUploadInfoById(uploadId)?.let {
            if (it.links.isNotEmpty()) {
                cancel(uploadId)
                uploadCache
                    .update(
                        uploadInfo = it.copy(state = States.UploadInfo.State.FAILED)
                    )
            }
        }
    }

    private fun prepare(uploadInfo: UploadInfo) {
        viewModelScope.launch {
            uploadCache.update(uploadInfo.copy(state = States.UploadInfo.State.PREPARING))
            uploadCache.update(uploadInfo.copy(state = States.UploadInfo.State.PREPARED))
        }
    }


    override fun upload(
        requests: List<UploadRequest>
    ) {
        viewModelScope.launch {
            requests.map {
                UploadInfo(
                    id = 0,
                    uploadId = null,
                    key = null,
                    name = it.fileName,
                    path = it.filePath,
                    size = it.fileSize,
                    chunkSize = null,
                    chunkCount = null,
                    state = States.UploadInfo.State.IN_QUEUE,
                    folderId = it.folderId,
                    cloudPath = it.cloudPath,
                    links = emptyList(),
                    versionGroup = null,
                )
            }.let {
                uploadCache.insertAll(it)
            }
        }
    }

    override fun getUploadingByState(state: States.UploadInfo.State): Flow<List<UploadInfo>> =
        uploadCache.allUploadInfoByState(state)


    override fun getAllUploadInfos(): Flow<List<UploadInfo>> = uploadCache.allUpload()


    override suspend fun pause(id: Long) {
        uploadCache.getUploadInfoById(id)?.let { uploadInfo ->
            if (uploadInfo.state == States.UploadInfo.State.UPLOADING) {
                putJobs[id]?.map { it.cancel() }
                uploadCache.pause(id)
            }
        }
    }

    override suspend fun resume(id: Long) {
        uploadCache.getUploadInfoById(id)?.let { uploadInfo ->
            if (uploadInfo.state == States.UploadInfo.State.PAUSED) {
                uploadCache.updateWithLinks(
                    uploadInfo = uploadInfo.copy(
                        state = States.UploadInfo.State.UPLOADING,
                        links = uploadInfo.links.map { link ->
                            if (link.state == States.Link.State.PAUSED) {
                                link.copy(state = States.Link.State.IN_QUEUE)
                            } else {
                                link
                            }
                        }
                    )
                )
            }
        }
    }

    override suspend fun cancel(id: Long) {
        uploadCache.getUploadInfoById(id)?.let { uploadInfo ->
            putJobs[id]?.map { it.cancel() }
            if (uploadInfo.state == States.UploadInfo.State.UPLOADING) {
                uploadCache.cancel(id)
            }
        }
    }

    override suspend fun deleteAll() {
        rootJob?.cancel()
        uploadCache.deleteAll()
        init()
    }
}