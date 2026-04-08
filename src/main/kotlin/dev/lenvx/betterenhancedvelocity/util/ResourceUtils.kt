package dev.lenvx.betterenhancedvelocity.util

import java.io.File
import java.io.InputStream

object ResourceUtils {
    fun getResource(resourceFileName: String): InputStream? =
        ResourceUtils::class.java.classLoader.getResourceAsStream(resourceFileName)

    fun copyResource(resourceFileName: String, file: File) {
        getResource(resourceFileName)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}
