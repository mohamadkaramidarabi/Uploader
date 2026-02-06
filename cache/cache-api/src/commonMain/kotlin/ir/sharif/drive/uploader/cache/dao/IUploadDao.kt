package ir.sharif.drive.uploader.cache.dao

import ir.sharif.drive.uploader.cache.entity.ILinkEntity
import ir.sharif.drive.uploader.cache.entity.IUploadEntity
import ir.sharif.drive.uploader.cache.entity.IUploadWithLinks
import ir.sharif.drive.uploader.cache.entity.UploadItem
import ir.sharif.drive.uploader.models.States
import kotlinx.coroutines.flow.Flow

interface IUploadDao<T: IUploadEntity,L: ILinkEntity,UL: IUploadWithLinks<T,L>> {

    suspend fun insertAll(vararg uploadEntity: T)

    suspend fun update(uploadEntity: T)

    suspend fun updateLink(updateLink: L)

    suspend fun updateWithLinks(uploadWithLinks: UL)

    fun getUploadWithLinksFlow(state: States.UploadInfo.State): Flow<UL?>
    fun getUploadInfosFlow(state: States.UploadInfo.State): Flow<List<UL>>

    suspend fun deleteLinksByUploadId(uploadId: Long)

    fun getUploadByStateFlow(state: States.UploadInfo.State): Flow<T?>
    
    suspend fun getUploadById(id: Long): UL?

    fun allFlow(): Flow<List<UL>>

    fun firstInQueueOrFailedLink(runningLimitCount: Int): Flow<L?>

    fun firstLinkByState(state: States.Link.State): Flow<L?>

    fun firstUploadInfoWithStateAndAllLinkWithState(
        state: States.UploadInfo.State,
        linkState: States.Link.State
    ): Flow<T?>

    suspend fun cancel(id: Long)

    suspend fun updateUploadStateById(id: Long,state: States.UploadInfo.State)

    suspend fun init()

    suspend fun updateStates(
        id: Long,
        preState: States.UploadInfo.State,
        state: States.UploadInfo.State,
        preLinkState: States.Link.State,
        linkState: States.Link.State
    )

    suspend fun pause(id: Long)

    fun uploadItems(): Flow<List<UploadItem>>
    fun uploadItemsByState(state: States.UploadInfo.State): Flow<List<UploadItem>>

    suspend fun deleteAll()
}