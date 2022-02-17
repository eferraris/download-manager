package com.eferraris.download_manager.model

data class DownloadRequest(
    val bucketName: String,
    val keyName: String,
    val destinationPath: String
)