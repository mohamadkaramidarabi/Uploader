package ir.sharif.drive.uploader.models

import kotlin.jvm.JvmInline

@JvmInline
value class UploadId(val value: String) {
    companion object {
        val String.uploadId: UploadId
            get() = UploadId(this)
        val String?.uploadId: UploadId?
            get() = this?.uploadId
    }
}

@JvmInline
value class CloudKey(val value: String){
    companion object {
        val String.cloudKey: CloudKey
            get() = CloudKey(this)
        val String?.cloudKey: CloudKey?
            get() = this?.cloudKey
    }
}


@JvmInline
value class FolderId(val value: String) {
    companion object {
        val String.folderId: FolderId
            get() = FolderId(this)
        val String?.folderId: FolderId?
            get() = this?.folderId
    }
}

@JvmInline
value class LInkId(val value: String){
    companion object {
        val String.lInkId: LInkId
            get() = LInkId(this)
        val String?.lInkId: LInkId?
            get() = this?.lInkId
    }

}