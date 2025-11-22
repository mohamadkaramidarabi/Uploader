@file:Suppress("UNCHECKED_CAST")

package ir.sharif.drive.uploader.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ir.sharif.drive.uploader.cache.dao.IUploadDao
import ir.sharif.drive.uploader.cache.entity.ILinkEntity
import ir.sharif.drive.uploader.cache.entity.IUploadEntity
import ir.sharif.drive.uploader.cache.entity.IUploadWithLinks
import ir.sharif.drive.uploader.cache.entity.UploadItem
import ir.sharif.drive.uploader.database.UploadDatabase
import ir.sharif.drive.uploader.database.entity.LinkEntity
import ir.sharif.drive.uploader.database.entity.UploadEntity
import ir.sharif.drive.uploader.database.entity.UploadWithLinks
import ir.sharif.drive.uploader.database.entity.createUploadWithLinks
import ir.sharif.drive.uploader.database.entity.toGenericFlow
import ir.sharif.drive.uploader.database.entity.toGenericListFlow
import ir.sharif.drive.uploader.models.States
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


/**
 * Wrapper adapter that converts IUploadEntity to UploadEntity for Room compatibility
 */
private class UploadDaoAdapter(
    private val uploadDao: UploadDao
) : IUploadDao<IUploadEntity, ILinkEntity, IUploadWithLinks<IUploadEntity, ILinkEntity>> {

    override suspend fun insertAll(vararg uploadEntity: IUploadEntity) {
        val uploadEntities = uploadEntity.map { it as UploadEntity }.toTypedArray()
        uploadDao.insertAll(*uploadEntities)
    }

    override suspend fun update(uploadEntity: IUploadEntity) {
        val entity = uploadEntity as UploadEntity
        uploadDao.update(entity)
    }

    override suspend fun updateLink(updateLink: ILinkEntity) {
        uploadDao.updateLink(updateLink as LinkEntity)
    }

    override suspend fun updateWithLinks(uploadWithLinks: IUploadWithLinks<IUploadEntity, ILinkEntity>) {
        uploadDao.updateWithLinks(
            UploadWithLinks(
                upload = uploadWithLinks.upload as UploadEntity,
                links = uploadWithLinks.links as List<LinkEntity>
            )
        )
    }

    override fun getUploadWithLinksFlow(state: States.UploadInfo.State) =
        uploadDao.getUploadWithLinksFlow(state)
            .toGenericFlow

    override fun getUploadInfosFlow(state: States.UploadInfo.State) =
        uploadDao.getUploadInfosFlow(state)
            .toGenericListFlow


    override suspend fun deleteLinksByUploadId(uploadId: Long) {
        uploadDao.deleteLinksByUploadId(uploadId)
    }

    override fun getUploadByStateFlow(state: States.UploadInfo.State): Flow<IUploadEntity?> =
        uploadDao.getUploadByStateFlow(state)
            .map { it as? IUploadEntity }

    override suspend fun getUploadById(id: Long): IUploadWithLinks<IUploadEntity, ILinkEntity>? =
        uploadDao.getUploadById(id)?.let {
            createUploadWithLinks(it.upload, it.links)
        }

    override fun allFlow(): Flow<List<IUploadWithLinks<IUploadEntity, ILinkEntity>>> {
        return uploadDao.allFlow()
            .toGenericListFlow
    }

    override fun firstInQueueOrFailedLink(runningLimitCount: Int): Flow<ILinkEntity?> = uploadDao
        .firstInQueueOrFailedLink(runningLimitCount)


    override fun firstLinkByState(state: States.Link.State): Flow<ILinkEntity?> {
        return uploadDao.firstLinkByState(state)
    }


    override fun firstUploadInfoWithStateAndAllLinkWithState(
        state: States.UploadInfo.State,
        linkState: States.Link.State
    ): Flow<IUploadEntity?> =
        uploadDao.firstUploadInfoWithStateAndAllLinkWithState(state, linkState)

    override suspend fun cancel(id: Long) = uploadDao.cancel(id)
    override suspend fun updateUploadStateById(id: Long, state: States.UploadInfo.State) =
        uploadDao.updateUploadStateById(id, state)

    override suspend fun init() {
        uploadDao.init()
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

    override suspend fun pause(id: Long) = uploadDao.pause(id)
    override fun uploadItems(): Flow<List<UploadItem>> = uploadDao.uploadItems()

    override fun uploadItemsByState(state: States.UploadInfo.State): Flow<List<UploadItem>> =
        uploadDao.uploadItemsByState(state)
}

val uploadDao: IUploadDao<IUploadEntity, ILinkEntity, IUploadWithLinks<IUploadEntity, ILinkEntity>> =
    UploadDaoAdapter(UploadDatabase.instance.uploadDao())


@Dao
internal interface UploadDao : IUploadDao<UploadEntity, LinkEntity, UploadWithLinks> {

    companion object {
        const val IN_QUEUE = "IN_QUEUE"
        const val RUNNING = "RUNNING"
        const val FAILED = "FAILED"
        const val UPLOADING = "UPLOADING"
        const val PAUSE = "PAUSED"
        const val CANCELED = "CANCELED"
    }

    @Insert
    override suspend fun insertAll(vararg uploadEntity: UploadEntity)

    @Transaction
    suspend fun updateWitLinks(uploadWithLinks: UploadWithLinks) {
        update(uploadWithLinks.upload)
        insertAllLinks(*uploadWithLinks.links.toTypedArray())
    }

    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun update(uploadEntity: UploadEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun updateLink(updateLink: LinkEntity)


    override suspend fun updateWithLinks(uploadWithLinks: UploadWithLinks) {
        updateWitLinks(uploadWithLinks)
    }

    @Transaction
    @Query("select * from uploads where upload_state = :state order by upload_id asc limit 1")
    override fun getUploadWithLinksFlow(state: States.UploadInfo.State): Flow<UploadWithLinks?>


    @Query("delete from upload_link where link_upload_id = :uploadId")
    override suspend fun deleteLinksByUploadId(uploadId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLinks(vararg links: LinkEntity)

    @Transaction
    @Query("select * from uploads")
    override fun allFlow(): Flow<List<UploadWithLinks>>


    @Query("select * from uploads where upload_state = :state order by upload_id asc limit 1")
    override fun getUploadByStateFlow(state: States.UploadInfo.State): Flow<UploadEntity?>

    @Transaction
    @Query("select * from uploads where upload_id = :id")
    override suspend fun getUploadById(id: Long): UploadWithLinks?

    @Query(
        "SELECT * FROM upload_link " +
                "WHERE link_state IN ('$IN_QUEUE','$FAILED')" +
                "  AND (" +
                "        (link_state = '$IN_QUEUE' AND" +
                "         (SELECT COUNT(1) FROM upload_link WHERE link_state = '$RUNNING') < :runningLimitCount)" +
                "        OR link_state = '$FAILED'" +
                "      )" +
                "ORDER BY link_id ASC LIMIT 1"
    )
    override fun firstInQueueOrFailedLink(runningLimitCount: Int): Flow<LinkEntity?>


    @Query(
        "select * from uploads " +
                "where upload_state = :state and " +
                "upload_chunk_count = (select count(1) from upload_link " +
                "where link_upload_id=upload_id and link_state = :linkState)"
    )
    override fun firstUploadInfoWithStateAndAllLinkWithState(
        state: States.UploadInfo.State,
        linkState: States.Link.State
    ): Flow<UploadEntity?>

    @Transaction
    @Query("select * from uploads where upload_state = :state")
    override fun getUploadInfosFlow(state: States.UploadInfo.State): Flow<List<UploadWithLinks>>


    @Transaction
    override suspend fun cancel(id: Long) {
        deleteLinksByUploadId(id)
        updateUploadStateById(id, States.UploadInfo.State.CANCELED)
    }

    @Query("Update uploads set upload_state=:state where upload_id=:id")
    override suspend fun updateUploadStateById(id: Long, state: States.UploadInfo.State)


    @Transaction
    override suspend fun init() {
        updateLinkStateByState(
            newState = States.Link.State.IN_QUEUE,
            oldState = States.Link.State.RUNNING
        )
        updateStateByState(
            newState = States.UploadInfo.State.PREPARED,
            oldState = States.UploadInfo.State.STARTING,
        )
        updateStateByState(
            newState = States.UploadInfo.State.ALL_PUT_DONE,
            oldState = States.UploadInfo.State.COMPLETING
        )

    }

    @Query("update uploads set upload_state=:newState where upload_state =:oldState")
    suspend fun updateStateByState(
        newState: States.UploadInfo.State,
        oldState: States.UploadInfo.State
    )

    @Query("update upload_link set link_state=:newState where link_state =:oldState")
    suspend fun updateLinkStateByState(newState: States.Link.State, oldState: States.Link.State)

    @Query("select * from upload_link where link_state=:state")
    override fun firstLinkByState(state: States.Link.State): Flow<LinkEntity?>

    @Query("update uploads set upload_state=:state where upload_id=:id and upload_state=:preState")
    suspend fun updateState(
        id: Long,
        preState: States.UploadInfo.State,
        state: States.UploadInfo.State
    )

    @Query("update upload_link set link_state=:state where link_upload_id=:id and link_state+:preState")
    suspend fun updateLinkState(id: Long, preState: States.Link.State, state: States.Link.State)

    @Transaction
    override suspend fun updateStates(
        id: Long,
        preState: States.UploadInfo.State,
        state: States.UploadInfo.State,
        preLinkState: States.Link.State,
        linkState: States.Link.State
    ) {
        updateState(id, preState, state)
        updateLinkState(id, preLinkState, linkState)
    }

    @Query("update uploads set upload_state='$PAUSE' where upload_id=:id")
    suspend fun pauseUploadInfo(id: Long)

    @Query("update upload_link set link_state='$PAUSE' where link_upload_id=:id " +
            "and link_state in ('$RUNNING','$IN_QUEUE')")
    suspend fun pauseUploadLink(id: Long)

    @Transaction
    override suspend fun pause(id: Long) {
        pauseUploadInfo(id)
        pauseUploadLink(id)
    }

    @Query("select upload_id as id, upload_file_name as name, " +
            "((select count(1) from upload_link " +
            "where link_upload_id=upload_id and link_state='SUCCESS') / upload_chunk_count) as progress, " +
            "upload_file_size as size, upload_state as state from uploads")
    override fun uploadItems(): Flow<List<UploadItem>>


    @Query("select upload_id as id, upload_file_name as name, " +
            "((select count(1) from upload_link " +
            "where link_upload_id=upload_id and link_state='SUCCESS') / upload_chunk_count) as progress, " +
            "upload_file_size as size, upload_state as state from uploads " +
            "where upload_state = :state")
    override fun uploadItemsByState(state: States.UploadInfo.State): Flow<List<UploadItem>>

}