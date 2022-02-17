package com.eferraris.download_manager.model

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.eferraris.download_manager.utils.FileCommonOperations.fileWithoutExtension
import com.eferraris.download_manager.utils.FileCommonOperations.filename
import com.eferraris.download_manager.utils.FileCommonOperations.path
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.measureTimeMillis

class FilePart(
    private val lower: Long,
    private val upper: Long,
    private val request: DownloadRequest,
    private val client: AmazonS3
) {

    private val log: Logger = LoggerFactory.getLogger( FilePart::class.java )

    fun download() {

        val file = File("${path(request.destinationPath)}/${fileWithoutExtension(request.destinationPath)}/$lower-${filename(request.destinationPath)}")

        log.info("file part ${lower.toString().padEnd(10)} download starting")

        file.exists()
            .takeIf { !it }
            ?.let {
                val bytes = measure(lower) {
                    client
                        .getObject(
                            GetObjectRequest(request.bucketName, request.keyName)
                                .withRange(lower, upper)
                        )
                        .objectContent
                        .readAllBytes()
                }

                FileUtils.writeByteArrayToFile(file, bytes, true)
            }?: log.info("file part ${lower.toString().padEnd(10)} already exists")

    }

    private inline fun <T> measure(lower: Long, block: () -> T): T {
        var result: T

        log.info("file part ${lower.toString().padEnd(10)} took ${
            (measureTimeMillis { result = block() } / 1000).toString().padStart(3)
        } s")

        return result
    }

}
