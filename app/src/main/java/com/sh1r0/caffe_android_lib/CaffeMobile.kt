package com.sh1r0.caffe_android_lib

import java.nio.charset.StandardCharsets

class CaffeMobile {
    external fun setNumThreads(numThreads: Int)
    external fun enableLog(enabled: Boolean) // currently nonfunctional
    external fun loadModel(modelPath: String?, weightsPath: String?): Int // required
    private external fun setMeanWithMeanFile(meanFile: String)
    private external fun setMeanWithMeanValues(meanValues: FloatArray)
    external fun setScale(scale: Float)
    external fun getConfidenceScore(
        data: ByteArray?,
        width: Int,
        height: Int
    ): FloatArray

    fun getConfidenceScore(imgPath: String): FloatArray {
        return getConfidenceScore(stringToBytes(imgPath), 0, 0)
    }

    external fun predictImage(data: ByteArray?, width: Int, height: Int, k: Int): IntArray
    @JvmOverloads
    fun predictImage(imgPath: String, k: Int = 1): IntArray {
        return predictImage(stringToBytes(imgPath), 0, 0, k)
    }

    external fun extractFeatures(
        data: ByteArray?,
        width: Int,
        height: Int,
        blobNames: String?
    ): Array<FloatArray>

    fun extractFeatures(
        imgPath: String,
        blobNames: String?
    ): Array<FloatArray> {
        return extractFeatures(stringToBytes(imgPath), 0, 0, blobNames)
    }

    fun setMean(meanValues: FloatArray) {
        setMeanWithMeanValues(meanValues)
    }

    fun setMean(meanFile: String) {
        setMeanWithMeanFile(meanFile)
    }

    companion object {
        private fun stringToBytes(s: String): ByteArray {
            return s.toByteArray(StandardCharsets.US_ASCII)
        }
    }
}