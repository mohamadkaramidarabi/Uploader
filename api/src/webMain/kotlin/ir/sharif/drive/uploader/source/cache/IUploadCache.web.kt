package ir.sharif.drive.uploader.source.cache

import ir.sharif.drive.uploader.cache.dao.IUploadDao
import ir.sharif.drive.uploader.cache.entity.ILinkEntity
import ir.sharif.drive.uploader.cache.entity.IUploadEntity
import ir.sharif.drive.uploader.cache.entity.IUploadWithLinks

internal actual val uploadDao: IUploadDao<IUploadEntity, ILinkEntity, IUploadWithLinks<IUploadEntity, ILinkEntity>>
    get() = TODO("Not yet implemented")