package com.kenny.speechifyandroid.model.response

import com.google.gson.annotations.SerializedName


data class AudioRecord(
    val audioStream: String,
    val format: String,
    val speechMarks: SpeechMarks
)

data class SpeechMarks(
    val chunks: List<Chunk>
)

//todo force Gson serialize, fix it
data class Chunk(
    @SerializedName("chunks")
    var wordChunks: List<WordChunk>,
    val end: Int,
    val endTime: Int,
    val start: Int,
    val startTime: Int,
    val type: String,
    val value: String
)

data class WordChunk(
    val end: Int,
    val endTime: Int,
    val start: Int,
    val startTime: Int,
    val type: String,
    val value: String
)