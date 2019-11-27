package com.example.imake.imake.Activity.Activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

import com.example.imake.R
import com.example.imake.imake.Activity.Entidades.Usuario
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PerfilAmadorActivity : AppCompatActivity() {
    private var bd: FirebaseDatabase? = null
    private var referenceBd: DatabaseReference? = null

    private var u: Usuario? = null

    private var codigoSessao: String? = null

    private var sessao: SharedPreferences? = null
    private var finalizarsessao: SharedPreferences.Editor? = null

    private lateinit var txtNome: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtNMakes: TextView
    private lateinit var txtCategoria: TextView
    private lateinit var txtVerLocalizacao: TextView
    private lateinit var btnVerVideos: Button
    private lateinit var btnVerFotos: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_amador)

        inicializarFirebase()
        inicializaComponentes()

        sessao = getSharedPreferences("dadosSessao", Context.MODE_PRIVATE)
        finalizarsessao = sessao!!.edit()

        codigoSessao = sessao!!.getString("codigo", "Vazio")
        pesquisarInfo()

    }

    fun inicializaComponentes() {
        txtNome = findViewById(R.id.txtNome) as TextView
        txtEmail = findViewById(R.id.txtEmail) as TextView
        txtCategoria = findViewById(R.id.txtCategoria) as TextView
        txtNMakes = findViewById(R.id.txtNMakes) as TextView
        btnVerFotos = findViewById(R.id.btnVerFotos) as Button
    }

    fun pesquisarInfo() {
        val q = referenceBd!!.child("Usuario").child(codigoSessao!!)
        q.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                u = dataSnapshot.getValue(Usuario::class.java)
                txtNome.text = u!!.nome.toString()
                txtEmail.text = u!!.email.toString()
                txtCategoria.text = "Categoria: " + u!!.categoria.toString()
                txtNMakes.text = u!!.numeroMakes.toString() + " makes"
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this@PerfilAmadorActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBd = bd!!.reference
    }
}
