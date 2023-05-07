package com.kenny.speechifyandroid.model.usecase

import com.google.gson.Gson
import com.kenny.speechifyandroid.model.request.RequestAudioFormat
import com.kenny.speechifyandroid.model.response.AudioRecord
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class RequestSpeechifyTTS {
    operator fun invoke(format: RequestAudioFormat):AudioRecord {
        var result = ""

        try {
            val urlObj = URL(speechifyUrl)
            val conn: HttpURLConnection = urlObj.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.doOutput = true
            val os: OutputStream = conn.outputStream
            os.write(format.toString().toByteArray())
            os.close()

            val inputStream = conn.inputStream
            val status = conn.responseCode
            if (inputStream != null) {
                result = BufferedReader(
                    InputStreamReader(
                        inputStream
                    )
                ).use(BufferedReader::readText)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Gson().fromJson(result, AudioRecord::class.java)
    }


    companion object {
        const val speechifyUrl = "https://audio.api.speechify.dev/generateAudioFiles"
    }
}

/**
 *
 * @param T
 * @property data
 * @property message
 * @constructor Create empty Resource
 */
sealed class Resource<out T>(
    data: T? = null,
    message: String? = null,
) {
    class Success<out T>(data: T) : Resource<T>(data)
    class Error<T>(
        message: String,
        data: T? = null,
    ) : Resource<T>(data, message)
    class Loading<Nothing> : Resource<Nothing>()
}