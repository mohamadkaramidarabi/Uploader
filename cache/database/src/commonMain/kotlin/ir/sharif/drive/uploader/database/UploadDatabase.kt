package ir.sharif.drive.uploader.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import ir.sharif.drive.uploader.database.dao.UploadDao
import ir.sharif.drive.uploader.database.entity.LinkEntity
import ir.sharif.drive.uploader.database.entity.UploadEntity
import ir.sharif.drive.uploader.database.migration.MIGRATION_1_2
import ir.sharif.drive.uploader.models.States
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.concurrent.Volatile

class Convertor {
    @TypeConverter
    fun formUploadInfoState(state: States.UploadInfo.State) = state.name

    @TypeConverter
    fun fromUploadLinkState(state: States.Link.State) = state.name

    @TypeConverter
    fun toUploadInfoState(state: String) =
        enumValueOf<States.UploadInfo.State>(state)

    @TypeConverter
    fun toUploadLinkState(state: String) = enumValueOf<States.Link.State>(state)
}
@Database(entities = [UploadEntity::class, LinkEntity::class], version = 2)
@TypeConverters(Convertor::class)
@ConstructedBy(UploadDatabaseConstructor::class)
internal abstract class UploadDatabase : RoomDatabase() {
    abstract fun uploadDao(): UploadDao

    companion object {
        @Volatile
        private var platformContext: Any? = null
        fun configureForPlatform(context: Any?) {
            platformContext = context
        }

        val instance: UploadDatabase by lazy {
            provideUploadDatabase(platformContext)
                .setQueryCoroutineContext(Dispatchers.IO)
                .addMigrations(MIGRATION_1_2)
                .setDriver(BundledSQLiteDriver())
                .build()
        }
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object UploadDatabaseConstructor : RoomDatabaseConstructor<UploadDatabase> {
    override fun initialize(): UploadDatabase
}

internal expect fun provideUploadDatabase(platformContext: Any?): RoomDatabase.Builder<UploadDatabase>

