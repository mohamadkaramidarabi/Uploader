package ir.sharif.drive.uploader.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.sharif.drive.uploader.models.States
import ir.sharif.drive.uploader.models.UploadInfo.Link
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = koinViewModel()
    val selectedFiles by viewModel.selectedFiles.collectAsState()
    var triggerFilePicker by remember { mutableStateOf(false) }


    FilePicker(
        trigger = triggerFilePicker,
        onFilesSelected = { files ->
            viewModel.addFiles(files)
            triggerFilePicker = false
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                triggerFilePicker = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Files")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Selected Files: ${selectedFiles.size}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))
        val uploadings =
            viewModel.uploadingInfos.collectAsStateWithLifecycle(initialValue = emptyList()).value
        LazyColumn {
            items(uploadings) { file ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(file.name.value)
                    Text(file.state.name)
                    Text(
                        "progress: ${
                            file.links.filter { it.state == States.Link.State.SUCCESS }.size.times(
                                100
                            ).div(file.chunkCount?.value ?: 1)
                        }%"
                    )
                    Spacer(Modifier.height(8.dp))
                    Row {
                        when (file.state) {
                            States.UploadInfo.State.UPLOADING,
                            States.UploadInfo.State.STARTED,
                            States.UploadInfo.State.STARTING -> {
                                Button(
                                    onClick = { viewModel.pauseUpload(file.id) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Pause")
                                }
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.cancelUpload(file.id) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancel")
                                }
                            }
                            States.UploadInfo.State.PAUSED -> {
                                Button(
                                    onClick = { viewModel.resumeUpload(file.id) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Resume")
                                }
                                Spacer(Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.cancelUpload(file.id) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancel")
                                }
                            }
                            States.UploadInfo.State.IN_QUEUE,
                            States.UploadInfo.State.PREPARING,
                            States.UploadInfo.State.PREPARED -> {
                                Button(
                                    onClick = { viewModel.cancelUpload(file.id) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Cancel")
                                }
                            }
                            else -> {
                                // For SUCCESS, FAILED, CANCELED, COMPLETING, ALL_PUT_DONE states
                                // No action buttons needed
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FileItem(
    file: PickedFile,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = file.name,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${file.size / 1024} KB",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = file.path,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}