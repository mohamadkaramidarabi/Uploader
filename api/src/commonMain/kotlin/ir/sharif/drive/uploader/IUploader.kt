package ir.sharif.drive.uploader

import io.ktor.client.HttpClient
import ir.sharif.drive.uploader.models.CloudPath
import ir.sharif.drive.uploader.models.FileName
import ir.sharif.drive.uploader.models.FilePath
import ir.sharif.drive.uploader.models.FileSize
import ir.sharif.drive.uploader.models.FolderId

interface IUploader {

    fun init(
        httpClient: HttpClient,
        startEndPoint: String,
        completeEndPoint: String
    )

    suspend fun upload(
        filePath: FilePath,
        name: FileName,
        cloudPath: CloudPath,
        folderId: FolderId,
        size: FileSize,
    )

}