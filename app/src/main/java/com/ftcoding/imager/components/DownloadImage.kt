package com.ftcoding.imager.components

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

object DownloadImage {

    private val notification: Notification = Notification()

    // Function to convert string to URL
    private fun stringToURL(string: String, context: Context): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
        return null
    }


    fun saveToLocalStorage(string: String, activity: Activity?, context: Context) {

        val url: URL = stringToURL(string, context)!!
        val connection: HttpURLConnection
        val inputStream: InputStream
        var fos: OutputStream?

        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()

            // filename
            val filename = "${System.currentTimeMillis()}.jpg"

            // handler to execute toast message
            val handler = Handler(Looper.getMainLooper())

            //set the path where we want to save the file in this case, going to save it on the root directory of the sd card.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                activity?.contentResolver.also { resolver ->
                    val contentValue = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "imager_logo.png/jpg")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES
                        )
                    }
                    val imageUri: Uri? =
                        resolver?.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValue
                        )

                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imagesDir = Environment.getExternalStorageState()
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }

            handler.post {
                Toast.makeText(context, "Download starting....", Toast.LENGTH_LONG).show()
            }


            // download the file
            inputStream = connection.inputStream

            // total size of the file
            val totalSize = connection.contentLength

            // variable to store total download bytes
            var downloadedSize = 0
            val buffer = ByteArray(1024 * 8)
            //used to store a temporary size of the buffer
            var bufferLength = 0

            if (activity != null) {
                notification.showDownloadProgressNotification(context, activity, totalSize, downloadedSize)
            }

            //now, read through the input buffer and write the contents to the file
            do {
                bufferLength = inputStream.read(buffer)
                if (bufferLength == -1) {
                    break
                } else {
                    //add the data in the buffer to the file in the file output stream (the file on the sd card

                    fos?.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    //this is where you would do something to report the prgress, like this maybe
                    println("downloaded size is $downloadedSize and total size is $totalSize")
                    //updateProgress(downloadedSize, totalSize);

                }
            } while (true)

            fos?.close()
            if (totalSize == downloadedSize) {
                handler.post {
                    Toast.makeText(context, "Saved to Gallery", Toast.LENGTH_LONG).show()
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error, ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}