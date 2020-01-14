package com.demo.flowerrecognition.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import com.demo.flowerrecognition.IMAGE_URI_KEY
import com.demo.flowerrecognition.R
import com.demo.flowerrecognition.model.FlowerDataSource
import com.demo.flowerrecognition.photo.PhotoView
import com.demo.flowerrecognition.util.CategoryType
import com.demo.flowerrecognition.util.ImageNet
import com.demo.flowerrecognition.util.Utils
import com.sh1r0.caffe_android_lib.CaffeMobile
import kotlinx.android.synthetic.main.activity_recog_result.*
import kotlinx.coroutines.*
import java.io.*


interface CNNListener {
    fun onTaskCompleted(result: FloatArray?, isFlower: Boolean)
}

class RecogResultActivity : BaseActivity(), CNNListener {

    private lateinit var modelBinary: String
    private val LOG_TAG = "RecogResultActivity"
    private val BUFFER_SIZE = 16 * 1024
    private lateinit var photoView: PhotoView
    private lateinit var modelDir: String

    init {
        System.loadLibrary("caffe")
        System.loadLibrary("caffe_jni")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recog_result)
        this.photoView = findViewById(R.id.photoView)

        val pictureUri = intent.getParcelableExtra<Uri>(IMAGE_URI_KEY)

        pictureUri?.let {
            this.photoView.setUri(pictureUri)
        }
        setScreenTitle("识别结果")

        modelDir = filesDir.absolutePath + "/model"
//        val modelProto = modelDir + "/google_net.prototxt"
//        modelBinary = modelDir + "/bvlc_googlenet.caffemodel"
        val modelProto = "$modelDir/classifier_template.prototxt"
        modelBinary = "$modelDir/classifier_googlenet_25M.caffemodel"


        copyModelsToAppData()

        val caffeMobile = CaffeMobile()
        caffeMobile.setNumThreads(4)
        caffeMobile.loadModel(modelProto, modelBinary)
//        val meanValues: FloatArray = floatArrayOf(104.0f, 117.0f, 123.0f)
        caffeMobile.setMean("$modelDir/classifier_googlenet.binaryproto")

//        val cnnTask = CNNTask(this, caffeMobile, false)
//        cnnTask.execute(pictureUri.path)
        bottomResultView.visibility = GONE

        cnnRecognitionAsync(this, caffeMobile, false, pictureUri.path!!)


    }

    private fun cnnRecognitionAsync(
        activity: RecogResultActivity,
        caffeMobile: CaffeMobile,
        isFlower: Boolean,
        imgPath: String
    ) = launch {
        val modelDir = activity.filesDir?.absolutePath + "/model"
        cnnRecognition(caffeMobile, activity, modelDir, imgPath, isFlower)
    }

    private suspend fun cnnRecognition(
        caffeMobile: CaffeMobile,
        activity: RecogResultActivity,
        modelDir: String,
        imgPath: String, isFlower: Boolean
    ) {
        var startTime: Long = 0
        withContext(Dispatchers.Main) {
            activity.showProgressDialog("识别中...")
        }
        val f = GlobalScope.async {
            startTime = SystemClock.uptimeMillis()
            caffeMobile.getConfidenceScore(imgPath)
        }

        withContext(Dispatchers.Main) {
            Log.i(
                activity.LOG_TAG,
                String.format(
                    "elapsed wall time: %d ms",
                    SystemClock.uptimeMillis() - startTime
                )
            )
            val farr = f.await()
            activity.onTaskCompleted(farr, isFlower)
            activity.dismissProgressDialog()
            if (!isFlower) {
                val flowerCaffeMobile = CaffeMobile()
                flowerCaffeMobile.setNumThreads(4)
                flowerCaffeMobile.loadModel(
                    "$modelDir/template.prototxt",
                    "$modelDir/plant_googlenet_25M.caffemodel"
                )
                flowerCaffeMobile.setMean("$modelDir/classifier_googlenet.binaryproto")
                val (_, index) = ImageNet.findMax(
                    farr,
                    if (isFlower) CategoryType.CATFLOWER404 else CategoryType.CAT110
                )
                if (index == 40) {
                    cnnRecognitionAsync(activity, flowerCaffeMobile, true, imgPath)
                }
            }
        }
    }


    private fun copyModelsToAppData() {
        Log.i(LOG_TAG, "copyModelsToAppData")
        val manager = resources.assets
        var inputStream: InputStream? = null
        var os: OutputStream? = null
        try {
            val trainedModel = File(modelBinary)
            if (trainedModel.exists()) {
                return
            }
            // copy nsfw models
            val destDir = File(modelDir)
            if (!destDir.exists()) {
                destDir.mkdirs()
            }
            var buffer: ByteArray? = null
            val files = manager.list("model")
            if (files != null) {
                for (file in files) {
                    val output = File(destDir, file)
                    if (output.exists()) {
                        continue
                    }
                    Log.i(LOG_TAG, "Extracting resource $file")
                    inputStream = manager.open("model/$file")
                    os = FileOutputStream(output)
                    if (buffer == null) {
                        buffer = ByteArray(BUFFER_SIZE)
                    }
                    var count: Int
                    while (inputStream.read(buffer, 0, BUFFER_SIZE).also {
                            count = it
                        } != -1) {
                        os.write(buffer, 0, count)
                    }
                    os.flush()
                    // Ensure something reasonable was written.
                    if (output.length() == 0L) {
                        throw IOException("$file extracted with 0 length!")
                    }
                }
            }
        } catch (e: IOException) {
            Log.w(LOG_TAG, "IOException: " + e.message)
        } finally {
            try {
                inputStream?.close()
                os?.close()
            } catch (e: IOException) {
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onTaskCompleted(result: FloatArray?, isFlower: Boolean) {
        val (str, maxIndex) = ImageNet.findMax(
            result,
            if (isFlower) CategoryType.CATFLOWER404 else CategoryType.CAT110
        )
        Log.v(LOG_TAG, str)
        resultTextView.text = "这可能是 $str"
        if (isFlower) {
            val photosList = FlowerDataSource.allFlower
            for (flowerItem in photosList) {
                if (flowerItem.ind == maxIndex.toLong()) {
                    bottomResultView.visibility = VISIBLE
                    bottomResultView.setOnClickListener {
                        val intent = Intent(this, DetailActivity::class.java)
                        intent.putExtra("item", flowerItem)
                        startActivity(intent)
                    }
                    itemImage.setImageDrawable(
                        Utils.getDrawable(
                            this,
                            "flower${flowerItem.ind}_0"
                        )
                    )
                    itemDate.text = flowerItem.nameEn
                    itemDescription.text = flowerItem.nameCh
                    textViewLang.text = flowerItem.language
                }
            }

        }

    }

    //    private class CNNTask(
//        private val listener: RecogResultActivity,
//        private val caffeMobile: CaffeMobile,
//        private val isFlower: Boolean
//    ) :
//        AsyncTask<String, Void?, FloatArray>() {
//        private var startTime: Long = 0
//        private lateinit var imgPath: String
//        private val weakListener: WeakReference<RecogResultActivity> = WeakReference(listener)
//        private val modelDir = weakListener.get()?.filesDir?.absolutePath + "/model"
//        override fun onPreExecute() {
//            super.onPreExecute()
//            this.weakListener.get()?.showProgressDialog("识别中...")
//        }
//
//        override fun doInBackground(vararg strings: String): FloatArray? {
//            imgPath = strings[0]
//            startTime = SystemClock.uptimeMillis()
//            return caffeMobile.getConfidenceScore(imgPath)
//        }
//
//        override fun onPostExecute(f: FloatArray) {
//            Log.i(
//                weakListener.get()?.LOG_TAG,
//                String.format(
//                    "elapsed wall time: %d ms",
//                    SystemClock.uptimeMillis() - startTime
//                )
//            )
//            listener.onTaskCompleted(f, isFlower)
//            super.onPostExecute(f)
//            this.weakListener.get()?.dismissProgressDialog()
//            if (!isFlower) {
//                val flowerCaffeMobile = CaffeMobile()
//                flowerCaffeMobile.setNumThreads(4)
//                flowerCaffeMobile.loadModel(
//                    "$modelDir/template.prototxt",
//                    "$modelDir/plant_googlenet_25M.caffemodel"
//                )
//                flowerCaffeMobile.setMean("$modelDir/classifier_googlenet.binaryproto")
//                val (_, index) = ImageNet.findMax(
//                    f,
//                    if (isFlower) CategoryType.CATFLOWER404 else CategoryType.CAT110
//                )
//                if (index == 40) {
//                    val cnnTask = CNNTask(listener, flowerCaffeMobile, true)
//                    cnnTask.execute(imgPath)
//                }
//            }
//        }
//
//    }


}
