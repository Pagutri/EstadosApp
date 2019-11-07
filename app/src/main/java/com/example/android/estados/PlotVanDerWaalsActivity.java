package com.example.android.estados;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class PlotVanDerWaalsActivity extends AppCompatActivity {

    private GestureDetectorCompat gestureDetectorCompat;
    private GraphView graph;

    private EditText ViEditText;
    private EditText VfEditText;
    private EditText PiEditText;
    private EditText PfEditText;
    private EditText TEditText;
    private EditText aEditText;
    private EditText bEditText;
    private EditText PartesEditText;

    int parts; // No. de partes de la gráfica
    int i, j; // Contadores
    final int iter = 100; //Máximo de iteraciones para el newton-raphson
    final double epsilon = 0.001; //Precisión de newton-raphson
    double a, b, T, P_i, P, P_f, delta, V, guess;
    final double cte_R = 0.082;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_van_der_waals);

        // onClickListeners para los botones
//        TextView plotBtn = (TextView) findViewById(R.id.plot_btn);
//
//        plotBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                graficar(view);
//            }
//        });

        TextView PlotButton = (TextView) findViewById(R.id.plot_btn);

        PlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revisarCampos(view);
            }
        });

        TextView ResetButton = (TextView) findViewById(R.id.reset_btn);

        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrarTodo(view);
            }
        });

        gestureDetectorCompat = new GestureDetectorCompat(this, new My2ndGestureListener());
        graph = (GraphView) findViewById(R.id.graph);

        ViEditText = (EditText) findViewById(R.id.Vi_edit_text);
        VfEditText = (EditText) findViewById(R.id.Vf_edit_text);
        PiEditText = (EditText) findViewById(R.id.Pi_edit_text);
        PfEditText = (EditText) findViewById(R.id.Pf_edit_text);
        TEditText = (EditText) findViewById(R.id.T_edit_text);
        aEditText = (EditText) findViewById(R.id.a_edit_text);
        bEditText = (EditText) findViewById(R.id.b_edit_text);
        PartesEditText = (EditText) findViewById(R.id.parts_edit_text);
    }

    // El siguiente código horroroso es para cambiar de pantalla
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        super.dispatchTouchEvent(ev);
        return gestureDetectorCompat.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class My2ndGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if(event2.getX() > event1.getX()){
                Intent intent = new Intent(
                        PlotVanDerWaalsActivity.this, VanDerWaalsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in);
            }

            return true;
        }
    }

    ArrayList<Double> presion_data = new ArrayList<>(); // Valores de la presión
    ArrayList<Double> volum_data = new ArrayList<>(); // Valores del volumen
    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(data());

    private void graficar(View view) {
        series.resetData(data());
        graph.addSeries(series);
    }

    public DataPoint[] data(){

        DataPoint[] values = new DataPoint[parts];
        for(i = 0; i < parts; i++){
            DataPoint v = new DataPoint(presion_data.get(i),volum_data.get(i));
            values[i] = v;
        }
        return values;
    }

    private void revisarCampos(View view) {

        // Revisar qué los campos están vacíos
        if (TextUtils.isEmpty(PartesEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            parts = Integer.valueOf(PartesEditText.getText().toString());
        }

        if (TextUtils.isEmpty(aEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            a = Double.valueOf(aEditText.getText().toString());
        }

        if (TextUtils.isEmpty(bEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            b = Double.valueOf(bEditText.getText().toString());
        }
        if (TextUtils.isEmpty(TEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            T = Double.valueOf(TEditText.getText().toString());
        }

        if (TextUtils.isEmpty(PiEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            P_i = Double.valueOf(PiEditText.getText().toString());
        }

        if (TextUtils.isEmpty(PfEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            P_f = Double.valueOf(PfEditText.getText().toString());
        }

        delta = (P_f - P_i)/parts;
        llenarPresion(view);
        calcularVolumen(view);
        graficar(view);
    }

    private void llenarPresion(View view) {

        presion_data.clear();
        P = P_i;
        presion_data.add(P);

        for(i = 0; i < parts; i++) {
            P += delta;
            presion_data.add(P);
        }
    }

    private void calcularVolumen(View view) {

        volum_data.clear();
        P = P_i;

        for(i = 0; i < parts; i++) {
            guess = cte_R * T / P;

            //Aquí empieza el Newton-Raphson
            for (j = 0; j < iter; j++) {
                V = metodoVolumen(guess, P);
                if (Math.abs(V - guess) < epsilon) break;
                guess = V;
            }
            volum_data.add(V);
            P += delta;
        }
    }

    private double metodoVolumen(double x, double miP) {
        double f = (miP + (a / (x * x))) * (x - b) - (cte_R * T);
        double df = (miP + (a / (x * x))) + ((x - b) * (-2.0 * a / Math.pow(x, 3)));

        return x - (f / df);
    }

    private void borrarTodo(View view) {

        ViEditText.setText(null);
        VfEditText.setText(null);
        PiEditText.setText(null);
        PfEditText.setText(null);
        TEditText.setText(null);
        aEditText.setText(null);
        bEditText.setText(null);
        PartesEditText.setText(null);
    }

}
