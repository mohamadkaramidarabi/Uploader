package ir.sharif.drive.uploader.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class UploadWithLink(
    @Embedded val upload: UploadEntity,
    @Relation(
        parentColumn = "upload_id",
        entityColumn = "link_upload_id"
    )
    val links: List<LinkEntity>
)