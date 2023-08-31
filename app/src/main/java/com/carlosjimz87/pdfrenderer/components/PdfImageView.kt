package com.carlosjimz87.pdfrenderer.components

import android.R.color
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.carlosjimz87.pdfrenderer.Constants
import com.carlosjimz87.pdfrenderer.utils.TAG
import kotlin.math.abs

class PdfImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {
    private var callback: SwipeCallback? = null
    private lateinit var gestureDetector : GestureDetector
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private val minScaleFactor = 1.0f
    private val maxScaleFactor = 3.0f

    init {
        initGestureDetectors(context)
    }

    fun setStyleForImageView(context: Context, mode: Int) {
        if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
            setBackgroundColor(ContextCompat.getColor(context, color.black))
        } else {
            setBackgroundColor(ContextCompat.getColor(context, color.white))
        }
    }

    fun setCallback(callback: SwipeCallback) {
        this.callback = callback
    }

    private fun initGestureDetectors(context: Context) {

        gestureDetector = GestureDetector(context, GestureListener()
            .apply {
            onSwipeRight = {
                callback?.swipeRight()
            }
            onSwipeLeft = {
                callback?.swipeLeft()
            }
        })

        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(minScaleFactor, maxScaleFactor)
                applyZoom(scaleFactor)
                return true
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                super.onScaleEnd(detector)
                invalidate()
            }
        })
    }

    private fun applyZoom(scaleFactor: Float) {
        Log.d(TAG, "changing zoom: $scaleFactor")
        val matrix = Matrix(imageMatrix)
        matrix.postScale(scaleFactor, scaleFactor, width / 2f, height / 2f)
        imageMatrix = matrix
    }



    override fun onDraw(canvas: Canvas) {
        // Apply no color filtering to prevent color inversion
        colorFilter = null

        super.onDraw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event) || scaleGestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    interface SwipeCallback {
        fun swipeLeft()
        fun swipeRight()
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        var onSwipeRight: () -> Unit = {}
        var onSwipeLeft: () -> Unit = {}

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            Log.d(TAG, "onFling: $e1 $e2 $velocityX $velocityY")
            val distanceX = e2.x - e1.x
            val distanceY = e2.y - e1.y
            if (abs(distanceX) > abs(distanceY) && abs(
                    distanceX
                ) > Constants.SWIPE_DISTANCE_THRESHOLD && abs(velocityX) > Constants.SWIPE_VELOCITY_THRESHOLD
            ) {
                if (distanceX > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }
                return true
            }
            return false
        }
    }
}
