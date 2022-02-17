package com.eferraris.download.manager.model

data class DownloadRequest(
    val bucketName: String,
    val keyName: String,
    val destinationPath: String
)