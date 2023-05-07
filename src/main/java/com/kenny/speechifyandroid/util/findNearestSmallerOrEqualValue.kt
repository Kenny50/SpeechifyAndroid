package com.kenny.speechifyandroid.util

internal fun LongArray.findNearestSmallerOrEqualValue(expectValue: Long): Int? {
    var left = 0
    var right = this.size - 1
    var result: Int? = null

    while (left <= right) {
        val mid = (left + right) / 2

        if (this[mid] <= expectValue) {
            result = mid
            left = mid + 1
        } else {
            right = mid - 1
        }
    }

    return result
}