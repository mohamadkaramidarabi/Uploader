package ir.sharif.drive.uploader.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "upload_link")
data class LinkEntity(
    @ColumnInfo(name = "link_id")
    @PrimaryKey
    val id: String,
    @ColumnInfo("link_upload_id")
    val uploadId: String,
    @ColumnInfo(name = "link_e_tag")
    val eTag: String?,
    @ColumnInfo(name = "link_state")
    val state: Int,
    @ColumnInfo(name = "link_retry_count")
    val retryCount: Int,
    @ColumnInfo(name = "link_chunk_size")
    val chunkSize: Long
)