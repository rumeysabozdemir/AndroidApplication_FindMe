package com.example.findme.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "query_log.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "QueryLog"
        const val COLUMN_ID = "id"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_IP_ADDRESS = "ip_address"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_PHONE_NUMBER = "phone_number"
        const val COLUMN_AGREEMENT_ACCEPTED = "agreement_accepted"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TIMESTAMP TEXT,
                $COLUMN_IP_ADDRESS TEXT,
                $COLUMN_LOCATION TEXT,
                $COLUMN_PHONE_NUMBER TEXT,
                $COLUMN_AGREEMENT_ACCEPTED INTEGER
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertQueryLog(timestamp: String, ipAddress: String, location: String, phoneNumber: String, agreementAccepted: Boolean): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TIMESTAMP, timestamp)
            put(COLUMN_IP_ADDRESS, ipAddress)
            put(COLUMN_LOCATION, location)
            put(COLUMN_PHONE_NUMBER, phoneNumber)
            put(COLUMN_AGREEMENT_ACCEPTED, if (agreementAccepted) 1 else 0)
        }
        return db.insert(TABLE_NAME, null, values)
    }
}