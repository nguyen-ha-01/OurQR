package com.nguyenhadev.ourqr.qr

import android.graphics.Matrix
import android.util.Log
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer

 class Analyzer(val listener: (Int,Int,ByteArray,ByteArray,ByteArray)->Unit):ImageAnalysis.Analyzer {
     fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }
    override fun analyze(image: ImageProxy) {
        val red = image.planes.get(0).buffer.toByteArray()
        val green =image.planes.get(1).buffer.toByteArray()
        val blue = image.planes.get(2).buffer.toByteArray()


        try {
            listener(image.width,  image.height,red, green, blue)
        }catch (e:Exception){
            Log.e("analyzer",  e.message.toString())
        }finally {
            image.close()
        }


    }



    override fun getDefaultTargetResolution(): Size? {
        return super.getDefaultTargetResolution()
    }

    override fun updateTransform(matrix: Matrix?) {
        super.updateTransform(matrix)
    }
}
