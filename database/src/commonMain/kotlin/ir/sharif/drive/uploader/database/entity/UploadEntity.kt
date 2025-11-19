package ir.sharif.drive.uploader.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.sharif.drive.uploader.models.ChunkCount
import ir.sharif.drive.uploader.models.ChunkSize
import ir.sharif.drive.uploader.models.CloudPath
import ir.sharif.drive.uploader.models.CouldKey
import ir.sharif.drive.uploader.models.FileName
import ir.sharif.drive.uploader.models.FilePath
import ir.sharif.drive.uploader.models.FileSize
import ir.sharif.drive.uploader.models.FolderId
import ir.sharif.drive.uploader.models.UploadId
import ir.sharif.drive.uploader.models.UploadInfo.Link
import ir.sharif.drive.uploader.models.UploadInfo.State

//val id: Long,
//val uploadId: UploadId?,
//val key: CouldKey?,
//val name: FileName,
//val path: FilePath,
//val size: FileSize,
//val chunkSize: ChunkSize?,
//val chunkCount: ChunkCount?,
//val state: State,
//val folderId: FolderId?,
//val cloudPath: CloudPath,
//val links: List<Link>,
//val versionGroup: String?,

@Entity(tableName = "uploads")
data class UploadEntity(
    @ColumnInfo(name = "upload_id")
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "upload_server_id")
    val serverId: String?,
    @ColumnInfo(name = "upload_key")
    val key: String?,
    @ColumnInfo(name = "upload_file_name")
    val fileName: String,
    @ColumnInfo(name = "upload_file_path")
    val filePath: String,
    @ColumnInfo(name = "upload_file_size")
    val fileSize: Long,
    @ColumnInfo(name = "upload_chunk_size")
    val chunkSize: Long?,
    @ColumnInfo(name = "upload_chunk_count")
    val chunkCount: Int?,
    @ColumnInfo(name = "upload_state")
    val state: Int,
    @ColumnInfo("upload_folder_id")
    val folderId: String?,
    @ColumnInfo("upload_version_group")
    val versionGroup: String?,
    @ColumnInfo("upload_cloud_path")
    val cloudPath: String
)