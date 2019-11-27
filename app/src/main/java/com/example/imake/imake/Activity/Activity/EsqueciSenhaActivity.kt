package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.example.imake.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class EsqueciSenhaActivity : AppCompatActivity() {
    private lateinit var edtEmail: EditText
    private lateinit var btnRedefenirSenha: Button

    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esqueci_senha)

        edtEmail = findViewById(R.id.edtEmail) as EditText
        btnRedefenirSenha = findViewById(R.id.btnRedefenirSenha) as Button
        firebaseAuth = FirebaseAuth.getInstance()

        btnRedefenirSenha.setOnClickListener {
            if (edtEmail.text.toString() == "") {
                Toast.makeText(this@EsqueciSenhaActivity, "Preencha o campo senha.", Toast.LENGTH_SHORT).show()
            } else {
                firebaseAuth!!.sendPasswordResetEmail(edtEmail.text.toString()).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        edtEmail.setText("")
                        Toast.makeText(this@EsqueciSenhaActivity, "E-mail de redefinição enviado.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@EsqueciSenhaActivity, "E-mail não cadastrado, tente novamente.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
