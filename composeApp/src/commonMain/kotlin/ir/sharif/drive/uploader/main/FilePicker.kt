package ir.sharif.drive.uploader.main

import androidx.compose.runtime.Composable

@Composable
expect fun FilePicker(
    requestId: Int,
    onFilesSelected: (List<PickedFile>) -> Unit,
)
