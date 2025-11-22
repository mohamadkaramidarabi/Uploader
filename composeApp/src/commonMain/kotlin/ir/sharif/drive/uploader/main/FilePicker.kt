package ir.sharif.drive.uploader.main

import androidx.compose.runtime.Composable

@Composable
expect fun FilePicker(
    trigger: Boolean,
    onFilesSelected: (List<PickedFile>) -> Unit
)
