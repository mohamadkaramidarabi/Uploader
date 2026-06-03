package ir.sharif.drive.uploader.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
actual fun FilePicker(
    requestId: Int,
    onFilesSelected: (List<PickedFile>) -> Unit,
) {
    LaunchedEffect(requestId) {
        if (requestId <= 0) return@LaunchedEffect
        val fileChooser = JFileChooser().apply {
            isMultiSelectionEnabled = true
            fileFilter = FileNameExtensionFilter("All Files", "*")
        }

        val result = fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val files = fileChooser.selectedFiles.map { file ->
                PickedFile(
                    path = file.absolutePath,
                    name = file.name,
                    size = file.length(),
                )
            }
            onFilesSelected(files)
        }
    }
}
