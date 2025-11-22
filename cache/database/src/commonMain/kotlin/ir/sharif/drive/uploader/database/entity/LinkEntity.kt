package ir.sharif.drive.uploader.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ir.sharif.drive.uploader.cache.entity.ILinkEntity
import ir.sharif.drive.uploader.models.States


@Entity(
    tableName = "upload_link",
    foreignKeys = [
        ForeignKey(
            entity = UploadEntity::class,
            parentColumns = arrayOf("upload_id"),
            childColumns = arrayOf("link_upload_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class LinkEntity(
    @ColumnInfo(name = "link_id")
    @PrimaryKey(autoGenerate = true)
    override val id: Long,
    @ColumnInfo("link_upload_id")
    override val uploadId: Long,
    @ColumnInfo(name = "link_e_tag")
    override val eTag: String?,
    @ColumnInfo(name = "link_state")
    override val state: States.Link.State,
    @ColumnInfo(name = "link_retry_count")
    override val retryCount: Int,
    @ColumnInfo(name = "link_chunk_size")
    override val chunkSize: Long,
    @ColumnInfo(name = "link_url")
    override val url: String
): ILinkEntity

fun createLinkEntity(
    id: Long,
    uploadId: Long,
    eTag: String?,
    state: States.Link.State,
    retryCount: Int,
    chunkSize: Long,
    url: String
) : ILinkEntity = LinkEntity(
    id = id,
    uploadId = uploadId,
    eTag = eTag,
    state = state,
    retryCount = retryCount,
    chunkSize = chunkSize,
    url = url,
)