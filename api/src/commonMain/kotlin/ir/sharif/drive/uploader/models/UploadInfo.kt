package ir.sharif.drive.uploader.models


data class UploadInfo(
    val id: Long,
    val uploadId: UploadId?,
    val key: CloudKey?,
    val name: FileName,
    val path: FilePath,
    val size: FileSize,
    val chunkSize: ChunkSize?,
    val chunkCount: ChunkCount?,
    val state: States.UploadInfo.State,
    val folderId: FolderId?,
    val cloudPath: CloudPath,
    val links: List<Link>,
    val versionGroup: String?,
){
    data class Link(
        val id: Long,
        val uploadId: Long,
        val eTag: String?,
        val state: States.Link.State,
        val retryCount: Int,
        val size: Long,
        val url: String,
    )
}
