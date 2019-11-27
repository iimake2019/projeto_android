package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

import com.example.imake.R
import com.example.imake.imake.Activity.Entidades.Usuario
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OpcoesContaActivity:AppCompatActivity() {
    private lateinit var btnAlterarDados:Button
    private lateinit var btnDeletarConta:Button

    private var bd:FirebaseDatabase? = null
    private var referenceBd:DatabaseReference? = null
    private  var u:Usuario? = null

    private lateinit var codigoSessao:String


    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opcoes_conta)

        val intent = this.intent
        val bundle = intent.extras
        codigoSessao = bundle!!.getSerializable("codigoUsuario") as String
        bundle!!.clear()

        inicializarFirebase()
        inicializaComponentes()
        pesquisarInfo()
        btnAlterarDados.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("codigoUsuario", u!!.codigo)
            val intent = Intent(this@OpcoesContaActivity, AlterarDadosActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        btnDeletarConta.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view:View) {
                val builder1 = AlertDialog.Builder(this@OpcoesContaActivity)
                builder1.setMessage("Tem certeza?")
                builder1.setCancelable(true)

                builder1.setPositiveButton(
                        "Sim"
                ) { dialog, id ->
                    val user = FirebaseAuth.getInstance().currentUser
                    val credential = EmailAuthProvider
                            .getCredential(codigoSessao, u!!.senha)

                    user!!.reauthenticate(credential)
                            .addOnCompleteListener {
                                user!!.delete()
                                        .addOnCompleteListener {
                                            Log.d("Resultado", "Deletou")
                                            referenceBd!!.child("Usuario").child(codigoSessao).removeValue()
                                            referenceBd!!.child("Desejos").child(codigoSessao).removeValue()
                                            referenceBd!!.child("Favoritos").child(codigoSessao).removeValue()
                                            val q = referenceBd!!.child("FotosGeral").orderByChild("codigo").equalTo(codigoSessao)
                                            q.addValueEventListener(object:ValueEventListener {
                                                override fun onDataChange(dataSnapshot:DataSnapshot) {
                                                    referenceBd!!.child("Usuario").child(codigoSessao).removeValue()
                                                    referenceBd!!.child("Desejos").child(codigoSessao).removeValue()
                                                    referenceBd!!.child("Favoritos").child(codigoSessao).removeValue()
                                                    referenceBd!!.child("FotosUsuario").child(codigoSessao).removeValue()
                                                    referenceBd!!.child("FotosGeral").addListenerForSingleValueEvent(object:ValueEventListener {
                                                        override fun onDataChange(snapshot:DataSnapshot) {
                                                            for (dataSnapshot in snapshot.children) {
                                                                if (dataSnapshot.child("codigo").value!!.toString() == codigoSessao) {
                                                                    dataSnapshot.ref.setValue(null)
                                                                }
                                                            }
                                                            finish()
                                                            val intent = Intent(this@OpcoesContaActivity, SplashActivity::class.java)
                                                            startActivity(intent)
                                                        }

                                                        override fun onCancelled(databaseError:DatabaseError) {

                                                        }
                                                    })

                                                }

                                                override fun onCancelled(databaseError:DatabaseError) {

                                                }
                                            })
                                            finish()
                                            val intent = Intent(this@OpcoesContaActivity, SplashActivity::class.java)
                                            startActivity(intent)
                                        }
                            }
                }

                builder1.setNegativeButton(
                        "Não",
                        object:DialogInterface.OnClickListener {
                            override fun onClick(dialog:DialogInterface, id:Int) {
                                dialog.cancel()
                            }
                        })

                val alert11 = builder1.create()
                alert11.show()
            }
        })
    }
    fun pesquisarInfo() {
        val q = referenceBd!!.child("Usuario").child(codigoSessao)
        q.addValueEventListener(object:ValueEventListener {
            override fun onDataChange(dataSnapshot:DataSnapshot) {
                u = dataSnapshot.getValue(Usuario::class.java!!)
            }

            override fun onCancelled(databaseError:DatabaseError) {

            }
        })
    }
    fun inicializaComponentes() {
        btnDeletarConta = findViewById(R.id.btnDeletarConta) as Button
        btnAlterarDados = findViewById(R.id.btnAlterarDados) as Button
    }
    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this@OpcoesContaActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBd = bd!!.reference
    }
}
