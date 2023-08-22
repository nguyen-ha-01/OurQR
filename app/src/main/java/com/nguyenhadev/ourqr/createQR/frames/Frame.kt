package com.nguyenhadev.ourqr.createQR.frames

import android.graphics.Bitmap
import android.graphics.Path

interface Frame {
    fun createPath(): Path
    fun createFrame():Bitmap
}