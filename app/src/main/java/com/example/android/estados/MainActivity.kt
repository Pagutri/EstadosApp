package com.example.android.estados

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gasideal = findViewById<View>(R.id.gas_ideal_btn) as LinearLayout

        gasideal.setOnClickListener()
        {
            val gasidealIntent = Intent(this@MainActivity, GasIdealActivity::class.java)
            startActivity(gasidealIntent)
        }

        val dieterici = findViewById<View>(R.id.dieterici_btn) as LinearLayout

        dieterici.setOnClickListener()
        {
            val dietericiIntent = Intent(this@MainActivity, DietericiActivity::class.java)
            startActivity(dietericiIntent)
        }

        val vanderwaals = findViewById<View>(R.id.vanderwaals_btn) as LinearLayout

        vanderwaals.setOnClickListener()
        {
            val vanderwaalsIntent = Intent(this@MainActivity, VanDerWaalsActivity::class.java)
            startActivity(vanderwaalsIntent)
        }

        val margules = findViewById<View>(R.id.marg_btn) as LinearLayout

        margules.setOnClickListener()
        {
            val margulesIntent = Intent(this@MainActivity, MargulesActivity::class.java)
            startActivity(margulesIntent)
        }

        val pengrob = findViewById<View>(R.id.peng_btn) as LinearLayout

        pengrob.setOnClickListener()
        {
            val pengrobIntent = Intent(this@MainActivity, PengRobinsonActivity::class.java)
            startActivity(pengrobIntent)
        }

        val redlich = findViewById<View>(R.id.redlich_btn) as LinearLayout

        redlich.setOnClickListener()
        {
            val redlichIntent = Intent(this@MainActivity, RedlichKwongActivity::class.java)
            startActivity(redlichIntent)
        }

        val soave = findViewById<View>(R.id.soave_btn) as LinearLayout

        soave.setOnClickListener()
        {
            val soaveIntent = Intent(this@MainActivity, SoaveActivity::class.java)
            startActivity(soaveIntent)
        }

        val wilson = findViewById<View>(R.id.wilson_btn) as LinearLayout

        wilson.setOnClickListener()
        {
            val wilsonIntent = Intent(this@MainActivity, WilsonActivity::class.java)
            startActivity(wilsonIntent)
        }

        val vanlaar = findViewById<View>(R.id.vanlaar_btn) as LinearLayout

        vanlaar.setOnClickListener()
        {
            val vanlaarIntent = Intent(this@MainActivity, VanLaarActivity::class.java)
            startActivity(vanlaarIntent)
        }

    }
}
