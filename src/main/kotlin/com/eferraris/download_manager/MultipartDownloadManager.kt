package com.eferraris.download_manager

import com.amazonaws.services.s3.AmazonS3
import org.slf4j.Logger

class MultipartDownloadManager(
    private val client: AmazonS3,
    private val threshold: Long,
    private val request: DownloadRequest,
    private val parallel: Boolean,
    private val log: Logger?
) {

    private val parts = mutableListOf<FilePart>()

    fun download() {

        instantiateParts()

        val partStream = parts
            .parallelStream()
            .takeIf { parallel }
            ?: parts.stream()

        partStream.forEach { it.download() }

    }

    private fun instantiateParts() {

        val totalLength = client
            .getObjectMetadata(request.bucketName, request.keyName)
            .contentLength

        var upper = 0L
        var lower = 0L

        while ( totalLength > upper ) {
            lower+=threshold
            upper+=threshold
            parts.add(
                FilePart(
                    lower - threshold,
                    upper(upper, totalLength),
                    request,
                    client,
                    log
                )
            )
        }

    }

    private fun upper(upper: Long, totalLength: Long) = (upper - 1)
        .takeIf { it < totalLength }
        ?: (totalLength - 1)

}
