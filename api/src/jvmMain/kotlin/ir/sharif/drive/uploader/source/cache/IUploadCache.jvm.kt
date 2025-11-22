package ir.sharif.drive.uploader.source.cache

import ir.sharif.drive.uploader.cache.dao.IUploadDao
import ir.sharif.drive.uploader.cache.entity.ILinkEntity
import ir.sharif.drive.uploader.cache.entity.IUploadEntity
import ir.sharif.drive.uploader.cache.entity.IUploadWithLinks
import ir.sharif.drive.uploader.database.dao.uploadDao as databaseDao

actual val uploadDao: IUploadDao<IUploadEntity, ILinkEntity, IUploadWithLinks<IUploadEntity, ILinkEntity>> = databaseDao
