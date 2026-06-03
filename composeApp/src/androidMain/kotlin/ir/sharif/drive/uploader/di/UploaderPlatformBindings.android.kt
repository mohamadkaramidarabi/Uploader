package ir.sharif.drive.uploader.di

import android.content.Context
import org.koin.core.context.GlobalContext

actual fun fileReaderContextForUploader(): Any =
    GlobalContext.get().get<Context>().applicationContext
