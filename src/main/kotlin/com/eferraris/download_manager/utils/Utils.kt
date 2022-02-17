package com.eferraris.download_manager.utils

object Utils {

    fun path(fullPath: String): String = fullPath
        .split("/")
        .let { it.subList(0, it.size - 1).joinToString("/") }

    fun filename(fullPath: String): String = fullPath
        .split("/")
        .last()

    fun fileWithoutExtension(fullPath: String): String = filename(fullPath)
        .split(".")
        .first()

    fun partPath(destinationPath: String, lower: Long) =
        "${path(destinationPath)}/${fileWithoutExtension(destinationPath)}/${lower}-${filename(destinationPath)}"

}