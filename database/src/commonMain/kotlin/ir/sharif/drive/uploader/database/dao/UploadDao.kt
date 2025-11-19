package ir.sharif.drive.uploader.database.dao

import androidx.room.Dao
import ir.sharif.drive.uploader.database.UploadDatabase

@Dao
interface UploadDao {

    companion object {
        val instance: UploadDao by lazy {
            UploadDatabase.instance.uploadDao()
        }
    }
}