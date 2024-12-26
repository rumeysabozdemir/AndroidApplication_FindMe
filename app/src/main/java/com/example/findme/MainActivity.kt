package com.example.findme

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.findme.database.DatabaseHelper
import com.example.findme.model.LocationResponse
import com.example.findme.network.RetrofitClient
import com.example.findme.network.LocationApi
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

        // Konum ve IP bilgisi için API çağrısı
        fetchLocationData()

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
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                saveToDatabase(timestamp, phoneNumber, agreementAccepted)
            }
        }
    }

    private fun saveToDatabase(timestamp: String, phoneNumber: String, agreementAccepted: Boolean) {
        val id = dbHelper.insertQueryLog(
            timestamp = timestamp,
            ipAddress = ipAddress,
            location = location,
            phoneNumber = phoneNumber,
            agreementAccepted = agreementAccepted
        )

        if (id != -1L) {
            Toast.makeText(this, "Sorgu başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Veritabanı hatası", Toast.LENGTH_SHORT).show()
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

    private fun fetchLocationData() {
        val api = RetrofitClient.instance.create(LocationApi::class.java)
        api.getLocation().enqueue(object : Callback<LocationResponse> {
            override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        ipAddress = it.query
                        location = "${it.city}, ${it.country}"
                        Toast.makeText(this@MainActivity, "Konum Bilgisi Alındı", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "API başarısız yanıt döndü", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Konum bilgisi alınamadı: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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