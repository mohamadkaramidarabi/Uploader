package ir.sharif.drive.uploader.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun FilePicker(
    requestId: Int,
    onFilesSelected: (List<PickedFile>) -> Unit,
) {
    val context = LocalContext.current
    val onFilesSelectedState = rememberUpdatedState(onFilesSelected)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris ->
        val files = uris.mapNotNull { uri -> uriToPickedFile(context, uri) }
        onFilesSelectedState.value(files)
    }

    LaunchedEffect(requestId) {
        if (requestId > 0) {
            launcher.launch(arrayOf("*/*"))
        }
    }
}

private fun uriToPickedFile(context: Context, uri: Uri): PickedFile? {
    takePersistableUriPermission(context, uri)
    val (queriedName, sizeFromCursor) = queryDisplayNameAndSize(context, uri)
    val name = queriedName ?: uri.lastPathSegment ?: return null
    val size = resolveSize(context, uri, sizeFromCursor)
    return PickedFile(
        path = uri.toString(),
        name = name,
        size = size,
    )
}

private fun takePersistableUriPermission(context: Context, uri: Uri) {
    try {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
    } catch (_: SecurityException) {
        // Some providers only grant temporary read access for this session.
    }
}

private fun queryDisplayNameAndSize(context: Context, uri: Uri): Pair<String?, Long> {
    val cursor = context.contentResolver.query(uri, null, null, null, null) ?: return null to 0L
    cursor.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
        if (!it.moveToFirst()) return null to 0L
        val name = if (nameIndex >= 0) it.getString(nameIndex) else null
        val size = if (sizeIndex >= 0 && !it.isNull(sizeIndex)) it.getLong(sizeIndex) else -1L
        return name to size
    }
}

private fun resolveSize(context: Context, uri: Uri, sizeFromCursor: Long): Long {
    if (sizeFromCursor > 0) return sizeFromCursor
    return context.contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
        descriptor.statSize.coerceAtLeast(0L)
    } ?: 0L
}
