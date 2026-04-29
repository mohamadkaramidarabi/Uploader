package ir.sharif.drive.uploader.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.sharif.drive.uploader.cache.entity.IUploadEntity
import ir.sharif.drive.uploader.models.States


@Entity(tableName = "uploads")
internal data class UploadEntity(
    @ColumnInfo(name = "upload_id")
    @PrimaryKey(autoGenerate = true)
    override val id: Long,
    @ColumnInfo(name = "upload_server_id")
    override val serverId: String?,
    @ColumnInfo(name = "upload_key")
    override val key: String?,
    @ColumnInfo(name = "upload_file_name")
    override val fileName: String,
    @ColumnInfo(name = "upload_file_path")
    override val filePath: String,
    @ColumnInfo(name = "upload_file_size")
    override val fileSize: Long,
    @ColumnInfo(name = "upload_chunk_size")
    override val chunkSize: Long?,
    @ColumnInfo(name = "upload_chunk_count")
    override val chunkCount: Int?,
    @ColumnInfo(name = "upload_state")
    override val state: States.UploadInfo.State,
    @ColumnInfo("upload_folder_id")
    override val folderId: String?,
    @ColumnInfo("upload_version_group")
    override val versionGroup: String?,
    @ColumnInfo("upload_cloud_path")
    override val cloudPath: String,
    @ColumnInfo("meta_data")
    override val metaData: String?
): IUploadEntity


fun createUploadEntity(
    id: Long,
    serverId: String?,
    key: String?,
    fileName: String,
    filePath: String,
    fileSize: Long,
    chunkSize: Long?,
    chunkCount: Int?,
    state: States.UploadInfo.State,
    folderId: String?,
    versionGroup: String?,
    cloudPath: String,
    metaData: String?,
): IUploadEntity = UploadEntity(
    id = id,
    serverId = serverId,
    key = key,
    fileName = fileName,
    filePath = filePath,
    fileSize = fileSize,
    chunkSize = chunkSize,
    chunkCount = chunkCount,
    state = state,
    folderId = folderId,
    versionGroup = versionGroup,
    cloudPath = cloudPath,
    metaData = metaData,
)