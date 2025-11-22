package ir.sharif.drive.uploader.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ir.sharif.drive.uploader.main.MainViewModel.PickedFile
import kotlinx.browser.document
import org.w3c.files.File

@Composable
actual fun FilePicker(
    trigger: Boolean,
    onFilesSelected: (List<PickedFile>) -> Unit
) {
    LaunchedEffect(trigger) {
        if (trigger) {
            val input = document.createElement("input") as org.w3c.dom.HTMLInputElement
            input.type = "file"
            input.multiple = true
            input.accept = "*/*"
            
            input.onchange = { event ->
                val target = event.target as? org.w3c.dom.HTMLInputElement
                val files = target?.files
                if (files != null) {
                    val pickedFiles = (0 until files.length).mapNotNull { index ->
                        val file = files.item(index) as? File
                        file?.let {
                            PickedFile(
                                path = it.name,
                                name = it.name,
                                size = it.size.toLong()
                            )
                        }
                    }
                    onFilesSelected(pickedFiles)
                }
            }
            
            input.click()
        }
    }
}

