package ir.sharif.drive.uploader.api

import kotlin.concurrent.Volatile

internal object UploadEngineHolder {
    @Volatile
    var uploader: IUploader? = null
        private set

    fun register(uploader: IUploader) {
        this.uploader = uploader
    }
}
