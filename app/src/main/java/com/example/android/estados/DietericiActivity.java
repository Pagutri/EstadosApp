package com.example.android.estados;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DietericiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dieterici);

        TextView calcularBtn = (TextView) findViewById(R.id.calc_P_btn);

        calcularBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calcularPresion(view);
            }
        });

        TextView resetButton = (TextView) findViewById(R.id.reset_btn);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrarTodo(view);
            }
        });
    }

    double volum, par_a, par_b, temp;

    public void calcularPresion(View view) {
        EditText volumEditText = (EditText) findViewById(R.id.volum_edit_text);
        EditText parAEditText = (EditText) findViewById(R.id.par_a_edit_text);
        EditText parBEditText = (EditText) findViewById(R.id.par_b_edit_text);
        EditText tempEditText = (EditText) findViewById(R.id.temp_edit_text);

        // Revisar si los campos están vacíos
        if(TextUtils.isEmpty(volumEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            volum = Double.valueOf(volumEditText.getText().toString());
        }

        if(TextUtils.isEmpty(parAEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            par_a = Double.valueOf(parAEditText.getText().toString());
        }

        if(TextUtils.isEmpty(parBEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            par_b = Double.valueOf(parBEditText.getText().toString());
        }

        if(TextUtils.isEmpty(tempEditText.getText().toString())) {
            Toast.makeText(this, R.string.falta_algo, Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            temp = Double.valueOf(tempEditText.getText().toString());
        }

        double R = 0.082; // atm*lts/mol*K, por ahora
        double presion = R*temp*(Math.exp(-par_a/(volum*R*temp))/(volum-par_b));
        String pConUni = "P = " + String.format("%.2f", presion) + " atm";

        mostrarResultado(pConUni);
    }

    private void mostrarResultado(String message) {
        TextView resultadoTextView = (TextView) findViewById(R.id.resultado_text_view);
        resultadoTextView.setText(message);
    }

    public void borrarTodo(View view) {
        EditText volumEditText = (EditText) findViewById(R.id.volum_edit_text);
        EditText parAEditText = (EditText) findViewById(R.id.par_a_edit_text);
        EditText parBEditText = (EditText) findViewById(R.id.par_b_edit_text);
        EditText tempEditText = (EditText) findViewById(R.id.temp_edit_text);

        volumEditText.setText(null);
        parAEditText.setText(null);
        parBEditText.setText(null);
        tempEditText.setText(null);
        mostrarResultado("0");
    }
}
