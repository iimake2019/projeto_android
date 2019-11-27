package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button

import com.example.imake.R
import com.google.firebase.auth.FirebaseAuth

class InicialActivity : AppCompatActivity() {
    private lateinit var btnComecar : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_inicial)

            btnComecar = findViewById(R.id.btnComecar)
            btnComecar.setOnClickListener(View.OnClickListener { startActivity(Intent(this@InicialActivity, LoginCadastroActivity::class.java)) })
        } else {
            super.onCreate(savedInstanceState)
            val intent = Intent(this@InicialActivity, DrawerNavigationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}