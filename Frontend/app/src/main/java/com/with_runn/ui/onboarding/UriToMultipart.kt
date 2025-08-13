package com.with_runn.ui.onboarding
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object UriToMultipart {
    fun create(context: Context, uri: Uri, partName: String = "file"): MultipartBody.Part {
        // 1️⃣ 임시 파일 생성
        val file = File(context.cacheDir, "upload_image.jpg")
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        // 2️⃣ RequestBody 생성
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)

        // 3️⃣ MultipartBody.Part 생성
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
}
