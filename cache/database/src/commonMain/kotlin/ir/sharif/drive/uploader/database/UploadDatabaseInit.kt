package ir.sharif.drive.uploader.database

/**
 * Call from the Android Application before any upload database access (before creating the uploader / Koin graph).
 */
fun initializeUploadDatabasePlatform(context: Any?) {
    UploadDatabase.configureForPlatform(context)
}
