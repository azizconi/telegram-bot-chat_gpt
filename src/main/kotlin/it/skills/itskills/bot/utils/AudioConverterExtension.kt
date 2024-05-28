package it.skills.itskills.bot.utils

import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.Executors

object AudioConverterExtension {

    fun copyStream(
        inputStream: InputStream,
        outputStream: FileOutputStream
    ) {
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
    }

    fun executeFFmpegCommand(
        inputFilePath: String,
        outputFilePath: String
    ) {
        val processBuilder = ProcessBuilder(
            "ffmpeg",
            "-i", inputFilePath,
            "-ac", "1",
            "-map", "0:a",
            "-codec:a", "libopus", // Use 'libopus' encoder
            "-b:a", "128k",
            "-vbr", "off",
            "-ar", "24000",
            outputFilePath
        )

        processBuilder.redirectErrorStream(true)

        try {
            val process = processBuilder.start()
            Executors.newSingleThreadExecutor().submit {
                process.inputStream.use { inputStream ->
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (inputStream.read(buffer)
                            .also { bytesRead = it } != -1
                    ) {
                        System.out.write(buffer, 0, bytesRead)
                    }
                }
            }
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                println("FFmpeg execution failed with exit code: $exitCode")
            }
            println("FFmpeg finished")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}