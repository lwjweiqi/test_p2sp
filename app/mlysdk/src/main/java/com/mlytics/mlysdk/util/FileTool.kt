package com.mlytics.mlysdk.util

import android.content.Context
import android.os.Environment
import java.io.FileOutputStream
import java.io.IOException

object FileTool {

    fun write(
        file: String,
        bytes: ByteArray,
        context: Context,
        type: String = Environment.DIRECTORY_DOCUMENTS
    ) {

        val path = context.getExternalFilesDir(type)?.canonicalPath
            ?: throw IOException("getExternalFilesDir null")
        val fn = "$path/$file"
        FileOutputStream(fn).use {
            it.write(bytes)
        }
    }
}