package ir.sharif.drive.uploader.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE uploads ADD COLUMN meta_data TEXT")
        super.migrate(connection)
    }
}