package com.nguyenhadev.ourqr

import kotlinx.serialization.Serializable

@Serializable
data class QRTable(val id:Int, val folder: Folder?, val uri:String,  val description:String) {
}