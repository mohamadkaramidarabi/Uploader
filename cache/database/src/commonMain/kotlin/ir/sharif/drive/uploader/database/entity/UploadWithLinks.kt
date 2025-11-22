package ir.sharif.drive.uploader.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import ir.sharif.drive.uploader.cache.entity.ILinkEntity
import ir.sharif.drive.uploader.cache.entity.IUploadEntity
import ir.sharif.drive.uploader.cache.entity.IUploadWithLinks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal data class UploadWithLinks(
    @Embedded override val upload: UploadEntity,
    @Relation(
        parentColumn = "upload_id",
        entityColumn = "link_upload_id"
    )
    override val links: List<LinkEntity>
): IUploadWithLinks<UploadEntity, LinkEntity>

internal val UploadWithLinks.toGeneric: IUploadWithLinks<IUploadEntity, ILinkEntity>
    get() = createUploadWithLinks(upload,links)

internal val Flow<UploadWithLinks?>.toGenericFlow: Flow<IUploadWithLinks<IUploadEntity, ILinkEntity>?>
    get() = map { it?.toGeneric }

internal val List<UploadWithLinks>.toGenericList: List<IUploadWithLinks<IUploadEntity, ILinkEntity>>
    get() = map { it.toGeneric }
internal val Flow<List<UploadWithLinks>>.toGenericListFlow: Flow<List<IUploadWithLinks<IUploadEntity, ILinkEntity>>>
    get() = map { it.toGenericList }
fun createUploadWithLinks(
    upload: IUploadEntity,
    links: List<ILinkEntity>
): IUploadWithLinks<IUploadEntity, ILinkEntity> =
    object : IUploadWithLinks<IUploadEntity, ILinkEntity> {
        override val upload: IUploadEntity = upload
        override val links: List<ILinkEntity> = links
    }