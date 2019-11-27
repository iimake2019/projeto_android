package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView

import com.example.imake.R
import com.example.imake.imake.Activity.Entidades.Usuario
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AlterarDadosActivity : AppCompatActivity() {

    private var bd: FirebaseDatabase? = null
    private var referenceBd: DatabaseReference? = null

    private var u: Usuario? = null
    private lateinit var codigoSessao: String

    private lateinit var txtCategoria: TextView
    private lateinit var edtNome: EditText
    private lateinit var edtCPF: EditText
    private lateinit var edtTelefone: EditText
    private lateinit var edtEndereco: EditText
    private lateinit var edtCidade: EditText
    private lateinit var edtIdade: EditText
    private lateinit var spnCategorias: Spinner
    private lateinit var btnAlterar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alterar_dados)

        inicializarComponentes()
        inicializarFirebase()

        val intent = this.intent
        val bundle = intent.extras
        codigoSessao = bundle!!.getSerializable("codigoUsuario") as String
        bundle.clear()

        pesquisarInfo()

        btnAlterar.setOnClickListener {
            if (edtNome.text.toString() != "" && edtTelefone.text.toString() != "" && edtEndereco.text.toString() != "" && edtCidade.text.toString() != ""
                    && edtIdade.text.toString() != "") {
                if (spnCategorias.selectedItem == 0) {
                    u!!.categoria = "Casamento"
                } else if (spnCategorias.selectedItem == 1) {
                    u!!.categoria = "Debutante"
                } else if (spnCategorias.selectedItem == 2) {
                    u!!.categoria = "Festa"
                } else if (spnCategorias.selectedItem == 3) {
                    u!!.categoria = "Formatura"
                } else {
                    u!!.categoria = "Artistico"
                }
                if (u!!.tipoUsuario == "1") {
                    u!!.categoria = null
                }
                u!!.nome = edtNome.text.toString()
                u!!.telefone = edtTelefone.text.toString()
                u!!.endereco = edtEndereco.text.toString()
                u!!.cidade = edtCidade.text.toString()
                u!!.idade = edtIdade.text.toString()

                referenceBd!!.child("Usuario").child(codigoSessao).setValue(u)
            }
        }
    }

    private fun inicializarComponentes() {
        edtNome = findViewById(R.id.edtNome) as EditText
        edtCPF = findViewById(R.id.edtCPF) as EditText
        edtTelefone = findViewById(R.id.edtTelefone) as EditText
        edtEndereco = findViewById(R.id.edtEndereco) as EditText
        edtCidade = findViewById(R.id.edtCidade) as EditText
        edtIdade = findViewById(R.id.edtIdade) as EditText
        txtCategoria = findViewById(R.id.txtCategoria) as TextView
        spnCategorias = findViewById(R.id.spnCategorias)
        btnAlterar = findViewById(R.id.btnAlterar) as Button
        val adapter = ArrayAdapter.createFromResource(this, R.array.categoria, R.layout.spinner_item)
        spnCategorias.adapter = adapter


    }

    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this@AlterarDadosActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBd = bd!!.reference
    }

    fun pesquisarInfo() {
        val q = referenceBd!!.child("Usuario").child(codigoSessao)
        q.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                u = dataSnapshot.getValue(Usuario::class.java)
                edtNome.setText(u!!.nome)
                edtCidade.setText(u!!.cidade)
                edtCPF.setText(u!!.cpf)
                edtEndereco.setText(u!!.endereco)
                edtIdade.setText(u!!.idade)
                edtTelefone.setText(u!!.telefone)
                Log.d("Spinner", u!!.tipoUsuario)
                if (u!!.tipoUsuario == "1") {
                    spnCategorias.visibility = View.INVISIBLE
                    txtCategoria.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
