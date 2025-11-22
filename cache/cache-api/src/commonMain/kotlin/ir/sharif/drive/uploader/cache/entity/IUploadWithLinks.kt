package ir.sharif.drive.uploader.cache.entity


interface IUploadWithLinks<T : IUploadEntity,L: ILinkEntity> {
    val upload: T
    val links: List<L>
}