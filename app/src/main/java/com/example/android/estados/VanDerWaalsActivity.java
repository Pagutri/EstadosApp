package com.example.android.estados;

//TODO: Hacer que no se borren los datos al cambiar de pantalla

import android.app.Activity;
import android.content.Intent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GestureDetectorCompat;

public class VanDerWaalsActivity extends Activity {

    final double R_1 = 0.082; // atm*lts/mol*K
    double volum, presion, moles, temp, cte_R, par_a, par_b;
    double R_p, R_v;
    int i; //Contador
    int iter = 100; //Máximo de iteraciones para el newton-raphson
    double epsilon = 0.001; //Precisión de newton-raphson
    double guess; //Pruebas del newton-raphson
    String uni_P, uni_V, uni_T;
    private EditText volumEditText;
    private EditText presionEditText;
    private EditText molesEditText;
    private EditText tempEditText;
    private EditText parAEditText;
    private EditText parBEditText;

    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vanderwaals);

        // OnClickListeners para botones de calcular
        TextView presionBtn = (TextView) findViewById(R.id.calc_P_btn);

        presionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcularPresion(view);
            }
        });

        TextView volumBtn = (TextView) findViewById(R.id.calc_V_btn);

        volumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcularVolumen(view);
            }
        });

        TextView molesBtn = (TextView) findViewById(R.id.calc_n_btn);

        molesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcularMoles(view);
            }
        });

        TextView tempBtn = (TextView) findViewById(R.id.calc_T_btn);

        tempBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcularTemp(view);
            }
        });

        TextView factBtn = (TextView) findViewById(R.id.calc_Z_btn);

        factBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcularFactor(view);
            }
        });

        TextView ResetButton = (TextView) findViewById(R.id.reset_btn);

        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrarTodo(view);
            }
        });

        // Programación de los spinners
        Spinner presionSpinner = (Spinner) findViewById(R.id.presion_spinner);
        ArrayAdapter<CharSequence> presionAdapter = ArrayAdapter.createFromResource(this,
                R.array.presion_array, android.R.layout.simple_spinner_item);
        presionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        presionSpinner.setAdapter(presionAdapter);

        presionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                elegirRP(pos);
                elegirUniP(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner volumSpinner = (Spinner) findViewById(R.id.volum_spinner);
        ArrayAdapter<CharSequence> volumAdapter = ArrayAdapter.createFromResource(this,
                R.array.volum_array, android.R.layout.simple_spinner_item);
        volumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        volumSpinner.setAdapter(volumAdapter);

        volumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                elegirRV(pos);
                elegirUniV(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Spinner tempSpinner = (Spinner) findViewById(R.id.temp_spinner);
        ArrayAdapter<CharSequence> tempAdapter = ArrayAdapter.createFromResource(this,
                R.array.temp_array, android.R.layout.simple_spinner_item);
        tempAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tempSpinner.setAdapter(tempAdapter);

        tempSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                elegirUniT(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        volumEditText = (EditText) findViewById(R.id.volum_edit_text);
        presionEditText = (EditText) findViewById(R.id.presion_edit_text);
        molesEditText = (EditText) findViewById(R.id.moles_edit_text);
        tempEditText = (EditText) findViewById(R.id.temp_edit_text);
        parAEditText = (EditText) findViewById(R.id.par_a_edit_text);
        parBEditText = (EditText) findViewById(R.id.par_b_edit_text);

        gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());
    }

    // El siguiente código detecta si la pantalla se desliza a la derecha, para pasar a la
    // actividad de graficar
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        super.dispatchTouchEvent(ev);
        return gestureDetectorCompat.onTouchEvent(ev);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if(event2.getX() < event1.getX()){
                Intent intent = new Intent(
                        VanDerWaalsActivity.this, PlotVanDerWaalsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
            return true;
        }
    }

    public void elegirRP(int pos) { // Cambio en la constante R por unidades de presión
        switch (pos) {
            case 0:
                R_p = 1; // atm
                break;
            case 1:
                R_p = 101325; // Pa
                break;
            case 2:
                R_p = 1.01325; // bar
                break;
            case 3:
                R_p = 760.002; // mmHg
                break;
            case 4:
                R_p = 14.6959; // PSI
                break;
        }
    }

    public void elegirUniP(int pos) { // Cambio en la presentación del resultado por unidades de presión
        switch (pos) {
            case 0:
                uni_P = "atm";
                break;
            case 1:
                uni_P = "Pa";
                break;
            case 2:
                uni_P = "bar";
                break;
            case 3:
                uni_P = "mmHg";
                break;
            case 4:
                uni_P = "PSI";
                break;
        }
    }

    public void elegirRV(int pos) { // Cambio en la constante R por unidades de volumen
        switch (pos) {
            case 0:
                R_v = 1; // lts
                break;
            case 1:
                R_v = 0.001; // m3
                break;
        }
    }

    public void elegirUniV(int pos) { // Cambio en la presentación del resultado por unidades de volumen
        switch (pos) {
            case 0:
                uni_V = "lts";
                break;
            case 1:
                uni_V = "m3";
                break;
        }
    }

    public double convertirTempAK(int pos, double T) { // Cambiar la temp dada a temp absoluta
        switch (pos) {
            case 0: // Temperatura dada en K
                break;
            case 1: // Temperatura dada en °C
                T += 273.15;
                break;
            case 2: // Temperatura dada en °F
                T = 5 * (T - 32) / 9 + 273.15;
                break;
            case 3: // Temperatura dada en R
                T *= 5 / 9;
                break;
        }

        return T;
    }

    public double convertirTempDeK(int pos, double T) { // Cambiar la temp absoluta a la temp solicitada
        switch (pos) {
            case 0: // Convertir a K
                break;
            case 1: // Convertir a °C
                T -= 273.15;
                break;
            case 2: // Convertir a °F
                T = 9 * (T - 273.15) / 5 + 32.0;
                break;
            case 3: // Convertir a R
                T *= 9 / 5;
                break;
        }

        return T;
    }

    public void elegirUniT(int pos) { // Cambio en la presentación del resultado por unidades de temperatura
        switch (pos) {
            case 0:
                uni_T = "K";
                break;
            case 1:
                uni_T = "°C";
                break;
            case 2:
                uni_T = "°F";
                break;
            case 3:
                uni_T = "R";
                break;
        }
    }

    private void calcularPresion(View view) {

        // Revisar si los campos están vacíos
        if (TextUtils.isEmpty(volumEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            volum = Double.valueOf(volumEditText.getText().toString());
        }

        if (TextUtils.isEmpty(molesEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            moles = Double.valueOf(molesEditText.getText().toString());
        }

        if (TextUtils.isEmpty(tempEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            temp = Double.valueOf(tempEditText.getText().toString());

            Spinner tempSpinner = (Spinner) findViewById(R.id.temp_spinner);

            int pos = tempSpinner.getSelectedItemPosition();

            temp = convertirTempAK(pos, temp);
        }
        if (TextUtils.isEmpty(parAEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            par_a = Double.valueOf(parAEditText.getText().toString());
        }

        if (TextUtils.isEmpty(parBEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            par_b = Double.valueOf(parBEditText.getText().toString());
        }

        cte_R = R_1 * R_p * R_v;
        presion = (moles * cte_R * temp / (volum - (moles * par_b))) - (moles * moles * par_a / (volum * volum));
        String result = "P = " + String.format("%.4f", presion) + " " + uni_P;

        mostrarResultado(result);
    }

    private void calcularVolumen(View view) {

        // Revisar si los campos están vacíos
        if (TextUtils.isEmpty(presionEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            presion = Double.valueOf(presionEditText.getText().toString());
        }

        if (TextUtils.isEmpty(molesEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            moles = Double.valueOf(molesEditText.getText().toString());
        }

        if (TextUtils.isEmpty(tempEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            temp = Double.valueOf(tempEditText.getText().toString());

            Spinner tempSpinner = (Spinner) findViewById(R.id.temp_spinner);

            int pos = tempSpinner.getSelectedItemPosition();

            temp = convertirTempAK(pos, temp);
        }

        cte_R = R_1 * R_p * R_v;
        guess = moles * cte_R * temp / presion;

        //Aquí empieza el Newton-Raphson
        for (i = 0; i < iter; i++) {
            volum = metodoVolumen(guess, cte_R, presion);
            if (Math.abs(volum - guess) < epsilon) break;
            guess = volum;
        }

        String result = "V = " + String.format("%.4f", volum) + " " + uni_V;

        mostrarResultado(result);
    }

    private void calcularMoles(View view) {

        // Revisar si los campos están vacíos
        if (TextUtils.isEmpty(presionEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            presion = Double.valueOf(presionEditText.getText().toString());
        }

        if (TextUtils.isEmpty(volumEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            volum = Double.valueOf(volumEditText.getText().toString());
        }

        if (TextUtils.isEmpty(tempEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            temp = Double.valueOf(tempEditText.getText().toString());

            Spinner tempSpinner = (Spinner) findViewById(R.id.temp_spinner);

            int pos = tempSpinner.getSelectedItemPosition();

            temp = convertirTempAK(pos, temp);
        }

        cte_R = R_1 * R_p * R_v;
        moles = presion * volum / (cte_R * temp);
        String result = "n = " + String.format("%.4f", moles) + " moles";

        mostrarResultado(result);
    }

    private void calcularTemp(View view) {

        // Revisar si los campos están vacíos
        if (TextUtils.isEmpty(presionEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            presion = Double.valueOf(presionEditText.getText().toString());
        }

        if (TextUtils.isEmpty(volumEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            volum = Double.valueOf(volumEditText.getText().toString());
        }

        if (TextUtils.isEmpty(molesEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            moles = Double.valueOf(molesEditText.getText().toString());
        }

        if (TextUtils.isEmpty(parAEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            par_a = Double.valueOf(parAEditText.getText().toString());
        }

        if (TextUtils.isEmpty(parBEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            par_b = Double.valueOf(parBEditText.getText().toString());
        }

        cte_R = R_1 * R_v * R_p;
        temp = (presion + (moles * moles * par_a / (volum * volum))) * (volum - (moles * par_b)) / (moles * cte_R);

        Spinner tempSpinner = (Spinner) findViewById(R.id.temp_spinner);
        int pos = tempSpinner.getSelectedItemPosition();
        temp = convertirTempDeK(pos, temp);

        String result = "T = " + String.format("%.4f", temp) + " " + uni_T;

        mostrarResultado(result);
    }

    private void calcularFactor(View view) {

        // Revisar si los campos están vacíos
        if (TextUtils.isEmpty(tempEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            temp = Double.valueOf(tempEditText.getText().toString());

            Spinner tempSpinner = (Spinner) findViewById(R.id.temp_spinner);

            int pos = tempSpinner.getSelectedItemPosition();

            temp = convertirTempAK(pos, temp);
        }

        if (TextUtils.isEmpty(presionEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            presion = Double.valueOf(presionEditText.getText().toString());
        }

        if (TextUtils.isEmpty(volumEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            volum = Double.valueOf(volumEditText.getText().toString());
        }

        if (TextUtils.isEmpty(molesEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        } else {
            moles = Double.valueOf(molesEditText.getText().toString());
        }

        cte_R = R_1 * R_p * R_v;
        double factor = presion * volum / (cte_R * moles * temp);
        String result = "Z = " + String.format("%.4f", factor);

        mostrarResultado(result);
    }

    private void mostrarResultado(String message) {
        TextView resultadoTextView = (TextView) findViewById(R.id.resultado_text_view);
        resultadoTextView.setText(message);
    }

    private void borrarTodo(View view) {

        volumEditText.setText(null);
        presionEditText.setText(null);
        molesEditText.setText(null);
        tempEditText.setText(null);
        parAEditText.setText(null);
        parBEditText.setText(null);
        mostrarResultado("0");
    }

    private double metodoVolumen(double x, double miR, double miP) {
        double f = (miP + (moles * moles * par_a / (x * x))) * (x - (moles * par_b)) - (moles * miR * temp);
        double df = (miP + (moles * moles * par_a / (x * x))) + ((x - (moles * par_b)) * (-2.0 * moles * moles * par_a / Math.pow(x, 3)));

        return x - (f / df);
    }

}
