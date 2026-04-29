package ir.sharif.drive.uploader.source.cache

import ir.sharif.drive.uploader.cache.dao.IUploadDao
import ir.sharif.drive.uploader.cache.entity.ILinkEntity
import ir.sharif.drive.uploader.cache.entity.IUploadEntity
import ir.sharif.drive.uploader.cache.entity.IUploadWithLinks
import ir.sharif.drive.uploader.cache.entity.UploadItem
import ir.sharif.drive.uploader.models.States
import ir.sharif.drive.uploader.models.UploadInfo
import kotlinx.coroutines.flow.Flow


internal expect val uploadDao: IUploadDao<IUploadEntity, ILinkEntity, IUploadWithLinks<IUploadEntity, ILinkEntity>>

internal interface IUploadCache {

    companion object {
        operator fun invoke(): IUploadCache = UploadCache
    }

    suspend fun insertAll(uploadInfos: List<UploadInfo>)

    fun getUploadInfoByState(state: States.UploadInfo.State): Flow<UploadInfo?>
    fun getUploadInfoWitLinksByState(state: States.UploadInfo.State): Flow<UploadInfo?>

    fun allUpload(): Flow<List<UploadInfo>>
    fun allUploadInfoByState(state: States.UploadInfo.State): Flow<List<UploadInfo>>

    suspend fun update(uploadInfo: UploadInfo)
    suspend fun updateWithLinks(uploadInfo: UploadInfo)
    suspend fun updateStates(
        id: Long,
        preState: States.UploadInfo.State,
        state: States.UploadInfo.State,
        preLinkState: States.Link.State,
        linkState: States.Link.State
    )
    suspend fun updateLink(link: UploadInfo.Link)

    suspend fun cancel(id: Long)
    
    suspend fun getUploadInfoById(id: Long): UploadInfo?
    suspend fun init()

    suspend fun firstLinkByState(state: States.Link.State): Flow<UploadInfo.Link?>
    suspend fun pause(id: Long)

    suspend fun deleteAll()

    suspend fun deleteLinksOfUpload(uploadId: Long)

    suspend fun updateStateWithId(id: Long, state: States.UploadInfo.State)

    val firstInQueueOrFailedLink: Flow<UploadInfo.Link?>

    val firstAllPutDone: Flow<UploadInfo?>

    val allUploadsItem: Flow<List<UploadItem>>

}