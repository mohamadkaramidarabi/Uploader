package ir.sharif.drive.uploader.di

import androidx.compose.runtime.Composable

@Composable
expect fun AppKoinHost(content: @Composable () -> Unit)
