package com.example.ourqr

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nguyenhadev.ourqr.qr.QrWriter

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.ourqr", appContext.packageName)
    }
    @Test
    fun checkQRwriter(){
        var writer = QrWriter()
        val mtx = writer.encode("draw shape on bitmap android")
        val size = writer.getBlockSetSize(mtx)
        println("block size: ${size}")
        Log.d("DEBUG", size.toString())
    }
}