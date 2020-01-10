package com.demo.flowerrecognition.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import java.io.IOException

object Utils {
    fun getDrawable(context: Context, imgName: String): Drawable? {
        return ContextCompat.getDrawable(
            context,
            context.resources.getIdentifier(imgName, "mipmap", context.packageName)
        )
    }

    fun loadJSONFromAssets(context: Context, fileName: String): String? {
        var json: String? = null
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            json = String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return json
    }
}