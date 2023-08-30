package com.carlosjimz87.pdfrenderer.utils

import android.content.Context
import com.carlosjimz87.pdfrenderer.Constants.PDF_TEMP_FILENAME
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {
     fun saveToDisk(context: Context, body: ResponseBody): File {
        val file = File(context.getExternalFilesDir(null), PDF_TEMP_FILENAME)
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
}