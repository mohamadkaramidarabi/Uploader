package ir.sharif.drive.uploader.source.network

internal interface INetworkApi {

    suspend fun startUpload()
    suspend fun putChunk()
    suspend fun completeUpload()
}