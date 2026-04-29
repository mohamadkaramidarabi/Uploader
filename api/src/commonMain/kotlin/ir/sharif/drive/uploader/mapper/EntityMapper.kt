package ir.sharif.drive.uploader.mapper

import ir.sharif.drive.uploader.cache.entity.ILinkEntity
import ir.sharif.drive.uploader.cache.entity.IUploadEntity
import ir.sharif.drive.uploader.cache.entity.IUploadWithLinks
import ir.sharif.drive.uploader.models.ChunkCount.Companion.chunkCount
import ir.sharif.drive.uploader.models.ChunkSize.Companion.chunkSize
import ir.sharif.drive.uploader.models.CloudKey.Companion.cloudKey
import ir.sharif.drive.uploader.models.CloudPath.Companion.cloudPath
import ir.sharif.drive.uploader.models.FileName.Companion.fileName
import ir.sharif.drive.uploader.models.FilePath.Companion.filePath
import ir.sharif.drive.uploader.models.FileSize.Companion.fileSize
import ir.sharif.drive.uploader.models.FolderId.Companion.folderId
import ir.sharif.drive.uploader.models.UploadId.Companion.uploadId
import ir.sharif.drive.uploader.models.UploadInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal expect val UploadInfo.toUploadEntity: IUploadEntity
internal expect val UploadInfo.Link.toLinkEntity: ILinkEntity
internal val UploadInfo.toUploadWithLinks: IUploadWithLinks<IUploadEntity, ILinkEntity>
    get() = object : IUploadWithLinks<IUploadEntity, ILinkEntity> {
        override val upload: IUploadEntity = this@toUploadWithLinks.toUploadEntity
        override val links: List<ILinkEntity> = this@toUploadWithLinks.links.map { it.toLinkEntity }
    }

internal val ILinkEntity.toLink: UploadInfo.Link
    get() = UploadInfo.Link(
        id = id,
        uploadId = uploadId,
        eTag = eTag,
        state = state,
        retryCount = retryCount,
        size = chunkSize,
        url = url,
    )

internal val Flow<ILinkEntity?>.toLinkFlow: Flow<UploadInfo.Link?>
    get() =  map { it?.toLink }

internal val List<ILinkEntity>.toLinks: List<UploadInfo.Link>
    get() =  map { it.toLink }
internal val IUploadEntity.toUploadInfo: UploadInfo
    get() = UploadInfo(
        id = id,
        uploadId = serverId.uploadId,
        key = key.cloudKey,
        name = fileName.fileName,
        path = filePath.filePath,
        size = fileSize.fileSize,
        chunkSize = chunkSize.chunkSize,
        chunkCount = chunkCount.chunkCount,
        state = state,
        folderId = folderId.folderId,
        cloudPath = cloudPath.cloudPath,
        links = emptyList(),
        versionGroup = versionGroup,
        metaData = metaData
    )

internal val Flow<IUploadEntity?>.toUploadInfoFlow: Flow<UploadInfo?>
    get() = map { it?.toUploadInfo }

internal val List<IUploadEntity>.toUploadInfoList: List<UploadInfo>
    get() = map { it.toUploadInfo }

internal val Flow<List<IUploadEntity>>.toUploadInfoListFlow: Flow<List<UploadInfo>>
    get() = map {
        it.toUploadInfoList
    }

internal val IUploadWithLinks<IUploadEntity, ILinkEntity>.toUploadInfo: UploadInfo
    get() = upload.toUploadInfo
        .copy(links = links.map { it.toLink })

internal val Flow<IUploadWithLinks<IUploadEntity, ILinkEntity>?>.infoFlow: Flow<UploadInfo?>
    get() = map { it?.toUploadInfo }

internal val List<IUploadWithLinks<IUploadEntity, ILinkEntity>>.toUploadInfos: List<UploadInfo>
    get() = this.map {
        it.toUploadInfo
    }

internal val Flow<List<IUploadWithLinks<IUploadEntity, ILinkEntity>>>.toUploadInfosFlow: Flow<List<UploadInfo>>
    get() = this.map {
        it.toUploadInfos
    }