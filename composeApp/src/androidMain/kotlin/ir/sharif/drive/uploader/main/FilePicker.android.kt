package ir.sharif.drive.uploader.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun FilePicker(
    trigger: Boolean,
    onFilesSelected: (List<PickedFile>) -> Unit
) {
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val files = mutableListOf<PickedFile>()
            val data = result.data
            
            data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i)?.uri
                    uri?.let {
                        val fileInfo = getFileInfo(context, it)
                        files.add(
                            PickedFile(
                                path = it.toString(),
                                name = fileInfo.first,
                                size = fileInfo.second
                            )
                        )
                    }
                }
            } ?: data?.data?.let { uri ->
                val fileInfo = getFileInfo(context, uri)
                files.add(
                    PickedFile(
                        path = uri.toString(),
                        name = fileInfo.first,
                        size = fileInfo.second
                    )
                )
            }
            
            onFilesSelected(files)
        }
    }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            launcher.launch(intent)
        }
    }
}

private fun getFileInfo(context: android.content.Context, uri: Uri): Pair<String, Long> {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
        if (it.moveToFirst()) {
            val name = if (nameIndex >= 0) it.getString(nameIndex) else "Unknown"
            val size = if (sizeIndex >= 0) it.getLong(sizeIndex) else 0L
            return name to size
        }
    }
    return "Unknown" to 0L
}

