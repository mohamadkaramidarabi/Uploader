package ir.sharif.drive.uploader.api

import ir.sharif.drive.uploader.models.States
import ir.sharif.drive.uploader.models.UploadInfo

internal fun States.UploadInfo.State.isUploadSettled(): Boolean = when (this) {
    States.UploadInfo.State.SUCCESS,
    States.UploadInfo.State.FAILED,
    States.UploadInfo.State.CANCELED,
    -> true
    else -> false
}

internal fun List<UploadInfo>.allUploadsSettled(): Boolean =
    isNotEmpty() && all { it.state.isUploadSettled() }

internal fun States.UploadInfo.State.isEngineActive(): Boolean = when (this) {
    States.UploadInfo.State.IN_QUEUE,
    States.UploadInfo.State.PREPARING,
    States.UploadInfo.State.PREPARED,
    States.UploadInfo.State.STARTING,
    States.UploadInfo.State.STARTED,
    States.UploadInfo.State.UPLOADING,
    States.UploadInfo.State.ALL_PUT_DONE,
    States.UploadInfo.State.COMPLETING,
    -> true
    else -> false
}

internal fun UploadInfo.uploadProgress(): Pair<Int, Int>? {
    val max = chunkCount?.value ?: return null
    if (max <= 0) return null
    val current = links.count { it.state == States.Link.State.SUCCESS }
    return current to max
}
