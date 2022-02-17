package com.eferraris.download_manager

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.eferraris.download_manager.FileCommonOperations.fileWithoutExtension
import com.eferraris.download_manager.FileCommonOperations.filename
import com.eferraris.download_manager.FileCommonOperations.path
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import java.io.File
import kotlin.system.measureTimeMillis

class FilePart(
    private val lower: Long,
    private val upper: Long,
    private val request: DownloadRequest,
    private val client: AmazonS3,
    private val log: Logger?
) {

    fun download() {

        val file = File("${path(request.destinationPath)}/${fileWithoutExtension(request.destinationPath)}/$lower-${filename(request.destinationPath)}")

        log("file part ${lower.toString().padEnd(10)} download starting")

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
            }?: log("file part ${lower.toString().padEnd(10)} already exists")

    }

    private inline fun <T> measure(lower: Long, block: () -> T): T {
        var result: T

        log("file part ${lower.toString().padEnd(10)} took ${
            (measureTimeMillis { result = block() } / 1000).toString().padStart(3)
        } s")

        return result
    }

    private fun log(message: String) {
        log?.info( message )
            ?: println( message )
    }

}
