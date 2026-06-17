package ir.sharif.drive.uploader.mapper

import ir.sharif.drive.uploader.cache.entity.ILinkEntity
import ir.sharif.drive.uploader.cache.entity.IUploadEntity
import ir.sharif.drive.uploader.cache.entity.createWebLinkEntity
import ir.sharif.drive.uploader.cache.entity.createWebUploadEntity
import ir.sharif.drive.uploader.models.UploadInfo

internal actual val UploadInfo.toUploadEntity: IUploadEntity
    get() = createWebUploadEntity(
        id = id,
        serverId = uploadId?.value,
        key = key?.value,
        fileName = name.value,
        filePath = path.value,
        fileSize = size.value,
        chunkSize = chunkSize?.value,
        chunkCount = chunkCount?.value,
        state = state,
        folderId = folderId?.value,
        versionGroup = versionGroup,
        cloudPath = cloudPath.value,
        metaData = metaData,
    )

internal actual val UploadInfo.Link.toLinkEntity: ILinkEntity
    get() = createWebLinkEntity(
        id = id,
        uploadId = uploadId,
        eTag = eTag,
        state = state,
        retryCount = retryCount,
        chunkSize = size,
        url = url,
    )
