package com.example.android.estados;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout gasideal = (LinearLayout) findViewById(R.id.gas_ideal_btn);

        gasideal.setOnClickListener(new OnClickListener() {
            // The code in this method will be executed when the gas ideal button is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link GasIdealActivity}
                Intent gasidealIntent = new Intent(MainActivity.this, GasIdealActivity.class);
                // Start the new activity
                startActivity(gasidealIntent);
            }
        });

        LinearLayout dieterici = (LinearLayout) findViewById(R.id.dieterici_btn);

        dieterici.setOnClickListener(new OnClickListener() {
            // The code in this method will be executed when the dieterici button is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link DietericiActivity}
                Intent dietericiIntent = new Intent(MainActivity.this, DietericiActivity.class);
                // Start the new activity
                startActivity(dietericiIntent);
            }
        });

        LinearLayout vanderwaals = (LinearLayout) findViewById(R.id.vanderwaals_btn);

        vanderwaals.setOnClickListener(new OnClickListener() {
            // The code in this method will be executed when the vanderwaals button is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link VanDerWaalsActivity}
                Intent vanderwaalsIntent = new Intent(MainActivity.this, VanDerWaalsActivity.class);
                // Start the new activity
                startActivity(vanderwaalsIntent);
            }
        });

        LinearLayout margules = (LinearLayout) findViewById(R.id.marg_btn);

        margules.setOnClickListener(new OnClickListener() {
            // The code in this method will be executed when the margules button is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link MargulesActivity}
                Intent margulesIntent = new Intent(MainActivity.this, MargulesActivity.class);
                // Start the new activity
                startActivity(margulesIntent);
            }
        });

        LinearLayout pengrob = (LinearLayout) findViewById(R.id.peng_btn);

        pengrob.setOnClickListener(new OnClickListener() {
            // The code in this method will be executed when the peng button is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link PengRobinsonActivity}
                Intent pengrobIntent = new Intent(MainActivity.this, PengRobinsonActivity.class);
                // Start the new activity
                startActivity(pengrobIntent);
            }
        });

        LinearLayout redlich = (LinearLayout) findViewById(R.id.redlich_btn);

        redlich.setOnClickListener(new OnClickListener() {
            // The code in this method will be executed when the redlich button is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link RedlichKwongActivity}
                Intent redlichIntent = new Intent(MainActivity.this, RedlichKwongActivity.class);
                // Start the new activity
                startActivity(redlichIntent);
            }
        });

        LinearLayout soave = (LinearLayout) findViewById(R.id.soave_btn);

        soave.setOnClickListener(new OnClickListener() {
            // The code in this method will be executed when the soave button is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link SoaveActivity}
                Intent soaveIntent = new Intent(MainActivity.this, SoaveActivity.class);
                // Start the new activity
                startActivity(soaveIntent);
            }
        });

        LinearLayout wilson = (LinearLayout) findViewById(R.id.wilson_btn);

        wilson.setOnClickListener(new OnClickListener() {
            // The code in this method will be executed when the wilson button is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link WilsonActivity}
                Intent wilsonIntent = new Intent(MainActivity.this, WilsonActivity.class);
                // Start the new activity
                startActivity(wilsonIntent);
            }
        });

        LinearLayout vanlaar = (LinearLayout) findViewById(R.id.vanlaar_btn);

        vanlaar.setOnClickListener(new OnClickListener() {
            // The code in this method will be executed when the vanlaar button is clicked on.
            @Override
            public void onClick(View view) {
                // Create a new intent to open the {@link VanLaarActivity}
                Intent vanlaarIntent = new Intent(MainActivity.this, VanLaarActivity.class);
                // Start the new activity
                startActivity(vanlaarIntent);
            }
        });

    }
}
