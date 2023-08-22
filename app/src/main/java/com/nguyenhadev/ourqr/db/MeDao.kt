package com.nguyenhadev.ourqr.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(qrContent: QRScanned)
    //no paging support
    @Query("SELECT * FROM QR_SCANNED_TABLE ORDER BY id DESC LIMIT 10")
    fun gets(): Flow<List<QRScanned>>

    //paging support
    @Query("SELECT * FROM QR_SCANNED_TABLE  ORDER BY id DESC ")
    fun pagingGets():PagingSource<Int, QRScanned>

    @Delete
    suspend fun delete(content: QRScanned)
}