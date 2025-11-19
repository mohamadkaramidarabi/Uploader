package ir.sharif.drive.uploader.database.dao

import androidx.room.Dao
import ir.sharif.drive.uploader.database.UploadDatabase


@Dao
interface LinkDao {

    companion object {
        val instance: LinkDao by lazy {
            UploadDatabase.instance.linkDao()
        }
    }
}