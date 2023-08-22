package com.nguyenhadev.ourqr.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(QRScanned::class), version = 1)
abstract class QrDb :RoomDatabase(){
    abstract fun getDao(): MeDao

    companion object{
        @Volatile
        private var DB :QrDb? = null
        const val DB_Name = "QR_DB"
        fun getInstance(ctx: Context) :QrDb{if (DB == null) synchronized(QrDb::class){
            DB = Room.databaseBuilder(ctx.applicationContext,  QrDb::class.java, DB_Name).build()
            return DB as QrDb
        }else
            return DB as QrDb
        }

    }
}