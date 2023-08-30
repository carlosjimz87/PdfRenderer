package com.carlosjimz87.pdfrenderer.listeners

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

class GestureListener : GestureDetector.SimpleOnGestureListener() {

    var onSwipeRight: () -> Unit = {}
    var onSwipeLeft: () -> Unit = {}

    private val SWIPE_DISTANCE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val distanceX = e2.x - e1.x
        val distanceY = e2.y - e1.y
        if (abs(distanceX) > abs(distanceY) && abs(
                distanceX
            ) > SWIPE_DISTANCE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
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