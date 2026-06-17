package ir.sharif.drive.uploader.source.file

import org.w3c.files.Blob

private fun readBlobSize(blob: Blob): Double = js("Number(blob.size)")

actual fun Blob.browserSize(): Long = readBlobSize(this).toLong()
