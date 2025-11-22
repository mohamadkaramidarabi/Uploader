package ir.sharif.drive.uploader.database

import androidx.room.Room
import java.io.File

internal actual fun provideUploadDatabase(platformContext: Any?): androidx.room.RoomDatabase.Builder<UploadDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "drive_uploader.db")
    println(dbFile.absolutePath)
    return Room.databaseBuilder<UploadDatabase>(
        name = dbFile.absolutePath,
    )
}