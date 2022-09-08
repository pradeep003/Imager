package com.ftcoding.imager.components

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.io.File

object ImageDownloadManager {

    fun beginDownload(context: Context, url: String) {

        // initializing looper for showing toast message
        val handler = Handler(Looper.getMainLooper())

        handler.post {
            try {
                val downloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                // filename
                val filename = "${System.currentTimeMillis()}.jpg"
                // parse imager_logo.png link to uri
                val imageLink = Uri.parse(url)
                val request = DownloadManager.Request(imageLink)
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                    .setMimeType("image/jpeg")
                    .setAllowedOverRoaming(false)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setTitle(filename)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_PICTURES,
                        File.separator + filename + ".jpg"
                    )
                downloadManager.enqueue(request)
                Toast.makeText(context, "Image is Downloading...", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                Toast.makeText(context, "Image Download Failed", Toast.LENGTH_LONG).show()
            }

        }
    }
}