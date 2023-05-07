package com.kenny.speechifyandroid.model.request

import com.kenny.speechifyandroid.model.SoundOptions

data class RequestAudioFormat(
    val audioFormat: String = "ogg",
    val paragraphChunks: List<String>,
    val voiceParams:VoiceParams
)

data class VoiceParams(
    val name: SoundOptions,
    val engine:String = "neural",
    val languageCode:String = "en-US"
)
