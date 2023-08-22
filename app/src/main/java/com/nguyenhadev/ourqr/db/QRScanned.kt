package com.nguyenhadev.ourqr.db

import androidx.room.Entity
import androidx.room.PrimaryKey
const val QR_SCANNED_TABLE = "QR_SCANNED_TABLE"
@Entity(tableName = QR_SCANNED_TABLE)
data class QRScanned(@PrimaryKey(autoGenerate = true) val id:Int?,val content: String,  val date: String  )
