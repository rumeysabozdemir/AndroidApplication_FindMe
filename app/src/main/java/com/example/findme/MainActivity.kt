package com.example.findme

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.findme.database.DatabaseHelper
import com.example.findme.model.CellInfo
import com.example.findme.model.CellLocationResponse
import com.example.findme.model.CellRequest
import com.example.findme.model.PhoneNumberResponse
import com.example.findme.network.OpenCellIDApi
import com.example.findme.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var textViewLocation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        val editText = findViewById<EditText>(R.id.editTextNumber)
        val checkBox = findViewById<CheckBox>(R.id.checkBoxAccept)
        val button = findViewById<Button>(R.id.buttonSubmit)
        textViewLocation = findViewById(R.id.textViewLocation)

        val apiKey = RetrofitClient.getApiKey(this)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showAgreementDialog()
            }
        }

        button.setOnClickListener {
            val phoneNumber = editText.text.toString().trim()
            val agreementAccepted = checkBox.isChecked

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Lütfen bir telefon numarası girin", Toast.LENGTH_SHORT).show()
            } else if (!isValidPhoneNumber(phoneNumber)) {
                Toast.makeText(this, "Lütfen uygun formatta bir telefon numarası giriniz", Toast.LENGTH_SHORT).show()
            } else if (!agreementAccepted) {
                Toast.makeText(this, "Şartları kabul etmelisiniz", Toast.LENGTH_SHORT).show()
            } else {
                fetchCellLocation(apiKey, phoneNumber)  // Telefon numarasını da gönder
                validatePhoneNumber(phoneNumber)  // Girilen telefon numarasını doğrulama
            }
        }
    }

    // 📌 1. OpenCellID API: Cihazın lokasyon ve IP bilgilerini almak ve veritabanına kaydetmek
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
                        val latitude = it.lat ?: 0.0
                        val longitude = it.lon ?: 0.0
                        saveToDatabase(phoneNumber, "Bilinmiyor", latitude, longitude)
                    }
                } else {
                    Log.e("Cell API Error", "Baz istasyonu bilgisi alınamadı.")
                    Toast.makeText(this@MainActivity, "Baz istasyonu bilgisi alınamadı.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CellLocationResponse>, t: Throwable) {
                Log.e("Cell API Error", "Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Baz istasyonu bilgisi alınamadı.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 📌 2. PhoneNumber API: Girilen telefon numarasının konum bilgilerini ekrana yazdırmak
    private fun validatePhoneNumber(phoneNumber: String) {
        val apiKey = RetrofitClient.getPhoneNumberApiKey(this)
        val api = RetrofitClient.phoneNumberApi

        api.validatePhoneNumber(apiKey, phoneNumber).enqueue(object : Callback<PhoneNumberResponse> {
            override fun onResponse(call: Call<PhoneNumberResponse>, response: Response<PhoneNumberResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val country = it.country_name ?: "Bilinmiyor"
                        val city = it.location ?: "Bilinmiyor"
                        showResult(phoneNumber, country, city)
                    }
                } else {
                    Log.e("API Error", "API başarısız yanıt döndü.")
                    Toast.makeText(this@MainActivity, "API başarısız yanıt döndü", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PhoneNumberResponse>, t: Throwable) {
                Log.e("API Error", "Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Numara doğrulanamadı.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 📌 Sonuçları ekrana yazdıran fonksiyon
    private fun showResult(phoneNumber: String, country: String, city: String) {
        val message = """
            Telefon Numarası: $phoneNumber
            Ülke: $country
            Şehir: $city
        """.trimIndent()

        textViewLocation.text = message
    }

    // 📌 Veritabanına kayıt fonksiyonu
    private fun saveToDatabase(
        phoneNumber: String,
        ipAddress: String,
        latitude: Double,
        longitude: Double
    ) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val id = dbHelper.insertQueryLog(
            timestamp = timestamp,
            ipAddress = ipAddress,
            country = "Bilinmiyor",
            city = "Bilinmiyor",
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

    // 📌 Telefon numarası doğrulama fonksiyonu
    private fun isValidPhoneNumber(number: String): Boolean {
        return number.matches("\\+\\d{12,14}".toRegex())
    }

    // 📌 Şartlar ve Koşullar Dialog'u
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