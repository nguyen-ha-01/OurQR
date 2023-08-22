package com.nguyenhadev.ourqr.createQR.frames

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.toArgb

class RoundedFrame(val blockSize:Int, val blockNum: Int):Frame {
    override fun createPath(): android.graphics.Path {
        val path = Path().apply {
            moveTo(2f*blockSize, 0f)
            arcTo(Rect(0f,0f,  2f*blockSize, 2f*blockSize),270f, -90f, false)
            lineTo(0f, (blockNum-1)*blockSize*1f)
            arcTo(Rect(0f, (blockNum-2)*blockSize*1f,  2f*blockSize, blockNum*blockSize*1f),  180f, -90f, false)
            lineTo((blockNum-1)*blockSize*1f, blockNum*blockSize*1f)
            arcTo(Rect((blockNum-2)*blockSize*1f,(blockNum-2)*blockSize*1f,   blockNum*blockSize*1f,blockNum*blockSize*1f),90f, -90f, false)
            lineTo(blockNum*blockSize*1f, blockSize*1f)
            arcTo(Rect((blockNum-2)*blockSize*1f,  0f, (blockNum)*blockSize*1f,  2*blockSize*1f),  0f, -90f, false)
            close()
        }
        val unsetPath =  Path().apply {
            moveTo(2f*blockSize, blockSize*1f)
            arcTo(Rect(blockSize*1f,  blockSize*1f, 3f*blockSize, blockSize*3f ), 270f, -90f, false)
            arcTo(Rect(blockSize*1f,(blockNum-3)*blockSize*1f,3f*blockSize ,(blockNum-1)*blockSize*1f ), 180f, -90f, false)
            arcTo(Rect((blockNum-3)*blockSize*1f, (blockNum-3)*blockSize*1f,(blockNum-1)*blockSize*1f,(blockNum-1)*blockSize*1f ), 90f, -90f, false)
            arcTo(Rect((blockNum-3)*blockSize*1f,  blockSize*1f, (blockNum-1)*blockSize*1f,  3*blockSize*1f),  0f, -90f, false)
            close()
        }
        val corePath = Path().apply {
            moveTo(3f*blockSize, blockSize*2f)
            arcTo(Rect(blockSize*2f,  blockSize*2f, 4f*blockSize, blockSize*4f ), 270f, -90f, false)
            arcTo(Rect(blockSize*2f,(blockNum-4)*blockSize*1f,4f*blockSize ,(blockNum-2)*blockSize*1f ), 180f, -90f, false)
            arcTo(Rect((blockNum-4)*blockSize*1f, (blockNum-4)*blockSize*1f,(blockNum-2)*blockSize*1f,(blockNum-2)*blockSize*1f ), 90f, -90f, false)
            arcTo(Rect((blockNum-4)*blockSize*1f,  blockSize*2f, (blockNum-2)*blockSize*1f,  4*blockSize*1f),  0f, -90f, false)
            close()}
        return path.asAndroidPath()
    }

    override fun createFrame(): Bitmap {
        TODO("Not yet implemented")
    }
    fun createSimpleFrame(blockSize: Int, blockNum:Int, setColor: Color = Color(0, 82,161), unsetColor: Color = Color(255, 255, 255, 255)):Bitmap{
        val setPaint = Paint().apply {
            color = setColor.toArgb()
        }
        val unsetPaint = Paint().apply {
            color = unsetColor.toArgb()
        }
        val frameSize = blockSize*blockNum
        val bitmap = Bitmap.createBitmap(frameSize, frameSize, Bitmap.Config.ARGB_8888)
        val path = Path().apply {
            moveTo(2f*blockSize, 0f)
            arcTo(Rect(0f,0f,  2f*blockSize, 2f*blockSize),270f, -90f, false)
            lineTo(0f, (blockNum-1)*blockSize*1f)
            arcTo(Rect(0f, (blockNum-2)*blockSize*1f,  2f*blockSize, blockNum*blockSize*1f),  180f, -90f, false)
            lineTo((blockNum-1)*blockSize*1f, blockNum*blockSize*1f)
            arcTo(Rect((blockNum-2)*blockSize*1f,(blockNum-2)*blockSize*1f,   blockNum*blockSize*1f,blockNum*blockSize*1f),90f, -90f, false)
            lineTo(blockNum*blockSize*1f, blockSize*1f)
            arcTo(Rect((blockNum-2)*blockSize*1f,  0f, (blockNum)*blockSize*1f,  2*blockSize*1f),  0f, -90f, false)
            close()
        }
        val unsetPath =  Path().apply {
            moveTo(2f*blockSize, blockSize*1f)
            arcTo(Rect(blockSize*1f,  blockSize*1f, 3f*blockSize, blockSize*3f ), 270f, -90f, false)
            arcTo(Rect(blockSize*1f,(blockNum-3)*blockSize*1f,3f*blockSize ,(blockNum-1)*blockSize*1f ), 180f, -90f, false)
            arcTo(Rect((blockNum-3)*blockSize*1f, (blockNum-3)*blockSize*1f,(blockNum-1)*blockSize*1f,(blockNum-1)*blockSize*1f ), 90f, -90f, false)
            arcTo(Rect((blockNum-3)*blockSize*1f,  blockSize*1f, (blockNum-1)*blockSize*1f,  3*blockSize*1f),  0f, -90f, false)
            close()
        }
        val corePath = Path().apply {
            moveTo(3f*blockSize, blockSize*2f)
            arcTo(Rect(blockSize*2f,  blockSize*2f, 4f*blockSize, blockSize*4f ), 270f, -90f, false)
            arcTo(Rect(blockSize*2f,(blockNum-4)*blockSize*1f,4f*blockSize ,(blockNum-2)*blockSize*1f ), 180f, -90f, false)
            arcTo(Rect((blockNum-4)*blockSize*1f, (blockNum-4)*blockSize*1f,(blockNum-2)*blockSize*1f,(blockNum-2)*blockSize*1f ), 90f, -90f, false)
            arcTo(Rect((blockNum-4)*blockSize*1f,  blockSize*2f, (blockNum-2)*blockSize*1f,  4*blockSize*1f),  0f, -90f, false)
            close()}
        Canvas(bitmap).apply {
            drawPath(path.asAndroidPath(), setPaint)
            drawPath(unsetPath.asAndroidPath() ,unsetPaint)
            drawPath(corePath.asAndroidPath(),  setPaint)
//                           drawLine(blockSize*1f,0f  ,  blockSize*1f,this.height*1f , paint)
//                           drawLine(0f, blockSize*1f, height*1f, blockSize*1f, paint)
//                           drawLine(0f, height-blockSize*1f, height*1f, height-blockSize*1f, paint)
//                           drawLine(height-blockSize*1f,  0f, height-blockSize*1f, height*1f, paint)
            save()


        }
        return bitmap
    }
}