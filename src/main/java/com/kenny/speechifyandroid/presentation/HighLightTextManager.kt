package com.kenny.speechifyandroid.presentation

import com.kenny.speechifyandroid.model.response.WordChunk

interface HighLightTextManager {
    fun getHighLightTextIndexByTime(time:Long):WordChunk?

    fun setWordChunks(chunks:List<WordChunk>)
}