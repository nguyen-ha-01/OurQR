package com.nguyenhadev.ourqr

import android.util.Log
import com.nguyenhadev.ourqr.qr.QrWriter
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val writer = QrWriter()
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    @Test
    fun checkQRwriter(){
        var writer = QrWriter()
        val mtx = writer.encode("draw shape on bitmap android")
        val size = writer.getBlockSetSize(mtx)
        println("block size: ${size}")
        assertNotEquals(0, size)
        Log.d("DEBUG", size.toString())
    }
    @Test
    fun checkRow(){

        val mtx = writer.encode("draw shape on bitmap android")
        val next = writer.nextPostSet(mtx, 146)
        println("next: ${next-146}")
        println("row: ${mtx.getRow(mtx.height/2,null).size}")
        println("row size ${mtx.rowSize}")

    }
    @Test
    fun check(){
        val mtx = writer.encode("draw shape on bitmap android")
        var x = 0
        var y = 0
        writer.reshape(mtx)
        println(mtx.toString("1","0"))


    }
}