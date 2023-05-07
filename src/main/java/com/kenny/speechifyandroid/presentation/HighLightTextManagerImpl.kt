package com.kenny.speechifyandroid.presentation

import com.kenny.speechifyandroid.model.response.WordChunk
import com.kenny.speechifyandroid.util.findNearestSmallerOrEqualValue

class HighLightTextManagerImpl :HighLightTextManager {
    private var chunks: List<WordChunk>? = null

    override fun getHighLightTextIndexByTime(time:Long): WordChunk? {
        val index = chunks?.map { it.startTime.toLong() }?.toLongArray()?.findNearestSmallerOrEqualValue(time) ?: 1
        return chunks?.get(index = index)
    }

    override fun setWordChunks(chunks: List<WordChunk>) {
        this.chunks = chunks
    }
}