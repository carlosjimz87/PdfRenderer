package com.carlosjimz87.pdfrenderer.utils

import android.content.Context
import android.util.Base64
import android.util.Log
import com.carlosjimz87.pdfrenderer.Constants
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.GZIPInputStream

object FileUtils {
     fun saveToDisk(context: Context, body: ResponseBody): File {
         Log.d(TAG, "saveToDisk content (${body.contentLength()}) length")
        val file = File(context.cacheDir, Constants.PDF_FILE_NAME)
        try {
            val inputStream = body.byteStream()
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    fun saveGzipToDisk(context: Context, inputStream: InputStream): File {
        Log.d(TAG, "saveGzipToDisk ")
        val file = File(context.cacheDir, Constants.PDF_FILE_NAME)
        try {
            val inputGzipStream = GZIPInputStream(inputStream)
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(8 * 1024)
            var bytesRead: Int
            while (inputGzipStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            outputStream.flush()
            inputGzipStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }


    fun saveToCache(responseBody: ResponseBody, context: Context): File? {
        // Define the cache directory
        val cacheDir = context.cacheDir
        val file = File(cacheDir, Constants.PDF_FILE_NAME)

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            val fileReader = ByteArray(4096)
            var fileSizeDownloaded: Long = 0
            inputStream = responseBody.byteStream()
            outputStream = FileOutputStream(file)

            while (true) {
                val read = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()
            }

            outputStream.flush()
            return file

        } catch (e: IOException) {
            return null
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    fun saveToCache(inputStream: InputStream, context: Context): File? {
        // Define the cache directory
        val cacheDir = context.cacheDir
        val file = File(cacheDir, Constants.PDF_FILE_NAME)

        var fileOutputStream: FileOutputStream? = null
        try {
            // Open a FileOutputStream to write to the file
            fileOutputStream = FileOutputStream(file)

            // Initialize buffer and bytesRead to read from InputStream
            val buffer = ByteArray(4096)
            var bytesRead: Int

            // Read the InputStream into the FileOutputStream
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                fileOutputStream.write(buffer, 0, bytesRead)
            }

            fileOutputStream.flush()
        } catch (e: IOException) {
            // Handle exceptions here
            e.printStackTrace()
            return null
        } finally {
            try {
                // Close the output stream
                fileOutputStream?.close()
                // Close the input stream
                inputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return file
    }
}