package com.carlosjimz87.pdfrenderer.components

import android.R.color
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.carlosjimz87.pdfrenderer.Constants
import com.carlosjimz87.pdfrenderer.utils.TAG
import kotlin.math.abs


class PdfImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {
    private var callback: SwipeCallback? = null
    private lateinit var gestureDetector: GestureDetector
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private val minScaleFactor = 1.0f
    private val maxScaleFactor = 3.0f
    private var isSwipeEvent = false  // Flag to detect if it's a swipe event

    private var lastX: Int = 0
    private var lastY: Int = 0

    private var lastTouchPoint: PointF? = null


    fun init(context: Context, setLightOrDarkMode: Int? = null) {
        this.callback = context as SwipeCallback
        setLightOrDarkMode?.let { setStyleForImageView(context, it) }
        initGestureDetectors(context)
        initTouchListener()
    }

    private fun setStyleForImageView(context: Context, mode: Int) {
        if (mode == AppCompatDelegate.MODE_NIGHT_YES) {
            setBackgroundColor(ContextCompat.getColor(context, color.black))
        } else {
            setBackgroundColor(ContextCompat.getColor(context, color.white))
        }
    }

    private fun initGestureDetectors(context: Context) {
        gestureDetector = GestureDetector(context, GestureListener().apply {
            onSwipeRight = {
                callback?.swipeRight()
            }
            onSwipeLeft = {
                callback?.swipeLeft()
            }
            onDoubleTap = {
                // Handle double-tap zoom here. For example:
                if (scaleFactor < maxScaleFactor) {
                    scaleFactor *= 2f
                    applyZoom(scaleFactor)
                } else {
                    scaleFactor = 1f
                    applyZoom(scaleFactor)
                }
            }
        })

        scaleGestureDetector = ScaleGestureDetector(context, object : SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                applyZoom(detector)
                return true
            }
        })
    }


    private fun initTouchListener() {
    }

    private fun applyZoom(detector: ScaleGestureDetector) {
        val scale = 1 - detector.scaleFactor
        val prevScale: Float = scaleFactor
        scaleFactor += scale
        // Minimum scale condition:
        if (scaleFactor < 0.1f) {
            scaleFactor = 0.1f
        }
        // Maximum scale condition:
        if (scaleFactor > 10f) {
            scaleFactor = 10f
        }
        val scaleAnimation = ScaleAnimation(
            1f / prevScale,
            1f / scaleFactor,
            1f / prevScale,
            1f / scaleFactor,
            detector.focusX,
            detector.focusY
        )
        scaleAnimation.duration = 0
        scaleAnimation.fillAfter = true
        this@PdfImageView.startAnimation(scaleAnimation)
    }

    private fun applyZoom(scaleFactor: Float) {
        Log.d(TAG, "changing zoom: $scaleFactor")
        val matrix = Matrix(imageMatrix)
        matrix.setScale(scaleFactor, scaleFactor, width / 2f, height / 2f)
        imageMatrix = matrix
    }

    override fun onDraw(canvas: Canvas) {
        // Apply no color filtering to prevent color inversion
        colorFilter = null

        super.onDraw(canvas)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // No need for ACTION_DOWN or ACTION_MOVE anymore, as we're not doing panning

        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else if (scaleGestureDetector.onTouchEvent(event)) {
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
        var onDoubleTap: () -> Unit = {}

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val distanceX = e2.x - e1.x
            val distanceY = e2.y - e1.y
            if (abs(distanceX) > abs(distanceY) && abs(distanceX) > Constants.SWIPE_DISTANCE_THRESHOLD
                && abs(velocityX) > Constants.SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }
                return true
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            onDoubleTap()
            return true
        }
    }


}
