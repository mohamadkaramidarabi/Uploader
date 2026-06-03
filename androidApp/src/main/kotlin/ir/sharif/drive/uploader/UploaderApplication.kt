package ir.sharif.drive.uploader

import android.app.Application
import ir.sharif.drive.uploader.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class UploaderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeAndroidUploaderPlatform(this)
        startKoin {
            androidLogger()
            androidContext(this@UploaderApplication)
            modules(appModule)
        }
    }
}
