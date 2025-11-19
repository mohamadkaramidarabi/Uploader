package ir.sharif.drive.uploader.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import ir.sharif.drive.uploader.database.dao.LinkDao
import ir.sharif.drive.uploader.database.dao.UploadDao
import ir.sharif.drive.uploader.database.entity.LinkEntity
import ir.sharif.drive.uploader.database.entity.UploadEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [UploadEntity::class, LinkEntity::class], version = 1)
internal abstract class UploadDatabase : RoomDatabase() {
    abstract fun uploadDao(): UploadDao
    abstract fun linkDao(): LinkDao

    companion object {
        val instance: UploadDatabase by lazy {
            provideUploadDatabase(null)
                .setQueryCoroutineContext(Dispatchers.IO)
                .setDriver(BundledSQLiteDriver())
                .build()
        }

    }
}

internal expect fun provideUploadDatabase(platformContext: Any?): RoomDatabase.Builder<UploadDatabase>

