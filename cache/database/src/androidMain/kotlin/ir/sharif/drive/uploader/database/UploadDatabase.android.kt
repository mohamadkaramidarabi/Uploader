package ir.sharif.drive.uploader.database

import android.content.Context
import androidx.room.Room

internal actual fun provideUploadDatabase(platformContext: Any?): androidx.room.RoomDatabase.Builder<UploadDatabase> {
    val ctx = platformContext as? Context
        ?: error(
            "UploadDatabase.configureForPlatform(Application) must be called on Android before any database access.",
        )
    return Room.databaseBuilder(
        ctx.applicationContext,
        UploadDatabase::class.java,
        "drive_uploader.db",
    )
}
