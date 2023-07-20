package com.eferraris.download.manager.model

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.eferraris.download.manager.utils.Utils
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.system.measureTimeMillis

class FilePart(
    val lower: Long,
    val part: Long,
    val upper: Long,
    private val size: Long = upper - lower + 1,
    private val request: DownloadRequest,
    private val client: AmazonS3,
    private val logReport: Boolean,
    private val totalParts: Long,
    private val totalBytes: Long
) {

    private val log: Logger = LoggerFactory.getLogger( FilePart::class.java )

    fun download(callback: (PartProgress) -> Unit) {

        val file = File( Utils.partPath(request.destinationPath, lower) )

        if ( logReport ) log.info("file part ${lower.toString().padEnd(10)} download starting")

        file.exists()
            .takeIf { !it }
            ?.let {
                val bytes = measure( lower ) {
                    val request = GetObjectRequest(request.bucketName, request.keyName)
                        .withRange(lower, upper)
                    request
                        .setGeneralProgressListener {
                            callback(
                                PartProgress(
                                    part = part,
                                    totalParts = totalParts,
                                    totalBytesToTransfer = totalBytes,
                                    partBytesTransferred = it.bytesTransferred,
                                    partBytesToTransfer = size
                                )
                            )
                        }
                    val s3Object = client.getObject(request)
                    val bytes = s3Object.objectContent.readAllBytes()
                    s3Object.close()
                    bytes
                }

                FileUtils.writeByteArrayToFile(file, bytes, true)
            }?: if ( logReport ) log.info("file part ${lower.toString().padEnd(10)} already exists")

    }

    private inline fun <T> measure(lower: Long, block: () -> T): T {
        var result: T

        val message = "file part ${lower.toString().padEnd(10)} took ${
            (measureTimeMillis { result = block() } / 1000).toString().padStart(3)
        } s"

        if ( logReport ) log.info( message )

        return result
    }

}
