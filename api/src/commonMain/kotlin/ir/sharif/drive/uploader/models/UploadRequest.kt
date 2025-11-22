package ir.sharif.drive.uploader.models

data class UploadRequest(
    val fileName: FileName,
    val filePath: FilePath,
    val fileSize: FileSize,
    val folderId: FolderId?,
    val cloudPath: CloudPath,
)