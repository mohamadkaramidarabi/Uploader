package ir.sharif.drive.uploader.source.file

import org.khronos.webgl.ArrayBuffer

internal expect fun ArrayBuffer.toKotlinByteArray(): ByteArray
