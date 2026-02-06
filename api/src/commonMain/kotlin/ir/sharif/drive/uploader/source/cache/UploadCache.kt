package ir.sharif.drive.uploader.source.cache

import ir.sharif.drive.uploader.cache.dao.IUploadDao
import ir.sharif.drive.uploader.cache.entity.ILinkEntity
import ir.sharif.drive.uploader.cache.entity.IUploadEntity
import ir.sharif.drive.uploader.cache.entity.IUploadWithLinks
import ir.sharif.drive.uploader.cache.entity.UploadItem
import ir.sharif.drive.uploader.mapper.infoFlow
import ir.sharif.drive.uploader.mapper.toLinkEntity
import ir.sharif.drive.uploader.mapper.toLinkFlow
import ir.sharif.drive.uploader.mapper.toUploadEntity
import ir.sharif.drive.uploader.mapper.toUploadInfo
import ir.sharif.drive.uploader.mapper.toUploadInfoFlow
import ir.sharif.drive.uploader.mapper.toUploadInfosFlow
import ir.sharif.drive.uploader.mapper.toUploadWithLinks
import ir.sharif.drive.uploader.models.States
import ir.sharif.drive.uploader.models.UploadInfo
import kotlinx.coroutines.flow.Flow

internal object UploadCache : IUploadCache {
    private val uploadDao: IUploadDao<IUploadEntity, ILinkEntity, IUploadWithLinks<IUploadEntity, ILinkEntity>> =
        ir.sharif.drive.uploader.source.cache.uploadDao

    override suspend fun init() {
        uploadDao.init()
    }

    override suspend fun insertAll(uploadInfos: List<UploadInfo>) {
        uploadDao.insertAll(
            *uploadInfos.map {
                it.toUploadEntity
            }.toTypedArray()
        )
    }

    override fun getUploadInfoByState(state: States.UploadInfo.State): Flow<UploadInfo?> = uploadDao
        .getUploadByStateFlow(state)
        .toUploadInfoFlow

    override suspend fun update(uploadInfo: UploadInfo) {
        uploadDao.update(uploadInfo.toUploadEntity)
    }

    override suspend fun updateWithLinks(uploadInfo: UploadInfo) {
        uploadDao.updateWithLinks(uploadInfo.toUploadWithLinks)
    }

    override suspend fun updateStates(
        id: Long,
        preState: States.UploadInfo.State,
        state: States.UploadInfo.State,
        preLinkState: States.Link.State,
        linkState: States.Link.State
    ) = uploadDao.updateStates(
        id = id,
        preState = preState,
        state = state,
        preLinkState = preLinkState,
        linkState = linkState,
    )

    override suspend fun updateLink(link: UploadInfo.Link) {
        uploadDao.updateLink(link.toLinkEntity)
    }

    override suspend fun cancel(id: Long) = uploadDao.cancel(id)

    override suspend fun getUploadInfoById(id: Long): UploadInfo? {
        return uploadDao.getUploadById(id)?.toUploadInfo
    }

    override val firstInQueueOrFailedLink: Flow<UploadInfo.Link?> by lazy {
        uploadDao
            .firstInQueueOrFailedLink(10)
            .toLinkFlow
    }

    override suspend fun firstLinkByState(state: States.Link.State): Flow<UploadInfo.Link?> =
        uploadDao
            .firstLinkByState(state)
            .toLinkFlow

    override fun allUpload(): Flow<List<UploadInfo>> = uploadDao
        .allFlow()
        .toUploadInfosFlow

    override fun allUploadInfoByState(state: States.UploadInfo.State): Flow<List<UploadInfo>> =
        uploadDao.getUploadInfosFlow(state)
            .toUploadInfosFlow

    override val firstAllPutDone: Flow<UploadInfo?> by lazy {
        uploadDao
            .firstUploadInfoWithStateAndAllLinkWithState(
                state = States.UploadInfo.State.UPLOADING,
                linkState = States.Link.State.SUCCESS
            )
            .toUploadInfoFlow
    }

    override fun getUploadInfoWitLinksByState(state: States.UploadInfo.State): Flow<UploadInfo?> =
        uploadDao.getUploadWithLinksFlow(state)
            .infoFlow

    override suspend fun pause(id: Long) = uploadDao.pause(id)

    override val allUploadsItem: Flow<List<UploadItem>> by lazy {
        uploadDao.uploadItems()
    }

    override suspend fun deleteAll() {
        uploadDao.deleteAll()
    }
}