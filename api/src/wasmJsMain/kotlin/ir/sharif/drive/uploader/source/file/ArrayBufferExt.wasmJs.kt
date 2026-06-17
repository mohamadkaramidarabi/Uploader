package ir.sharif.drive.uploader.source.file

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

internal actual fun ArrayBuffer.toKotlinByteArray(): ByteArray {
    val uint8 = Uint8Array(this)
    return ByteArray(uint8.length) { index -> uint8.get(index) }
}
