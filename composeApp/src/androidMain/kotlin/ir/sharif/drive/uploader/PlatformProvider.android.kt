package ir.sharif.drive.uploader

import android.annotation.SuppressLint
import android.content.Context
import ir.sharif.drive.uploader.network.AuthApi
import ir.sharif.drive.uploader.storage.TokenStorage

@SuppressLint("StaticFieldLeak")
actual object PlatformProvider {
    private var context: Context? = null
    
    fun init(context: Context) {
        this.context = context.applicationContext
    }
    
    fun getContext(): Context? = context
}

