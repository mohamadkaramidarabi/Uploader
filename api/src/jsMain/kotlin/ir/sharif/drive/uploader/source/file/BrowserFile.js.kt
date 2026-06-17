package ir.sharif.drive.uploader.source.file

import org.w3c.files.Blob

actual fun Blob.browserSize(): Long = size.toLong()
