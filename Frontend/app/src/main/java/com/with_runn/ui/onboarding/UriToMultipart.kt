package com.with_runn.ui.onboarding
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object UriToMultipart {
    fun create(context: Context, uri: Uri, partName: String = "file"): MultipartBody.Part {
        val fileName = "upload_image_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}
