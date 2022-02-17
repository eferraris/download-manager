package com.eferraris.download_manager.manager

import com.amazonaws.services.s3.AmazonS3
import com.eferraris.download_manager.model.DownloadRequest
import com.eferraris.download_manager.model.FilePart
import com.eferraris.download_manager.utils.Utils
import org.apache.commons.io.FileUtils
import java.io.File

class MultipartDownloadManager(
    private val client: AmazonS3,
    private val threshold: Long,
    private val request: DownloadRequest,
    private val parallel: Boolean
) {

    private val parts = mutableListOf<FilePart>()

    fun download() {

        instantiateParts()

        val partStream = parts
            .parallelStream()
            .takeIf { parallel }
            ?: parts.stream()

        partStream.forEach { it.download() }

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
            parts.add( FilePart(lower - threshold, upper(upper, totalLength), request, client) )
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
