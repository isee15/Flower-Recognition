package com.demo.flowerrecognition.photo

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.core.view.GestureDetectorCompat
import com.facebook.common.logging.FLog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.controller.BaseControllerListener
import com.facebook.drawee.controller.ControllerListener
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.ImageInfo
import java.io.FileNotFoundException
import java.util.*

class PhotoView(context: Context?, attrs: AttributeSet?) : SimpleDraweeView(context, attrs) {
    var drawMatrix: Matrix? = null
    var lastFocusX = 0f
    var lastFocusY = 0f
    private val mScaleDetector: ScaleGestureDetector
    private val mGestureDetector: GestureDetectorCompat
    //    private val mScaleFactor = 1f
    private val pointRed: ArrayList<PointF?> = ArrayList()
    private val pointGreen: ArrayList<ArrayList<Float>?> =
        ArrayList()
    private var redPaint = Paint()
    private var greenPaint = Paint()
    private var imgInfo: ImageInfo? = null
    private var realWidth = 0f
    private var realHeight = 0f
    private var imgWidth = 0f
    private var preMatrix: Matrix? = null
    private var redRadius = 0f
    private val maxZoom = 3f
    private val minZoom = 1f


    fun setSource(source: String?) {
        val uri = Uri.parse(source)
        setUri(uri)
    }

    fun setCoordinateArray(coordinate: ArrayList<ArrayList<Float>>) {
        var radius = 0f
        for (i in coordinate.indices) {
            val coord = coordinate[i]
            pointGreen.add(coord)
            radius += coord[2]
        }
        if (coordinate.size > 0) {
            radius /= coordinate.size
        }
        if (radius > 0) {
            redRadius = radius
        }
        invalidate()
    }

    fun setRedRadius(radius: Float) {
        redRadius = radius
    }

    private val controllerListener: ControllerListener<in ImageInfo>? =
        object : BaseControllerListener<ImageInfo?>() {
            override fun onFinalImageSet(
                id: String,
                imageInfo: ImageInfo?,
                anim: Animatable?
            ) {
                if (imageInfo == null) {
                    return
                }
                val qualityInfo = imageInfo.qualityInfo
                imgInfo = imageInfo
                FLog.d(
                    "Final image received! " +
                            "Size %d x %d",
                    "Quality level %d, good enough: %s, full quality: %s",
                    imageInfo.width,
                    imageInfo.height,
                    qualityInfo.quality,
                    qualityInfo.isOfGoodEnoughQuality,
                    qualityInfo.isOfFullQuality
                )
            }

            override fun onIntermediateImageSet(
                id: String,
                imageInfo: ImageInfo?
            ) {
                if (imageInfo == null) {
                    return
                }
                val qualityInfo = imageInfo.qualityInfo
                imgInfo = imageInfo
                FLog.d(
                    "Final image received! " +
                            "Size %d x %d",
                    "Quality level %d, good enough: %s, full quality: %s",
                    imageInfo.width,
                    imageInfo.height,
                    qualityInfo.quality,
                    qualityInfo.isOfGoodEnoughQuality,
                    qualityInfo.isOfFullQuality
                )
            }

            override fun onFailure(
                id: String,
                throwable: Throwable
            ) {
            }
        }

    fun setUri(fileUri: Uri?) {
        var uri = fileUri
        if (fileUri.toString().startsWith("/storage")) {
            uri = Uri.parse("file://" + fileUri.toString())
        }

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(uri),
                null,
                options
            )
            realHeight = options.outHeight.toFloat()
            realWidth = options.outWidth.toFloat()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val controller: DraweeController = Fresco.newDraweeControllerBuilder()
            .setControllerListener(controllerListener)
            .setUri(uri) // other setters
            .build()
        this.controller = controller
    }

    override fun onTouchEvent(event: MotionEvent): Boolean { // Let the ScaleGestureDetector inspect all events.
        var retVal = mScaleDetector.onTouchEvent(event)
        retVal = mGestureDetector.onTouchEvent(event) || retVal
        return retVal || super.onTouchEvent(event)
    }

    public override fun onDraw(canvas: Canvas) {
        if (imgInfo != null) {
            val w = this.width.toFloat()
            val h = this.height.toFloat()
            val w0 = imgInfo!!.width.toFloat()
            val h0 = imgInfo!!.height.toFloat()
            if (h > 0 && h0 > 0) {
                val dst =
                    RectF(
                        0.0f,
                        0.0f,
                        this@PhotoView.width.toFloat(),
                        this@PhotoView.height.toFloat()
                    )
                drawMatrix = Matrix()
                val outMatrix = Matrix()
                this.hierarchy.actualImageScaleType!!.getTransform(
                    outMatrix,
                    Rect(0, 0, w.toInt(), h.toInt()),
                    w0.toInt(),
                    h0.toInt(),
                    w0 / 2,
                    h0 / 2
                )
                preMatrix = outMatrix
                imgWidth = if (realWidth.toInt() * w0.toInt() == realHeight.toInt() * h0.toInt()) {
                    h0
                } else {
                    w0
                }
                imgInfo = null
            }
        }
        if (drawMatrix == null) {
            drawMatrix = Matrix()
        }
        val saveCount = canvas.save()
        canvas.save()
        canvas.concat(drawMatrix)
        super.onDraw(canvas)
        var realScale = 1f
        if (realWidth > 0 && imgWidth > 0) {
            realScale = imgWidth / realWidth
        }
        if (preMatrix != null) {
            val dst = FloatArray(2)
            for (g in pointGreen) {
                val point =
                    floatArrayOf(g!![0] * realScale, g[1] * realScale)
                preMatrix!!.mapPoints(dst, point)
                canvas.drawCircle(
                    dst[0],
                    dst[1],
                    preMatrix!!.mapRadius(g[2]) * realScale,
                    greenPaint
                )
            }
        }
        for (p in pointRed) {
            val dst = floatArrayOf(p!!.x, p.y)
            canvas.drawCircle(
                dst[0],
                dst[1],
                preMatrix!!.mapRadius(redRadius) * realScale,
                redPaint
            )
        }
        canvas.restoreToCount(saveCount)
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            lastFocusX = detector.focusX
            lastFocusY = detector.focusY
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val transformationMatrix = Matrix()
            val focusX = detector.focusX
            val focusY = detector.focusY
            //Zoom focus is where the fingers are centered,
            transformationMatrix.postTranslate(-focusX, -focusY)
            var scaleFactor = detector.scaleFactor
            val curScale = drawMatrix!!.mapRadius(1f)
            if (curScale * scaleFactor > maxZoom) {
                scaleFactor = maxZoom / curScale
            }
            if (curScale * scaleFactor < minZoom) {
                scaleFactor = minZoom / curScale
            }
            transformationMatrix.postScale(scaleFactor, scaleFactor)
            /* Adding focus shift to allow for scrolling with two pointers down. Remove it to skip this functionality. This could be done in fewer lines, but for clarity I do it this way here */ //Edited after comment by chochim
            val focusShiftX = focusX - lastFocusX
            val focusShiftY = focusY - lastFocusY
            transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY)
            drawMatrix!!.postConcat(transformationMatrix)
            lastFocusX = focusX
            lastFocusY = focusY
            checkBound()
            invalidate()
            return true
        }
    }

    fun checkCross(x: Float, y: Float): Boolean {
        if (preMatrix != null) {
            var realScale = 1f
            if (realWidth > 0 && imgWidth > 0) {
                realScale = imgWidth / realWidth
            }
            for (i in pointGreen.indices) {
                val g = pointGreen[i]
                val point =
                    floatArrayOf(g!![0] * realScale, g[1] * realScale)
                val dst = FloatArray(2)
                preMatrix!!.mapPoints(dst, point)
                val radius = preMatrix!!.mapRadius(g[2]) * realScale
                if ((x - dst[0]) * (x - dst[0]) + (y - dst[1]) * (y - dst[1]) <= radius * radius) {
                    pointGreen.remove(g)
                    return true
                }
            }
        }
        return false
    }

    fun checkRedCross(x: Float, y: Float): Boolean {
        var realScale = 1f
        if (realWidth > 0 && imgWidth > 0) {
            realScale = imgWidth / realWidth
        }
        for (i in pointRed.indices) {
            val g = pointRed[i]
            val radius = preMatrix!!.mapRadius(redRadius) * realScale
            if ((x - g!!.x) * (x - g.x) + (y - g.y) * (y - g.y) <= radius * radius) {
                pointRed.remove(g)
                return true
            }
        }
        return false
    }

    fun checkBound() {
        val w = this@PhotoView.width.toFloat()
        val h = this@PhotoView.height.toFloat()
        val scale = drawMatrix!!.mapRadius(1f)
        val point = floatArrayOf(
            0f, 0f, 0f, h,
            w, 0f,
            w, h
        )
        val dst = FloatArray(8)
        drawMatrix!!.mapPoints(dst, point)
        val transformationMatrix = Matrix()
        var error = false
        if (dst[0] > 0) {
            transformationMatrix.postTranslate(-dst[0], 0f)
            error = true
        }
        if (dst[0] < w - scale * w) {
            transformationMatrix.postTranslate(w - scale * w - dst[0], 0f)
            error = true
        }
        if (dst[1] > 0) {
            transformationMatrix.postTranslate(0f, -dst[1])
            error = true
        }
        if (dst[1] < h - scale * h) {
            transformationMatrix.postTranslate(0f, -dst[1] + (h - scale * h))
            error = true
        }
        if (error) {
            drawMatrix!!.postConcat(transformationMatrix)
        }
    }

    private val mGestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (redRadius == 0f) {
                return false
            }
            val point = floatArrayOf(e.x, e.y)
            val dst = FloatArray(2)
            val invert = Matrix()
            drawMatrix!!.invert(invert)
            invert.mapPoints(dst, point)
            val bounds = RectF()
            this@PhotoView.hierarchy.getActualImageBounds(bounds)
            if (!bounds.contains(dst[0], dst[1])) {
                return false
            }
            if (!checkCross(dst[0], dst[1])) {
                if (!checkRedCross(dst[0], dst[1])) {
                    pointRed.add(PointF(dst[0], dst[1]))
                }
            }
            invalidate()
            onReceiveNativeEvent()
            return true
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            drawMatrix!!.postTranslate(-distanceX, -distanceY)
            checkBound()
            invalidate()
            return true
        }
    }


    fun onReceiveNativeEvent() { //        WritableMap event = Arguments.createMap();
//        event.putInt("count", pointRed.size() + pointGreen.size());
//
//        ReactContext reactContext = (ReactContext) getContext();
//        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
//                getId(),
//                "onTap",
//                event);
    }

    init {
        this.hierarchy.actualImageScaleType = ScalingUtils.ScaleType.FIT_CENTER
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mGestureDetector = GestureDetectorCompat(context, mGestureListener)
        redPaint = Paint()
        redPaint.color = Color.parseColor("#e43636")
        redPaint.alpha = 200
        redPaint.style = Paint.Style.FILL
        greenPaint = Paint()
        greenPaint.color = Color.parseColor("#36e436")
        greenPaint.alpha = 200
        greenPaint.style = Paint.Style.FILL
    }

}