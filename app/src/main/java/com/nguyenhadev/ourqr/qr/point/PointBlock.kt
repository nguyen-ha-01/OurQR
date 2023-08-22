package com.nguyenhadev.ourqr.qr.point

import android.graphics.Bitmap
import android.graphics.Path

interface PointBlock {
    fun createSingleBlockPath(size: Int):Path
    fun createRelatedLeftBlockPath(size: Int):Path
    fun createRelatedRightBlockPath(size: Int):Path
    fun createBlock(size:Int): Bitmap
}