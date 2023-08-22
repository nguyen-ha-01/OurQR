package com.nguyenhadev.ourqr.qr

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import androidx.work.Data
import androidx.work.ListenableWorker.Result.failure
import androidx.work.ListenableWorker.Result.success
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nguyenhadev.ourqr.db.PATH
import java.io.File
import java.io.FileOutputStream

class CreateWork(val ctx: Context, val params : WorkerParameters) : Worker(ctx,  params){
    override fun doWork(): Result {
        val inputContent = inputData.getString(InputString)
        val nameForOutPut = inputData.getString(Name)
        val data = Data.Builder()
        data.putString("inputContent", inputContent)
        data.putString("nameForOutPut",  nameForOutPut)
//        val path  = File(Environment.getExternalStorageDirectory().absolutePath, PATH )
//
//        if (path.exists() != true) path.mkdir()
//        data.putString("path" ,if (path.exists())"ed"+ path.isDirectory.toString() else "nope")
       return success(data.build())
    }

    private fun run():Result{
        val data = Data.Builder()
        try {

            val inputContent = inputData.getString(InputString)
            val nameForOutPut = inputData.getString(Name)
            //check input
            if (inputContent == null){ data.putString(Exception,  "input's null");return failure(data.build())
            }
            //now, create a simple qr
            val writer = QrWriter()
            val bitmap = writer.write(inputContent)
            //then, save that into store
            //check permission
            if(ctx.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
                data.putString(Exception, "permission is denied")
                return failure(data.build())
            }
            val path  = File(Environment.getExternalStorageDirectory().absolutePath, PATH )
            if (path.exists() != true) path.mkdir()
            val file  = File(path, nameForOutPut+".jpeg")
            val stream =       FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,  100,  stream)
            stream.close()
            data.putString(OutputPath, file.path)
            return Result.success(data.build())



        }catch (e: Exception){
            data.putString(Exception,  e.message)
            return failure(data.build())
        }finally {
            return failure()
        }
    }
    companion object{
        const val InputString = "INPUT_STRING"
        const val Exception = "Exception"
        const val Name = "Name"
        const val OutputPath = "Path"
    }
}
