package com.example.findme

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.findme.network.LocationApi
import com.example.findme.model.LocationResponse
import com.example.findme.database.DatabaseHelper
import com.example.findme.model.CellInfo
import com.example.findme.model.CellLocationResponse
import com.example.findme.model.CellRequest
import com.example.findme.network.OpenCellIDApi
import com.example.findme.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private var ipAddress: String = "Bilinmiyor"
    private var location: String = "Bilinmiyor"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        val editText = findViewById<EditText>(R.id.editTextNumber)
        val checkBox = findViewById<CheckBox>(R.id.checkBoxAccept)
        val button = findViewById<Button>(R.id.buttonSubmit)
        val apiKey = RetrofitClient.getApiKey(this)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showAgreementDialog()
            }
        }

        button.setOnClickListener {
            val phoneNumber = editText.text.toString()
            val agreementAccepted = checkBox.isChecked

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Lütfen bir telefon numarası girin", Toast.LENGTH_SHORT).show()
            } else if (!isValidPhoneNumber(phoneNumber)) {
                Toast.makeText(this, "Lütfen uygun formatta bir telefon numarası giriniz", Toast.LENGTH_SHORT).show()
            } else if (!agreementAccepted) {
                Toast.makeText(this, "Şartları kabul etmelisiniz", Toast.LENGTH_SHORT).show()
            } else {
                fetchCellLocation(apiKey, phoneNumber)
                fetchLocationData(phoneNumber)
            }
        }
    }

    // Sadece bir tane fetchCellLocation() fonksiyonu olsun
    private fun fetchCellLocation(apiKey: String, phoneNumber: String) {
        val api = RetrofitClient.cellIdInstance.create(OpenCellIDApi::class.java)
        val cellRequest = CellRequest(
            token = apiKey,
            radio = "gsm",
            mcc = 286,
            mnc = 1,
            cells = listOf(CellInfo(lac = 7033, cid = 17811))
        )

        api.getCellLocation(cellRequest).enqueue(object : Callback<CellLocationResponse> {
            override fun onResponse(call: Call<CellLocationResponse>, response: Response<CellLocationResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val country = "SabitDeger"  // Sabit değer
                        val city = "SabitDeger"      // Sabit değer
                        val latitude = it.lat ?: 0.0
                        val longitude = it.lon ?: 0.0

                        saveToDatabase(phoneNumber, ipAddress, country, city, latitude, longitude)
                    } ?: Log.e("CellLocationResponse", "Response is null")
                } else {
                    Log.e("Cell API Error", "Error body: ${response.errorBody()?.string()}")
                    Toast.makeText(this@MainActivity, "Baz istasyonu bilgisi alınamadı.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CellLocationResponse>, t: Throwable) {
                Log.e("Cell API Error", "Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Baz istasyonu bilgisi alınamadı.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // IP ve Lokasyon bilgilerini almak için API çağrısı
    private fun fetchLocationData(phoneNumber: String) {
        val api = RetrofitClient.ipInstance.create(LocationApi::class.java)
        api.getLocation().enqueue(object : Callback<LocationResponse> {
            override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val ipAddress = it.query ?: "Bilinmiyor"
                        val country = it.country ?: "Bilinmiyor"
                        val city = it.city ?: "Bilinmiyor"
                        val latitude = it.lat ?: 0.0
                        val longitude = it.lon ?: 0.0

                        saveToDatabase(phoneNumber, ipAddress, country, city, latitude, longitude)
                    } ?: Toast.makeText(this@MainActivity, "Yanıt boş döndü.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "API başarısız yanıt döndü: ${response.errorBody()?.string()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                Log.e("IP API Error", "Error: ${t.message}")
                Toast.makeText(this@MainActivity, "IP bilgisi alınamadı.", Toast.LENGTH_SHORT).show()
            }
        })
    }

private fun saveToDatabase(
    phoneNumber: String,
    ipAddress: String,
    country: String,
    city: String,
    latitude: Double,
    longitude: Double
) {
    val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

    val id = dbHelper.insertQueryLog(
        timestamp = timestamp,
        ipAddress = ipAddress,
        country = country,
        city = city,
        latitude = latitude,
        longitude = longitude,
        phoneNumber = phoneNumber,
        agreementAccepted = true
    )

    if (id != -1L) {
        Toast.makeText(this, "Veri başarıyla kaydedildi.", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(this, "Veritabanı hatası.", Toast.LENGTH_SHORT).show()
    }
}

    private fun isValidPhoneNumber(number: String): Boolean {
        if (!number.matches("\\d{10,11}".toRegex())) return false

        var count = 1
        for (i in 1 until number.length) {
            if (number[i] == number[i - 1]) {
                count++
                if (count > 5) return false
            } else {
                count = 1
            }
        }
        return true
    }

    private fun showAgreementDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Şartlar ve Koşullar")
            .setMessage("Anlaşma")
            .setPositiveButton("Kabul Ediyorum") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}