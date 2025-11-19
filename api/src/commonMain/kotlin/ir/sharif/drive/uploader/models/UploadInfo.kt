package ir.sharif.drive.uploader.models



data class UploadInfo(
    val id: Long,
    val uploadId: UploadId?,
    val key: CouldKey?,
    val name: FileName,
    val path: FilePath,
    val size: FileSize,
    val chunkSize: ChunkSize?,
    val chunkCount: ChunkCount?,
    val state: State,
    val folderId: FolderId?,
    val cloudPath: CloudPath,
    val links: List<Link>,
    val versionGroup: String,
) {
    enum class State {
        IN_QUEUE,
        PREPARING,
        PREPARED,
        STARTING,
        STARTED,
        UPLOADING,
        CANCELED,
        PAUSED,
        ALL_PUT_DONE,
        COMPLETING,
        SUCCESS,
        FAILED
    }

    data class Link(
        val id: LInkId,
        val uploadId: UploadId,
        val eTag: String?,
        val state: UploadLinkState,
        val retryCount: Int,
    ) {
        enum class UploadLinkState {
            IN_QUEUE,
            RUNNING,
            SUCCESS,
            FAILED,
            PAUSED;
        }
    }
}
