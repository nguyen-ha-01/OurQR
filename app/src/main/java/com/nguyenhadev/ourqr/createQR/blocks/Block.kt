package com.nguyenhadev.ourqr.createQR.blocks

import android.graphics.Bitmap
import android.graphics.Path

interface Block {
    fun createPath():Path
    fun createBlock(): Bitmap
}