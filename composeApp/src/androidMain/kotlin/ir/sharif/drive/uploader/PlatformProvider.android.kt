package ir.sharif.drive.uploader

import android.content.Context
import ir.sharif.drive.uploader.network.AuthApi
import ir.sharif.drive.uploader.storage.TokenStorage

actual object PlatformProvider {
    private var context: Context? = null
    
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    
    fun getContext(): Context? = context
}

