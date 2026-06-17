package ir.sharif.drive.uploader.source.file

import kotlin.js.unsafeCast
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array

internal actual fun ArrayBuffer.toKotlinByteArray(): ByteArray = Int8Array(this).unsafeCast<ByteArray>()
