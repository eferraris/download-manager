package com.eferraris.download.manager

import com.amazonaws.services.s3.AmazonS3
import com.eferraris.download.manager.model.DownloadRequest
import com.eferraris.download.manager.model.FilePart
import com.eferraris.download.manager.utils.Utils
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.ForkJoinPool

class MultipartDownloadManager(
    private val client: AmazonS3,
    private val threshold: Long,
    private val request: DownloadRequest,
    private val parallel: Boolean,
    private val logReport: Boolean,
    private val threads: Int?
) {

    private val parts = mutableListOf<FilePart>()
    private val log = LoggerFactory.getLogger(MultipartDownloadManager::class.java)

    fun download() {

        val file = File( request.destinationPath )

        file.exists()
            .takeIf { it }
            ?.let {
                log.warn("file ${request.destinationPath} already exists, it will be erased")
                FileUtils.delete( file )
            }

        log.info("download of key ${request.keyName} has started")

        instantiateParts()

        parallel
            .takeIf { it }
            ?.let {

                threads?.let {

                    ForkJoinPool( threads )
                        .submit { parts.parallelStream().forEach { it.download() } }

                }?: let { parts.parallelStream().forEach { it.download() } }

            }?: let { parts.forEach { it.download() } }

        joinParts()

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
            parts.add( FilePart(lower - threshold, upper(upper, totalLength), request, client, logReport) )
        }

    }

    private fun upper(upper: Long, totalLength: Long) = (upper - 1)
        .takeIf { it < totalLength }
        ?: (totalLength - 1)

    private fun joinParts() {
        val destinationFile = File( request.destinationPath )

        parts
            .map { File( Utils.partPath(request.destinationPath, it.lower) ) }
            .forEach { FileUtils.writeByteArrayToFile(destinationFile, it.readBytes(), true) }

        FileUtils.deleteDirectory( File("${Utils.path(request.destinationPath)}/${Utils.fileWithoutExtension(request.destinationPath)}") )
    }

}
