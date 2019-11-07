package com.example.android.estados

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat

import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

import java.util.ArrayList

class PlotVanDerWaalsActivity : AppCompatActivity() {

    private var gestureDetectorCompat: GestureDetectorCompat? = null
    private var graph: GraphView? = null

    private var ViEditText: EditText? = null
    private var VfEditText: EditText? = null
    private var PiEditText: EditText? = null
    private var PfEditText: EditText? = null
    private var TEditText: EditText? = null
    private var aEditText: EditText? = null
    private var bEditText: EditText? = null
    private var PartesEditText: EditText? = null

    internal var parts: Int = 0 // No. de partes de la gráfica
    internal var i: Int = 0
    internal var j: Int = 0 // Contadores
    internal val iter = 100 //Máximo de iteraciones para el newton-raphson
    internal val epsilon = 0.001 //Precisión de newton-raphson
    internal var a: Double = 0.toDouble()
    internal var b: Double = 0.toDouble()
    internal var T: Double = 0.toDouble()
    internal var P_i: Double = 0.toDouble()
    internal var P: Double = 0.toDouble()
    internal var P_f: Double = 0.toDouble()
    internal var delta: Double = 0.toDouble()
    internal var V: Double = 0.toDouble()
    internal var guess: Double = 0.toDouble()
    internal val cte_R = 0.082

    internal var presion_data = ArrayList<Double>() // Valores de la presión
    internal var volum_data = ArrayList<Double>() // Valores del volumen
    internal var series = LineGraphSeries(data())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plot_van_der_waals)

        // onClickListeners para los botones
        //        TextView plotBtn = (TextView) findViewById(R.id.plot_btn);
        //
        //        plotBtn.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                graficar(view);
        //            }
        //        });

        val PlotButton = findViewById<View>(R.id.plot_btn) as TextView

        PlotButton.setOnClickListener { view -> revisarCampos(view) }

        val ResetButton = findViewById<View>(R.id.reset_btn) as TextView

        ResetButton.setOnClickListener { view -> borrarTodo(view) }

        gestureDetectorCompat = GestureDetectorCompat(this, My2ndGestureListener())
        graph = findViewById<View>(R.id.graph) as GraphView

        ViEditText = findViewById<View>(R.id.Vi_edit_text) as EditText
        VfEditText = findViewById<View>(R.id.Vf_edit_text) as EditText
        PiEditText = findViewById<View>(R.id.Pi_edit_text) as EditText
        PfEditText = findViewById<View>(R.id.Pf_edit_text) as EditText
        TEditText = findViewById<View>(R.id.T_edit_text) as EditText
        aEditText = findViewById<View>(R.id.a_edit_text) as EditText
        bEditText = findViewById<View>(R.id.b_edit_text) as EditText
        PartesEditText = findViewById<View>(R.id.parts_edit_text) as EditText
    }

    // El siguiente código horroroso es para cambiar de pantalla
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        super.dispatchTouchEvent(ev)
        return gestureDetectorCompat!!.onTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.gestureDetectorCompat!!.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    internal inner class My2ndGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onFling(event1: MotionEvent, event2: MotionEvent,
                             velocityX: Float, velocityY: Float): Boolean {

            if (event2.x > event1.x) {
                val intent = Intent(
                        this@PlotVanDerWaalsActivity, VanDerWaalsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in)
            }

            return true
        }
    }

    private fun graficar(view: View) {
        series.resetData(data())
        graph!!.addSeries(series)
    }

    fun data(): Array<DataPoint?> {

        val values = arrayOfNulls<DataPoint>(parts)
        i = 0
        while (i < parts) {
            val v = DataPoint(presion_data[i], volum_data[i])
            values[i] = v
            i++
        }
        return values
    }

    private fun revisarCampos(view: View) {

        // Revisar qué los campos están vacíos
        if (TextUtils.isEmpty(PartesEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            parts = Integer.valueOf(PartesEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(aEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            a = java.lang.Double.valueOf(aEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(bEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            b = java.lang.Double.valueOf(bEditText!!.text.toString())
        }
        if (TextUtils.isEmpty(TEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            T = java.lang.Double.valueOf(TEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(PiEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            P_i = java.lang.Double.valueOf(PiEditText!!.text.toString())
        }

        if (TextUtils.isEmpty(PfEditText!!.text.toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show()
            return
        } else {
            P_f = java.lang.Double.valueOf(PfEditText!!.text.toString())
        }

        delta = (P_f - P_i) / parts
        llenarPresion(view)
        calcularVolumen(view)
        graficar(view)
    }

    private fun llenarPresion(view: View) {

        presion_data.clear()
        P = P_i
        presion_data.add(P)

        i = 0
        while (i < parts) {
            P += delta
            presion_data.add(P)
            i++
        }
    }

    private fun calcularVolumen(view: View) {

        volum_data.clear()
        P = P_i

        i = 0
        while (i < parts) {
            guess = cte_R * T / P

            //Aquí empieza el Newton-Raphson
            j = 0
            while (j < iter) {
                V = metodoVolumen(guess, P)
                if (Math.abs(V - guess) < epsilon) break
                guess = V
                j++
            }
            volum_data.add(V)
            P += delta
            i++
        }
    }

    private fun metodoVolumen(x: Double, miP: Double): Double {
        val f = (miP + a / (x * x)) * (x - b) - cte_R * T
        val df = miP + a / (x * x) + (x - b) * (-2.0 * a / Math.pow(x, 3.0))

        return x - f / df
    }

    private fun borrarTodo(view: View) {

        ViEditText!!.text = null
        VfEditText!!.text = null
        PiEditText!!.text = null
        PfEditText!!.text = null
        TEditText!!.text = null
        aEditText!!.text = null
        bEditText!!.text = null
        PartesEditText!!.text = null
    }

}
