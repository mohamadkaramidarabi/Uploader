package ir.sharif.drive.uploader.upload

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

internal object UploadForegroundViewModelOwner : ViewModelStoreOwner {
    private val store = ViewModelStore()

    override val viewModelStore: ViewModelStore
        get() = store

    fun viewModel(): UploadForegroundViewModel =
        ViewModelProvider(this)[UploadForegroundViewModel::class.java]
}
