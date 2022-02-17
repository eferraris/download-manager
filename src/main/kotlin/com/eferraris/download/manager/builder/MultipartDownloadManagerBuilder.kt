package com.eferraris.download.manager.builder

import com.amazonaws.services.s3.AmazonS3
import com.eferraris.download.manager.MultipartDownloadManager
import com.eferraris.download.manager.model.DownloadRequest

class MultipartDownloadManagerBuilder(
    private val client: AmazonS3,
    private val bucketName: String,
    private val keyName: String,
    private val destinationPath: String
) {

    private var threshold: Long = 1024 * 1024 * 20L
    private var parallel: Boolean = false
    private var logReport: Boolean = false

    companion object {

        fun standard(
            client: AmazonS3,
            bucketName: String,
            keyName: String,
            destinationPath: String
        ): MultipartDownloadManagerBuilder = MultipartDownloadManagerBuilder(
            client,
            bucketName,
            keyName,
            destinationPath
        )

    }

    fun withPartThreshold(threshold: Long): MultipartDownloadManagerBuilder {
        this.threshold = threshold
        return this
    }

    fun withParallelResolution(parallel: Boolean): MultipartDownloadManagerBuilder {
        this.parallel = parallel
        return this
    }

    fun withLogReport(logReport: Boolean): MultipartDownloadManagerBuilder {
        this.logReport = logReport
        return this
    }

    fun build(): MultipartDownloadManager = MultipartDownloadManager(
        client,
        threshold,
        DownloadRequest(bucketName, keyName, destinationPath),
        parallel,
        logReport
    )


}
