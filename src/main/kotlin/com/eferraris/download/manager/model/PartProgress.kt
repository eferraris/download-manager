package com.eferraris.download.manager.model

data class PartProgress(
    val part: Long,
    val totalParts: Long,
    val partBytesTransferred: Long,
    val partBytesToTransfer: Long,
    val totalBytesToTransfer: Long
)
