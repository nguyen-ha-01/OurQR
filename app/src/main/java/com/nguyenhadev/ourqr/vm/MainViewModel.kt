package com.nguyenhadev.ourqr.vm

import android.content.Context
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.nguyenhadev.ourqr.Folder
import com.nguyenhadev.ourqr.db.QRScanned
import com.nguyenhadev.ourqr.db.QrDb
import com.nguyenhadev.ourqr.qr.QRDataType
import com.nguyenhadev.ourqr.qr.QrWriter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
const val SCAN_ERR = "SCAN ERROR"
class MainViewModel():ViewModel() {
    var launcher: ManagedActivityResultLauncher<String, Boolean>? = null

    var cameraPermission by mutableStateOf(false)
    var storePermission by mutableStateOf(false)
    var createType: QRDataType by mutableStateOf(QRDataType.Link)
    var listQrScanned: MutableList<QRScanned> by mutableStateOf(mutableListOf())
    var listFolder = mutableListOf<Folder>(
        Folder("folder a"), Folder( "folder b"),
        Folder(  "folder c")
    )
    var snackBarData by mutableStateOf("")
    var scanData by mutableStateOf("")
    var snackBarHostState by mutableStateOf(false)
    var scannedQrListState by mutableStateOf(true)
    var qr by mutableStateOf("")
    private var pagingData :Pager<Int, QRScanned>? = null

    private var db: QrDb? = null
    private val qrWriter : QrWriter  = QrWriter()

    fun getPaging(ctx:Context):Pager<Int, QRScanned>{
        if(pagingData == null) {
            pagingData = Pager(PagingConfig(2)){
                getDb(ctx).getDao().pagingGets()
            }
        }
        return pagingData as Pager<Int, QRScanned>
    }
    fun getDb(ctx: Context) :QrDb =if(db != null) {
         db as QrDb
    } else {
        db = QrDb.getInstance(ctx)
        db as QrDb
    }

    fun saveScannerQR(context : Context, content: String){
        var currentTime = getCurrentTime()

        val dao  = getDb(context).getDao()
        viewModelScope.launch {
            try{
                dao.add(QRScanned(content = content,  date = currentTime, id= null))
            }catch (e: Exception){
                Log.e(SCAN_ERR, e.message.toString())
            }
        }
    }
    fun getCurrentTime(format: String = "yyyy-MM-dd HH:mm"):String{
        val calendar : Calendar = Calendar.getInstance()
        var time  = calendar.time
        val formater = SimpleDateFormat(format)
        return formater.format(time)
    }

    fun getUpdateScannedQrList(context:Context) {
         viewModelScope.launch {
             try {
                 listQrScanned.clear()
                 getDb(context).getDao().gets().collectLatest {
                     listQrScanned.addAll(it)
                 }
             }catch (e: Exception){

             }
         }
    }
}