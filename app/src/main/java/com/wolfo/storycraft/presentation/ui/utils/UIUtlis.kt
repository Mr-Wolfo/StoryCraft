package com.wolfo.storycraft.presentation.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object UiUtils {
    fun toLocaleDateTime(time: String): LocalDateTime =
        try {
            Instant.parse(time)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        } catch (e: Exception) {
            LocalDateTime.now()
        }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }
}

// Расширения для сжатия изображения
fun Uri.toFile(context: Context): File? {
    return try {
        val stream = context.contentResolver.openInputStream(this)
        val file = File.createTempFile("avatar_", ".jpg", context.cacheDir)
        stream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file
    } catch (e: Exception) {
        null
    }
}

fun File.compressImage(maxSize: Long = 2 * 1024 * 1024): File { // 2MB по умолчанию
    if (length() <= maxSize) return this

    return try {
        val bitmap = BitmapFactory.decodeFile(absolutePath)
        var quality = 90
        var compressedFile: File? = null

        while (quality >= 10) {
            val output = File.createTempFile("compressed_", ".jpg", parentFile)
            FileOutputStream(output).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            }

            if (output.length() <= maxSize) {
                compressedFile = output
                break
            }
            quality -= 20
        }

        compressedFile ?: this
    } catch (e: Exception) {
        this
    }
}



