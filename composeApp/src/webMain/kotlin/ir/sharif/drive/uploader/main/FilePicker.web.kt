package ir.sharif.drive.uploader.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import ir.sharif.drive.uploader.source.file.WebFileRegistry
import ir.sharif.drive.uploader.source.file.browserSize
import kotlinx.browser.document
import org.w3c.files.File

@Composable
actual fun FilePicker(
    requestId: Int,
    onFilesSelected: (List<PickedFile>) -> Unit,
) {
    LaunchedEffect(requestId) {
        if (requestId <= 0) return@LaunchedEffect
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
                            path = WebFileRegistry.register(it),
                            name = it.name,
                            size = it.browserSize(),
                        )
                    }
                }
                onFilesSelected(pickedFiles)
            }
        }

        input.click()
    }
}
