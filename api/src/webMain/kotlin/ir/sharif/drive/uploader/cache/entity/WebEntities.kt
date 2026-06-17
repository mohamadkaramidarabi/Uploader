package ir.sharif.drive.uploader.cache.entity

import ir.sharif.drive.uploader.models.States

internal data class WebUploadEntity(
    override val id: Long,
    override val serverId: String?,
    override val key: String?,
    override val fileName: String,
    override val filePath: String,
    override val fileSize: Long,
    override val chunkSize: Long?,
    override val chunkCount: Int?,
    override val state: States.UploadInfo.State,
    override val folderId: String?,
    override val versionGroup: String?,
    override val cloudPath: String,
    override val metaData: String?,
) : IUploadEntity

internal data class WebLinkEntity(
    override val id: Long,
    override val uploadId: Long,
    override val eTag: String?,
    override val state: States.Link.State,
    override val retryCount: Int,
    override val chunkSize: Long,
    override val url: String,
) : ILinkEntity

internal data class WebUploadWithLinks(
    override val upload: WebUploadEntity,
    override val links: List<WebLinkEntity>,
) : IUploadWithLinks<WebUploadEntity, WebLinkEntity>

internal fun createWebUploadEntity(
    id: Long,
    serverId: String?,
    key: String?,
    fileName: String,
    filePath: String,
    fileSize: Long,
    chunkSize: Long?,
    chunkCount: Int?,
    state: States.UploadInfo.State,
    folderId: String?,
    versionGroup: String?,
    cloudPath: String,
    metaData: String?,
): IUploadEntity = WebUploadEntity(
    id = id,
    serverId = serverId,
    key = key,
    fileName = fileName,
    filePath = filePath,
    fileSize = fileSize,
    chunkSize = chunkSize,
    chunkCount = chunkCount,
    state = state,
    folderId = folderId,
    versionGroup = versionGroup,
    cloudPath = cloudPath,
    metaData = metaData,
)

internal fun createWebLinkEntity(
    id: Long,
    uploadId: Long,
    eTag: String?,
    state: States.Link.State,
    retryCount: Int,
    chunkSize: Long,
    url: String,
): ILinkEntity = WebLinkEntity(
    id = id,
    uploadId = uploadId,
    eTag = eTag,
    state = state,
    retryCount = retryCount,
    chunkSize = chunkSize,
    url = url,
)

internal fun createWebUploadWithLinks(
    upload: IUploadEntity,
    links: List<ILinkEntity>,
): IUploadWithLinks<IUploadEntity, ILinkEntity> = object : IUploadWithLinks<IUploadEntity, ILinkEntity> {
    override val upload: IUploadEntity = upload
    override val links: List<ILinkEntity> = links
}
