package com.nguyenhadev.ourqr.qr.point

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath

class SolidPoint(val _color: Int):PointBlock {
    override fun createSingleBlockPath(size:Int): android.graphics.Path {
            val width= size.toFloat()
            val path = Path()
            path.apply {
                moveTo(width/8,0f)
                arcTo(Rect(0.25f*width, 0f,width, 0.75f*width), 270f,90f,false)
                lineTo(width,0.75f*width+width/8)
                arcTo(Rect(0.25f*width, 0.25f*width, width,width),0f,90f,false)
                lineTo(width/8,width)
                arcTo(Rect(0f, 0.25f*width, 0.75f*width,width), 90f, 90f,false)
                lineTo(0f,0.25f*width)
                arcTo(Rect(0f, 0f, 0.75f*width,0.75f*width),180f,90f, false)
                lineTo(0.75f*width, 0f)
            }
            return path.asAndroidPath()
        }
    //note this fun draw a block with area > normal block so we have to minus 50% block size offset
    override fun createRelatedLeftBlockPath(size: Int): android.graphics.Path {
        val _size = (3*size/2f)
        val newPath = Path()
        newPath.apply {
            moveTo(_size/3f,0f)
            lineTo(_size/3f, _size/3f)
            lineTo(_size/6f,_size/3f )
            moveTo(_size/3f, _size/3f)
            lineTo(0f, _size/3f)
            addArc(Rect(0f, _size/3f, _size/3f, 2*_size/3f), 270f,90f)
            arcTo(Rect(_size/3f, (2*_size/3f)/4f+_size/3f, _size/3f+(2*_size/3f)*3/4 ,_size*1f),180f,-90f, false )
            arcTo(Rect((2*_size/3f)/4f+_size/3f, (2*_size/3f)/4f+_size/3f, _size*1f,_size*1f),90f,-90f,false)
            arcTo(Rect(_size/3f+(2*_size/3f)/4f, 0f+ _size/3f,_size*1f, _size/3f+3*(2*_size/3f)/4f ), 0f,  -90f,false)
            arcTo(Rect(_size/3f,0f,2f*_size/3f, _size/3f), 90f,90f,false)
            lineTo(_size/3f, 0f)
            this.close()
        }
        return newPath.asAndroidPath()
    }

    override fun createRelatedRightBlockPath(size: Int): android.graphics.Path {
        val _size = size*3/2f
        val newPath = Path()
        newPath.apply {
            arcTo(Rect(_size/3f-_size/3f, 0f,_size*2/3f-_size/3f,_size/3f), 0f,90f,false)
            lineTo((_size*2/3f)/2f-_size/3f,_size/3f)
            lineTo(_size/3f-_size/3f,_size*2/3f)
            lineTo(_size*2/3f-_size/3f, _size*2/3f)
            lineTo(2*_size/3f-_size/3f, _size/3f)
            arcTo(Rect(2*_size/3f-_size/3f,  _size/3f, _size*1f-_size/3f, 2*_size/3f), -180f,90f,false)
            lineTo(2*_size/3f-_size/3f,_size/3f)
            lineTo(2*_size/3f-_size/3f,0f)
            this.close()
        }
        return newPath.asAndroidPath()
    }

    override fun createBlock(size: Int): Bitmap {

        val newPath = createSingleBlockPath(size)
        val paint = Paint().apply {
            this.color = _color
        }
        val b = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888)
        val canvas_ = Canvas(b).apply {
            drawPath(newPath, paint)
        }
        canvas_.save()
        return b
    }
    fun createRelatedLeftBlock(size: Int):Bitmap{
        val newPath = createRelatedLeftBlockPath(size)
        val paint = Paint().apply {
            this.color = _color
        }
        val b = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888)
        val canvas_ = Canvas(b).apply {
            drawPath(newPath, paint)
        }
        canvas_.save()
        return b
    }
    fun createCollectedBlock(size:Int):Bitmap{
        val bm = Bitmap.createBitmap(size,size, Bitmap.Config.ARGB_8888)
        for (x in 0..size-1){
            for (y in 0..size-1){
                bm.setPixel(x,y,_color)
            }
        }
        return bm
    }
    fun createRelatedRightBlock(size:Int):Bitmap{
        val newPath = createRelatedRightBlockPath(size)
        val paint = Paint().apply {
            this.color = _color
        }
        val b = Bitmap.createBitmap(size,size,Bitmap.Config.ARGB_8888)
        val canvas_ = Canvas(b).apply {
            drawPath(newPath, paint)
        }
        canvas_.save()
        return b
    }
}