package com.example.android.estados

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

class DietericiActivity : AppCompatActivity() {

    internal var volum: Double = 0.toDouble()
    internal var par_a: Double = 0.toDouble()
    internal var par_b: Double = 0.toDouble()
    internal var temp: Double = 0.toDouble()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dieterici)

        val calcularBtn = findViewById<View>(R.id.calc_P_btn) as TextView

        calcularBtn.setOnClickListener { view -> calcularPresion(view) }

        val resetButton = findViewById<View>(R.id.reset_btn) as TextView

        resetButton.setOnClickListener { view -> borrarTodo(view) }
    }

    fun calcularPresion(view: View) {
        val volumEditText = findViewById<View>(R.id.volum_edit_text) as EditText
        val parAEditText = findViewById<View>(R.id.par_a_edit_text) as EditText
        val parBEditText = findViewById<View>(R.id.par_b_edit_text) as EditText
        val tempEditText = findViewById<View>(R.id.temp_edit_text) as EditText

        // Revisar si los campos están vacíos
        if (TextUtils.isEmpty(volumEditText.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            volum = java.lang.Double.valueOf(volumEditText.text.toString())
        }

        if (TextUtils.isEmpty(parAEditText.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            par_a = java.lang.Double.valueOf(parAEditText.text.toString())
        }

        if (TextUtils.isEmpty(parBEditText.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            par_b = java.lang.Double.valueOf(parBEditText.text.toString())
        }

        if (TextUtils.isEmpty(tempEditText.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            temp = java.lang.Double.valueOf(tempEditText.text.toString())
        }

        val R = 0.082 // atm*lts/mol*K, por ahora
        val presion = R * temp * (Math.exp(-par_a / (volum * R * temp)) / (volum - par_b))
        val pConUni = "P = " + java.lang.String.format("%.2f", presion) + " atm"

        mostrarResultado(pConUni)
    }

    private fun mostrarResultado(message: String) {
        val resultadoTextView = findViewById<View>(R.id.resultado_text_view) as TextView
        resultadoTextView.text = message
    }

    fun borrarTodo(view: View) {
        val volumEditText = findViewById<View>(R.id.volum_edit_text) as EditText
        val parAEditText = findViewById<View>(R.id.par_a_edit_text) as EditText
        val parBEditText = findViewById<View>(R.id.par_b_edit_text) as EditText
        val tempEditText = findViewById<View>(R.id.temp_edit_text) as EditText

        volumEditText.text = null
        parAEditText.text = null
        parBEditText.text = null
        tempEditText.text = null
        mostrarResultado("0")
    }
}
