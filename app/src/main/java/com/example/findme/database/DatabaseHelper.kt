package com.example.findme.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "query_log.db"
        private const val DATABASE_VERSION = 2

        // Tablo ve sütun adları
        const val TABLE_NAME = "QueryLog"
        const val COLUMN_ID = "id"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_IP_ADDRESS = "ip_address"
        const val COLUMN_COUNTRY = "country"
        const val COLUMN_CITY = "city"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_PHONE_NUMBER = "phone_number"
        const val COLUMN_AGREEMENT_ACCEPTED = "agreement_accepted"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TIMESTAMP TEXT,
                $COLUMN_IP_ADDRESS TEXT,
                $COLUMN_COUNTRY TEXT,
                $COLUMN_CITY TEXT,
                $COLUMN_LATITUDE REAL,
                $COLUMN_LONGITUDE REAL,
                $COLUMN_PHONE_NUMBER TEXT,
                $COLUMN_AGREEMENT_ACCEPTED INTEGER
            )
        """.trimIndent()

        db.execSQL(createTable)
        Log.d("DatabaseHelper", "Tablo başarıyla oluşturuldu: $TABLE_NAME")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_COUNTRY TEXT")
                db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_CITY TEXT")
                db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_LATITUDE REAL")
                db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_LONGITUDE REAL")
                Log.d("DatabaseHelper", "Tablo başarıyla güncellendi: $TABLE_NAME")
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Tablo güncellenirken hata oluştu: ${e.message}")
            }
        }
    }

    fun insertQueryLog(
        timestamp: String,
        ipAddress: String,
        country: String,
        city: String,
        latitude: Double?,
        longitude: Double?,
        phoneNumber: String,
        agreementAccepted: Boolean
    ): Long {
        return try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(COLUMN_TIMESTAMP, timestamp)
                put(COLUMN_IP_ADDRESS, ipAddress)
                put(COLUMN_COUNTRY, country)
                put(COLUMN_CITY, city)
                put(COLUMN_LATITUDE, latitude ?: 0.0)  // Null kontrolü
                put(COLUMN_LONGITUDE, longitude ?: 0.0) // Null kontrolü
                put(COLUMN_PHONE_NUMBER, phoneNumber)
                put(COLUMN_AGREEMENT_ACCEPTED, if (agreementAccepted) 1 else 0)
            }

            val result = db.insert(TABLE_NAME, null, values)
            if (result != -1L) {
                Log.d("DatabaseHelper", "Veri başarıyla kaydedildi. ID: $result")
            } else {
                Log.e("DatabaseHelper", "Veritabanına veri kaydedilemedi.")
            }
            result
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Veritabanına veri eklenirken hata oluştu: ${e.message}")
            -1L
        }
    }
}