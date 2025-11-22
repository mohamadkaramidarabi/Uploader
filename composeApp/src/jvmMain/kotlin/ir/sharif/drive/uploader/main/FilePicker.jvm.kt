package ir.sharif.drive.uploader.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
actual fun FilePicker(
    trigger: Boolean,
    onFilesSelected: (List<PickedFile>) -> Unit
) {
    LaunchedEffect(trigger) {
        delay(1.toDuration(DurationUnit.SECONDS))
        if (trigger) {
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
                        size = file.length()
                    )
                }
                onFilesSelected(files)
            }
        }
    }
}

