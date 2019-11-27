package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import com.example.imake.R
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var  btnComecar: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
        btnComecar = findViewById(R.id.btnComecar)

        btnComecar.setOnClickListener {
            val intent = Intent(this@SplashActivity, LoginCadastroActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
