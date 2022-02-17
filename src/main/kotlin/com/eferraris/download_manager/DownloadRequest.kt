package com.eferraris.download_manager

data class DownloadRequest(
    val bucketName: String,
    val keyName: String,
    val destinationPath: String
)