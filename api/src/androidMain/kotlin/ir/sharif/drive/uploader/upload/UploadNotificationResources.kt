package ir.sharif.drive.uploader.upload

import android.content.Context

internal object UploadNotificationResources {
    private const val LIBRARY_RESOURCE_PACKAGE = "ir.sharif.drive.uploader.api"

    fun string(context: Context, name: String, vararg formatArgs: Any): String {
        val id = context.resources.getIdentifier(name, "string", LIBRARY_RESOURCE_PACKAGE)
        if (id == 0) return name
        return if (formatArgs.isEmpty()) {
            context.getString(id)
        } else {
            context.getString(id, *formatArgs)
        }
    }

    fun smallIcon(context: Context): Int {
        val id = context.resources.getIdentifier("ic_stat_upload", "drawable", LIBRARY_RESOURCE_PACKAGE)
        return if (id != 0) id else android.R.drawable.stat_sys_upload
    }
}
