package com.example.android.estados

//TODO: Hacer que no se borren los datos al cambiar de pantalla

import android.app.Activity
import android.content.Intent
import android.view.GestureDetector
import android.view.MotionEvent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import androidx.core.view.GestureDetectorCompat

class VanDerWaalsActivity : Activity() {

    internal val R_1 = 0.082 // atm*lts/mol*K
    internal var volum: Double = 0.toDouble()
    internal var presion: Double = 0.toDouble()
    internal var moles: Double = 0.toDouble()
    internal var temp: Double = 0.toDouble()
    internal var cte_R: Double = 0.toDouble()
    internal var par_a: Double = 0.toDouble()
    internal var par_b: Double = 0.toDouble()
    internal var R_p: Double = 0.toDouble()
    internal var R_v: Double = 0.toDouble()
    internal var i: Int = 0 //Contador
    internal var iter = 100 //Máximo de iteraciones para el newton-raphson
    internal var epsilon = 0.001 //Precisión de newton-raphson
    internal var guess: Double = 0.toDouble() //Pruebas del newton-raphson
    internal lateinit var uni_P: String
    internal lateinit var uni_V: String
    internal lateinit var uni_T: String
    private var volumEditText: EditText? = null
    private var presionEditText: EditText? = null
    private var molesEditText: EditText? = null
    private var tempEditText: EditText? = null
    private var parAEditText: EditText? = null
    private var parBEditText: EditText? = null

    private var gestureDetectorCompat: GestureDetectorCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vanderwaals)

        // OnClickListeners para botones de calcular
        val presionBtn = findViewById<View>(R.id.calc_P_btn) as TextView

        presionBtn.setOnClickListener { view -> calcularPresion(view) }

        val volumBtn = findViewById<View>(R.id.calc_V_btn) as TextView

        volumBtn.setOnClickListener { view -> calcularVolumen(view) }

        val molesBtn = findViewById<View>(R.id.calc_n_btn) as TextView

        molesBtn.setOnClickListener { view -> calcularMoles(view) }

        val tempBtn = findViewById<View>(R.id.calc_T_btn) as TextView

        tempBtn.setOnClickListener { view -> calcularTemp(view) }

        val factBtn = findViewById<View>(R.id.calc_Z_btn) as TextView

        factBtn.setOnClickListener { view -> calcularFactor(view) }

        val ResetButton = findViewById<View>(R.id.reset_btn) as TextView

        ResetButton.setOnClickListener { view -> borrarTodo(view) }

        // Programación de los spinners
        val presionSpinner = findViewById<View>(R.id.presion_spinner) as Spinner
        val presionAdapter = ArrayAdapter.createFromResource(this,
                R.array.presion_array, android.R.layout.simple_spinner_item)
        presionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        presionSpinner.adapter = presionAdapter

        presionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View,
                                        pos: Int, id: Long) {
                elegirRP(pos)
                elegirUniP(pos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val volumSpinner = findViewById<View>(R.id.volum_spinner) as Spinner
        val volumAdapter = ArrayAdapter.createFromResource(this,
                R.array.volum_array, android.R.layout.simple_spinner_item)
        volumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        volumSpinner.adapter = volumAdapter

        volumSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View,
                                        pos: Int, id: Long) {
                elegirRV(pos)
                elegirUniV(pos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val tempSpinner = findViewById<View>(R.id.temp_spinner) as Spinner
        val tempAdapter = ArrayAdapter.createFromResource(this,
                R.array.temp_array, android.R.layout.simple_spinner_item)
        tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tempSpinner.adapter = tempAdapter

        tempSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View,
                                        pos: Int, id: Long) {
                elegirUniT(pos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        volumEditText = findViewById<View>(R.id.volum_edit_text) as EditText
        presionEditText = findViewById<View>(R.id.presion_edit_text) as EditText
        molesEditText = findViewById<View>(R.id.moles_edit_text) as EditText
        tempEditText = findViewById<View>(R.id.temp_edit_text) as EditText
        parAEditText = findViewById<View>(R.id.par_a_edit_text) as EditText
        parBEditText = findViewById<View>(R.id.par_b_edit_text) as EditText

        gestureDetectorCompat = GestureDetectorCompat(this, MyGestureListener())
    }

    // El siguiente código detecta si la pantalla se desliza a la derecha, para pasar a la
    // actividad de graficar
    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.gestureDetectorCompat!!.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        super.dispatchTouchEvent(ev)
        return gestureDetectorCompat!!.onTouchEvent(ev)
    }

    internal inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onFling(event1: MotionEvent, event2: MotionEvent,
                             velocityX: Float, velocityY: Float): Boolean {

            if (event2.x < event1.x) {
                val intent = Intent(
                        this@VanDerWaalsActivity, PlotVanDerWaalsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out)
            }
            return true
        }
    }

    fun elegirRP(pos: Int) { // Cambio en la constante R por unidades de presión
        when (pos) {
            0 -> R_p = 1.0 // atm
            1 -> R_p = 101325.0 // Pa
            2 -> R_p = 1.01325 // bar
            3 -> R_p = 760.002 // mmHg
            4 -> R_p = 14.6959 // PSI
        }
    }

    fun elegirUniP(pos: Int) { // Cambio en la presentación del resultado por unidades de presión
        when (pos) {
            0 -> uni_P = "atm"
            1 -> uni_P = "Pa"
            2 -> uni_P = "bar"
            3 -> uni_P = "mmHg"
            4 -> uni_P = "PSI"
        }
    }

    fun elegirRV(pos: Int) { // Cambio en la constante R por unidades de volumen
        when (pos) {
            0 -> R_v = 1.0 // lts
            1 -> R_v = 0.001 // m3
        }
    }

    fun elegirUniV(pos: Int) { // Cambio en la presentación del resultado por unidades de volumen
        when (pos) {
            0 -> uni_V = "lts"
            1 -> uni_V = "m3"
        }
    }

    fun convertirTempAK(pos: Int, T: Double): Double { // Cambiar la temp dada a temp absoluta
        var T = T
        when (pos) {
            0 // Temperatura dada en K
            -> {
            }
            1 // Temperatura dada en °C
            -> T += 273.15
            2 // Temperatura dada en °F
            -> T = 5 * (T - 32) / 9 + 273.15
            3 // Temperatura dada en R
            -> T *= (5 / 9).toDouble()
        }

        return T
    }

    fun convertirTempDeK(pos: Int, T: Double): Double { // Cambiar la temp absoluta a la temp solicitada
        var T = T
        when (pos) {
            0 // Convertir a K
            -> {
            }
            1 // Convertir a °C
            -> T -= 273.15
            2 // Convertir a °F
            -> T = 9 * (T - 273.15) / 5 + 32.0
            3 // Convertir a R
            -> T *= (9 / 5).toDouble()
        }

        return T
    }

    fun elegirUniT(pos: Int) { // Cambio en la presentación del resultado por unidades de temperatura
        when (pos) {
            0 -> uni_T = "K"
            1 -> uni_T = "°C"
            2 -> uni_T = "°F"
            3 -> uni_T = "R"
        }
    }

    private fun calcularPresion(view: View) {

        // Revisar si los campos están vacíos
        if (TextUtils.isEmpty(volumEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            volum = java.lang.Double.valueOf(volumEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(molesEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            moles = java.lang.Double.valueOf(molesEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(tempEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            temp = java.lang.Double.valueOf(tempEditText!!.text.toString())

            val tempSpinner = findViewById<View>(R.id.temp_spinner) as Spinner

            val pos = tempSpinner.selectedItemPosition

            temp = convertirTempAK(pos, temp)
        }
        if (TextUtils.isEmpty(parAEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            par_a = java.lang.Double.valueOf(parAEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(parBEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            par_b = java.lang.Double.valueOf(parBEditText!!.text.toString())
        }

        cte_R = R_1 * R_p * R_v
        presion = moles * cte_R * temp / (volum - moles * par_b) - moles * moles * par_a / (volum * volum)
        val result = "P = " + String.format("%.4f", presion) + " " + uni_P

        mostrarResultado(result)
    }

    private fun calcularVolumen(view: View) {

        // Revisar si los campos están vacíos
        if (TextUtils.isEmpty(presionEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            presion = java.lang.Double.valueOf(presionEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(molesEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            moles = java.lang.Double.valueOf(molesEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(tempEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            temp = java.lang.Double.valueOf(tempEditText!!.text.toString())

            val tempSpinner = findViewById<View>(R.id.temp_spinner) as Spinner

            val pos = tempSpinner.selectedItemPosition

            temp = convertirTempAK(pos, temp)
        }

        cte_R = R_1 * R_p * R_v
        guess = moles * cte_R * temp / presion

        //Aquí empieza el Newton-Raphson
        i = 0
        while (i < iter) {
            volum = metodoVolumen(guess, cte_R, presion)
            if (Math.abs(volum - guess) < epsilon) break
            guess = volum
            i++
        }

        val result = "V = " + String.format("%.4f", volum) + " " + uni_V

        mostrarResultado(result)
    }

    private fun calcularMoles(view: View) {

        // Revisar si los campos están vacíos
        if (TextUtils.isEmpty(presionEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            presion = java.lang.Double.valueOf(presionEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(volumEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            volum = java.lang.Double.valueOf(volumEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(tempEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            temp = java.lang.Double.valueOf(tempEditText!!.text.toString())

            val tempSpinner = findViewById<View>(R.id.temp_spinner) as Spinner

            val pos = tempSpinner.selectedItemPosition

            temp = convertirTempAK(pos, temp)
        }

        cte_R = R_1 * R_p * R_v
        moles = presion * volum / (cte_R * temp)
        val result = "n = " + String.format("%.4f", moles) + " moles"

        mostrarResultado(result)
    }

    private fun calcularTemp(view: View) {

        // Revisar si los campos están vacíos
        if (TextUtils.isEmpty(presionEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            presion = java.lang.Double.valueOf(presionEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(volumEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            volum = java.lang.Double.valueOf(volumEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(molesEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            moles = java.lang.Double.valueOf(molesEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(parAEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            par_a = java.lang.Double.valueOf(parAEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(parBEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            par_b = java.lang.Double.valueOf(parBEditText!!.text.toString())
        }

        cte_R = R_1 * R_v * R_p
        temp = (presion + moles * moles * par_a / (volum * volum)) * (volum - moles * par_b) / (moles * cte_R)

        val tempSpinner = findViewById<View>(R.id.temp_spinner) as Spinner
        val pos = tempSpinner.selectedItemPosition
        temp = convertirTempDeK(pos, temp)

        val result = "T = " + String.format("%.4f", temp) + " " + uni_T

        mostrarResultado(result)
    }

    private fun calcularFactor(view: View) {

        // Revisar si los campos están vacíos
        if (TextUtils.isEmpty(tempEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            temp = java.lang.Double.valueOf(tempEditText!!.text.toString())

            val tempSpinner = findViewById<View>(R.id.temp_spinner) as Spinner

            val pos = tempSpinner.selectedItemPosition

            temp = convertirTempAK(pos, temp)
        }

        if (TextUtils.isEmpty(presionEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            presion = java.lang.Double.valueOf(presionEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(volumEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            volum = java.lang.Double.valueOf(volumEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(molesEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            moles = java.lang.Double.valueOf(molesEditText!!.text.toString())
        }

        cte_R = R_1 * R_p * R_v
        val factor = presion * volum / (cte_R * moles * temp)
        val result = "Z = " + String.format("%.4f", factor)

        mostrarResultado(result)
    }

    private fun mostrarResultado(message: String) {
        val resultadoTextView = findViewById<View>(R.id.resultado_text_view) as TextView
        resultadoTextView.text = message
    }

    private fun borrarTodo(view: View) {

        volumEditText!!.text = null
        presionEditText!!.text = null
        molesEditText!!.text = null
        tempEditText!!.text = null
        parAEditText!!.text = null
        parBEditText!!.text = null
        mostrarResultado("0")
    }

    private fun metodoVolumen(x: Double, miR: Double, miP: Double): Double {
        val f = (miP + moles * moles * par_a / (x * x)) * (x - moles * par_b) - moles * miR * temp
        val df = miP + moles * moles * par_a / (x * x) + (x - moles * par_b) * (-2.0 * moles * moles * par_a / Math.pow(x, 3.0))

        return x - f / df
    }

}
