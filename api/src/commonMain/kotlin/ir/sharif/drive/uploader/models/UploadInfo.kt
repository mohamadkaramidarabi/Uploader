package ir.sharif.drive.uploader.models


data class UploadInfo(
    val id: Long,
    val uploadId: UploadId? = null,
    val key: CloudKey? = null,
    val name: FileName,
    val path: FilePath,
    val size: FileSize,
    val chunkSize: ChunkSize? = null,
    val chunkCount: ChunkCount? = null,
    val state: States.UploadInfo.State = States.UploadInfo.State.IN_QUEUE,
    val folderId: FolderId?,
    val cloudPath: CloudPath,
    val links: List<Link> = emptyList(),
    val versionGroup: String?,
    val metaData: String?,
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
