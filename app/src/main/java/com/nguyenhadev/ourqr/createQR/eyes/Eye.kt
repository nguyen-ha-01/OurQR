package com.nguyenhadev.ourqr.createQR.eyes

import android.graphics.Bitmap
import android.graphics.Path

interface Eye {
    fun createPath():Path
    fun createEye(): Bitmap
}