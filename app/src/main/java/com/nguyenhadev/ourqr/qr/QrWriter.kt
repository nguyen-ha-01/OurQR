package com.nguyenhadev.ourqr.qr

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint

import android.util.Log

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.scale
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.common.BitArray
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import java.math.RoundingMode
import java.text.DecimalFormat
import androidx.core.graphics.set


class QrWriter() {
    private val writer = QRCodeWriter()
    private var width: Int = 600
    private var height:Int = 600
    fun encode(content: String):BitMatrix{
        val hints = mutableMapOf<EncodeHintType, String>()
        hints.apply {
            put(EncodeHintType.CHARACTER_SET, "utf-8")
        }

        return writer.encode(
            content,  BarcodeFormat.QR_CODE, width, height, hints
        )
    }

    private fun colorDecode(color: Int ): androidx.compose.ui.graphics.Color{
        val  A: Int = color shr 24 and 0xff // or color >>> 24
        val R: Int = color shr 16 and 0xff
        val G: Int = color shr 8 and 0xff
        val B: Int = color and 0xff
        return  Color(red = R, green = G, blue = B, alpha = A)
    }

    private fun mashColor(colorBase:androidx.compose.ui.graphics.Color, bitValue: Boolean ) : Int{
        // TODO: i just change alpha
        val a = colorBase.alpha
        val r = colorBase.red
        val b = colorBase.blue
        val g = colorBase.green
        //more dark
        if(!bitValue){
            if (a<0.75 && a > 0.5) return  android.graphics.Color.argb(a+0.25f,r,g,b )
            else if(a>0.75) return android.graphics.Color.argb(a,r,g,b )
            else if (a< 0.5 ) return android.graphics.Color.argb(a+0.5f,r,g,b )
        }else {
            //more light
            if (a<0.75 && a > 0.5) return  android.graphics.Color.argb(a-0.25f,r,g,b )
            else if(a>0.75) return android.graphics.Color.argb(a/2,r,g,b )
            else if (a< 0.5 ) return android.graphics.Color.argb(a/2,r,g,b )
        }
        return colorBase.toArgb()
    }
    fun mash(matrix: BitMatrix,  filePath: String): Bitmap{
        try {
            val bitmap = BitmapFactory.decodeFile(filePath)
            return mash(matrix, bitmap)
        }catch (e: Exception){
            throw e
        }
    }
    fun each(run: (Int,  Int)->Unit){
        for (x in 0..width-1){
            for(y in 0..height-1){
                run(x,y)
            }}
    }
    fun mash(matrix: BitMatrix, bitmap: Bitmap):Bitmap{
        val icon = bitmap.scale(width,height)
        val mashedBitmap  = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)
        for (x in 0..width-1){
            for(y in 0..height-1){
                var pixel = icon.getPixel(x,y)

                var pixelValue = matrix.get(x,y)// 0 or 1
                val base = colorDecode(pixel)
                val outColor = mashColor(base, pixelValue)
                mashedBitmap.setPixel(x,y,outColor)
            }
        }
        return mashedBitmap
    }
    fun write(content: String, bitmap: Bitmap): Bitmap{
        val mtx = encode(content)
        return  mash(mtx,bitmap)
    }
    fun write(content:String , bitmapPath: String ):Bitmap{
        val mtx = encode(content)
        return mash(mtx,  bitmapPath)
    }
    fun write(content: String ):Bitmap{
        val mtx = encode(content)
        val bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)
        each {x,y->
        bitmap.setPixel(x,y,if(mtx.get(x,y))android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
        return bitmap
    }

    fun BitArray.each(action: (Int,Boolean)-> Unit){
    for(l in 0..this.size-1) {
        action(l, get(l))
    }
    }
    fun getBlockSetSize(mtx: BitMatrix):Int{
        var size =  0

        mtx.getRow(mtx.height/2, null).each{x, value->
            var tempSize = 0
            if(!value){
                val nextPosSet : Int= nextPostSet(mtx, x)
                 size = nextPosSet-x
                return@each
            }

        }
        return size
    }
    fun nextPostSet(mtx: BitMatrix, x: Int): Int{
        for(i in (x..mtx.getRow(mtx.height/2,null).size)){
            if(!mtx.get(i+1,  mtx.height/2)) return  i
        }
        return x
    }

    // TODO: filter get error with short content encoding
    //explain get bottom right on bit is fail but top left is still true
    //filter is ok now but rowSize get problem so i have to create a new one

    fun filter(mtx: BitMatrix): BitMatrix{
        var startX =mtx.topLeftOnBit[0]
        var startY = mtx.topLeftOnBit[1]
        var endX = mtx.getBottomRight().first
        var endY = mtx.getBottomRight().second
        var _mtx = BitMatrix(endX+1-startX)
        try {
            for (y in startY..endY) {
                for(x in startX..endX ){
                    if(x-startX<_mtx.width){
                        if(mtx.get(x,y))_mtx.set(x-startX,y-startY)
                        else _mtx.unset(x-startX,y-startY)
                    }
                }
            }
        }catch (e: Exception){
            Log.e("ERROR", e.message.toString())
        }

        return _mtx
    }
    
    fun reshape(mtx:BitMatrix):BitMatrix{
        val solidMtx = filter(mtx)
        // TODO: mtx.rowsSze have problems with small row size
        var blockSize = solidMtx.getRowSizeMe()
        val newSize = (solidMtx.height)/(blockSize)
        var matrix = BitMatrix(newSize)
        for (y in 0..solidMtx.height-1  step blockSize) {
            for(x in 0..solidMtx.width-1 step blockSize ){
                val newX= ((x).toFloat()/solidMtx.width.toFloat())*newSize.toFloat().roundFloor()
                val newY = ((y).toFloat()/solidMtx.height.toFloat())*newSize.toFloat().roundFloor()
                Log.d("xy", "${x} ${y}")
                Log.d("z", "${newX} ${newY}")
                if (solidMtx.get(x+1,y+1))matrix.set(newX.toInt(),newY.toInt())
                else matrix.unset(newX.toInt(),newY.toInt())
            }
            Log.d("line","line $y".toString())
        }
        return matrix
    }
    private  fun Float.roundFloor():Int{
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(this).toInt()

    }
    fun getFrameSizeInBitmap(mtx: BitMatrix,  bitSize: Int) = mtx.getSizeOfFrame()*bitSize
    fun findPostOfFrame(mtx: BitMatrix):List<Pair<Int,Int>>{
        var list = mutableListOf<Pair<Int,Int>>()
        val frameSize = mtx.getSizeOfFrame()
        val topLeftFrame = Pair(0,0)
        list.add(topLeftFrame)
        val topRightFrame = Pair(mtx.width-1-frameSize+1, 0)
        list.add(topRightFrame)
        val bottomLeftFrame = Pair(0,mtx.height-1-frameSize+1)
        list.add(bottomLeftFrame)
        return list.toList()
    }

    fun generateQR(text:String, frameImg:Bitmap, singleBlock:Bitmap, relatedLeftBlock:Bitmap,relatedRightBlock:Bitmap, collectedBlock:Bitmap, blockSize:Int = 40):Bitmap{
        try {
            val encoding = encode(text)
            val reshaping = reshape(encoding)
            val blockNum = reshaping.width
            val bitmap = Bitmap.createBitmap(
                blockNum * blockSize,
                blockNum * blockSize,
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)
            val paint = Paint().apply {
                color = android.graphics.Color.BLUE
            }
            reshaping.each { x, y, v ->
                if(v){
                    canvas.drawBitmap(
                    singleBlock.scale(blockSize, blockSize, true),
                    x * blockSize * 1f,
                    y * blockSize * 1f,
                    paint)


                        val relatedList = reshaping.checkingRelatedBlock(x,y)
                        relatedList.forEach { post->
                            when(post){
                                1->{
                                    canvas.drawBitmap(
                                        relatedLeftBlock.scale(blockSize, blockSize, true),
                                        x * blockSize * 1f-blockSize * 0.5f ,
                                        y * blockSize * 1f-blockSize * 0.5f ,
                                        paint)
                                }
                                2->{ canvas.drawBitmap(
                                    collectedBlock.scale(blockSize, blockSize/2, true),
                                    x * blockSize * 1f,
                                    y * blockSize * 1f,
                                    paint)}
                                3->{
                                    canvas.drawBitmap(
                                        relatedRightBlock.scale(blockSize, blockSize, true),
                                        x * blockSize * 1f+blockSize/2f,
                                        y * blockSize * 1f-blockSize/2f,
                                        paint)
                                }
                                4->{
                                    canvas.drawBitmap(
                                        collectedBlock.scale(blockSize/2, blockSize, true),
                                        x * blockSize * 1f+blockSize/2,
                                        y * blockSize * 1f,
                                        paint)

                                }
                                5->{
                                    canvas.drawBitmap(
                                        collectedBlock.scale(blockSize/2, blockSize/2, true),
                                        x * blockSize * 1f+blockSize/2,
                                        y * blockSize * 1f+blockSize/2,
                                        paint)
                                }
                                6->{
                                    canvas.drawBitmap(
                                        collectedBlock.scale(blockSize, blockSize/2, true),
                                        x * blockSize * 1f,
                                        y * blockSize * 1f+blockSize/2,
                                        paint)
                                }
                                7->{
                                    canvas.drawBitmap(
                                        collectedBlock.scale(blockSize/2, blockSize/2, true),
                                        x * blockSize * 1f,
                                        y * blockSize * 1f+blockSize/2,
                                        paint)
                                }
                                8->{
                                    canvas.drawBitmap(
                                        collectedBlock.scale(blockSize/2, blockSize, true),
                                        x * blockSize * 1f,
                                        y * blockSize * 1f,
                                        paint)
                                }
                                else-> {}
                            }

                    }
                }

            }

            findPostOfFrame(reshaping).forEach { pair ->
                val x = pair.first
                val y = pair.second
                val size = getFrameSizeInBitmap(reshaping, blockSize)

                canvas.drawBitmap(
                    frameImg.scale(size, size, true),
                    x * blockSize * 1f,
                    y * blockSize * 1f,
                    paint
                )
            }
            canvas.save()
            return bitmap
        }catch (e:Exception){
            throw e
        }
    }



    // TODO: haven't done yet
    //idea: check which block's color is white then mask it to background
    fun addBackground(bitmap: Bitmap, background: Bitmap, backgroundColor:Color = Color.White):Bitmap{
        try {
            val output = Bitmap.createBitmap(bitmap)
            val scaling = background.scale(bitmap.width,bitmap.height, true)
            for (y in 0..bitmap.height-1){
                for( x in 0..bitmap.width-1){
                    if(bitmap.getPixel(x,y) == android.graphics.Color.TRANSPARENT){
                        val color = mashColor(colorDecode(scaling.getPixel(x,y)), true)
                        output.set(x,y, color = color)

                    }
                }
            }
            return output
        }catch (e:Exception){
            throw e
        }
    }
}
fun BitMatrix.each(action: (Int, Int,Boolean)->Unit){
        for( y in (0.. height-1)){
            for(x in (0..width-1)){
                action(x,y, this.get(x,y))
            }
        }

}
fun BitMatrix.getSizeOfFrame():Int{
    var isRun  = true
    var size = 1
    var (startPointX,startPointY) = Pair(0,0)
    while (isRun){
        if(!this.get(startPointX, startPointY)){
            isRun = false
        }else{
            size+=1
            startPointX+=1
        }
    }
    return size-1
}
fun Float.roundFloor():Int{
    val df = DecimalFormat("#")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this).toInt()

}
fun BitMatrix.getBottomRight(): Pair<Int, Int >{
    return Pair(width-topLeftOnBit[0]-1, height-topLeftOnBit[1]-1)
}
fun BitMatrix.getRowSizeMe(): Int {
    var runPoint = 0
    //it still run get a unset point <false>
    while (get(runPoint,runPoint)){
        runPoint++
    }
    return runPoint
}
fun BitMatrix.checkingRelatedBlock(x: Int,y:Int):List<Int>{
    val listRelatedPost = mutableListOf<Int>()

    if(x>=1&&y>=1){
        if (this.get(x-1,y-1))listRelatedPost.add(1)
    }
    if(x>=1){
        if (this.get(x-1,y))listRelatedPost.add(8)
    }
    if (x<width-1&&y<height-1&&y>=1&&x>=1){
        if (this.get(x+1,y-1))listRelatedPost.add(3)

        if (this.get(x-1,y+1))listRelatedPost.add(7)
    }
    if (x<width-1&&y<height-1){
        if (this.get(x+1,y+1))listRelatedPost.add(5)
    }
    if(x<width-1){
        if (this.get(x+1,y))listRelatedPost.add(4)
    }
    if(y<height-1){
        if (this.get(x,y+1))listRelatedPost.add(6)
    }
    if(y>=1){
        if (this.get(x,y-1))listRelatedPost.add(2)
    }

    return listRelatedPost
}