package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.example.imake.R
import com.example.imake.imake.Activity.Entidades.Usuario
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import de.hdodenhof.circleimageview.CircleImageView

class PerfilActivity : AppCompatActivity() {

    private lateinit var txtNome: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtNMakes: TextView
    private lateinit var txtCategoria: TextView
    private lateinit var imgPerfil: CircleImageView
    private lateinit var btnAvaliar: Button
    private lateinit var btnIniciarChat: Button

    private lateinit var imgStar1: ImageView
    private lateinit var imgStar2: ImageView
    private lateinit var imgStar3: ImageView
    private lateinit var imgStar4: ImageView
    private lateinit var imgStar5: ImageView

    private lateinit var imgPStar1: ImageView
    private lateinit var imgPStar2: ImageView
    private lateinit var imgPStar3: ImageView
    private lateinit var imgPStar4: ImageView
    private lateinit var imgPStar5: ImageView
    private  var verif = 0
    private  var Avaliacao = 0
    private  var u: Usuario? = null
    private lateinit var codigoSessao: String
    private lateinit var codigoUsuario: String
    private var bd: FirebaseDatabase? = null
    private var referenceBd: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val intent = this.intent
        val bundle = intent.extras
        codigoSessao = bundle!!.getSerializable("codigoSessao") as String
        codigoUsuario = bundle.getSerializable("codigoUsuario") as String
        Log.d("CodigoUsuario", codigoUsuario)
        bundle.clear()

        inicializarComponentes()
        inicializarFirebase()
        pesquisarInfo()

        btnIniciarChat.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("codigoSessao", codigoSessao)
            bundle.putString("codigoUsuario", codigoUsuario)
            val intent = Intent(this@PerfilActivity, ChatActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        btnAvaliar.setOnClickListener {
            val q = referenceBd!!.child("Usuario").child(codigoUsuario)
            q.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var u2: Usuario? = Usuario()
                    u2 = dataSnapshot.getValue(Usuario::class.java)
                    if (u2 == null) {
                        u2 = Usuario()
                    }
                    val nota: Float?
                    val soma = java.lang.Float.valueOf(u2.somaAv) + Avaliacao
                    val numero = Integer.valueOf(u2.nAvaliacao)

                    nota = soma / (numero + 1)
                    u2.somaAv = soma.toString()
                    u2.avaliacao = nota.toString()
                    u2.nAvaliacao = (numero + 1).toString()

                    if (referenceBd != null) {
                        referenceBd!!.child("Usuario").child(codigoUsuario).setValue(u2)
                    }
                    referenceBd = null

                    if (u!!.urlPerfil != null) {
                        Glide.with(applicationContext).load(u!!.urlPerfil).into(imgPerfil)
                    }
                    if (java.lang.Double.valueOf(u!!.avaliacao) < 2 && java.lang.Double.valueOf(u!!.avaliacao) >= 1) {
                        imgPStar1.setBackgroundResource(R.drawable.staryellow)
                    } else if (java.lang.Double.valueOf(u!!.avaliacao) < 3 && java.lang.Double.valueOf(u!!.avaliacao) >= 2) {
                        imgPStar1.setBackgroundResource(R.drawable.staryellow)
                        imgPStar2.setBackgroundResource(R.drawable.staryellow)
                    } else if (java.lang.Double.valueOf(u!!.avaliacao) < 4 && java.lang.Double.valueOf(u!!.avaliacao) >= 3) {
                        imgPStar1.setBackgroundResource(R.drawable.staryellow)
                        imgPStar2.setBackgroundResource(R.drawable.staryellow)
                        imgPStar3.setBackgroundResource(R.drawable.staryellow)
                    } else if (java.lang.Double.valueOf(u!!.avaliacao) < 5 && java.lang.Double.valueOf(u!!.avaliacao) >= 4) {
                        imgPStar1.setBackgroundResource(R.drawable.staryellow)
                        imgPStar2.setBackgroundResource(R.drawable.staryellow)
                        imgPStar3.setBackgroundResource(R.drawable.staryellow)
                        imgPStar4.setBackgroundResource(R.drawable.staryellow)
                    } else {
                        imgPStar1.setBackgroundResource(R.drawable.staryellow)
                        imgPStar2.setBackgroundResource(R.drawable.staryellow)
                        imgPStar3.setBackgroundResource(R.drawable.staryellow)
                        imgPStar4.setBackgroundResource(R.drawable.staryellow)
                        imgPStar5.setBackgroundResource(R.drawable.staryellow)
                    }
                    imgStar1.visibility = View.INVISIBLE
                    imgStar2.visibility = View.INVISIBLE
                    imgStar3.visibility = View.INVISIBLE
                    imgStar4.visibility = View.INVISIBLE
                    imgStar5.visibility = View.INVISIBLE
                    btnAvaliar.visibility = View.INVISIBLE
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }

        //CLICK DAS ESTRELAS
        imgStar1.setOnClickListener {
            Avaliacao = 1
            imgStar1.setBackgroundResource(R.drawable.staryellow)
            imgStar2.setBackgroundResource(R.drawable.starvoid)
            imgStar3.setBackgroundResource(R.drawable.starvoid)
            imgStar4.setBackgroundResource(R.drawable.starvoid)
            imgStar5.setBackgroundResource(R.drawable.starvoid)
        }
        imgStar2.setOnClickListener {
            Avaliacao = 2
            imgStar1.setBackgroundResource(R.drawable.staryellow)
            imgStar2.setBackgroundResource(R.drawable.staryellow)
            imgStar3.setBackgroundResource(R.drawable.starvoid)
            imgStar4.setBackgroundResource(R.drawable.starvoid)
            imgStar5.setBackgroundResource(R.drawable.starvoid)
        }
        imgStar3.setOnClickListener {
            Avaliacao = 3
            imgStar1.setBackgroundResource(R.drawable.staryellow)
            imgStar2.setBackgroundResource(R.drawable.staryellow)
            imgStar3.setBackgroundResource(R.drawable.staryellow)
            imgStar4.setBackgroundResource(R.drawable.starvoid)
            imgStar5.setBackgroundResource(R.drawable.starvoid)
        }
        imgStar4.setOnClickListener {
            Avaliacao = 4
            imgStar1.setBackgroundResource(R.drawable.staryellow)
            imgStar2.setBackgroundResource(R.drawable.staryellow)
            imgStar3.setBackgroundResource(R.drawable.staryellow)
            imgStar4.setBackgroundResource(R.drawable.staryellow)
            imgStar5.setBackgroundResource(R.drawable.starvoid)
        }
        imgStar5.setOnClickListener {
            Avaliacao = 5
            imgStar1.setBackgroundResource(R.drawable.staryellow)
            imgStar2.setBackgroundResource(R.drawable.staryellow)
            imgStar3.setBackgroundResource(R.drawable.staryellow)
            imgStar4.setBackgroundResource(R.drawable.staryellow)
            imgStar5.setBackgroundResource(R.drawable.staryellow)
        }

        //FIM CLICK DAS ESTRELAS
    }

    private fun inicializarComponentes() {
        imgStar1 = findViewById(R.id.imgStar1) as ImageView
        imgStar2 = findViewById(R.id.imgStar2) as ImageView
        imgStar3 = findViewById(R.id.imgStar3) as ImageView
        imgStar4 = findViewById(R.id.imgStar4) as ImageView
        imgStar5 = findViewById(R.id.imgStar5) as ImageView

        imgPStar1 = findViewById(R.id.imgPStar1) as ImageView
        imgPStar2 = findViewById(R.id.imgPStar2) as ImageView
        imgPStar3 = findViewById(R.id.imgPStar3) as ImageView
        imgPStar4 = findViewById(R.id.imgPStar4) as ImageView
        imgPStar5 = findViewById(R.id.imgPStar5) as ImageView

        txtNome = findViewById(R.id.txtNome) as TextView
        txtEmail = findViewById(R.id.txtEmail) as TextView
        txtCategoria = findViewById(R.id.txtCategoria) as TextView
        txtNMakes = findViewById(R.id.txtNMakes) as TextView
        imgPerfil = findViewById(R.id.imgPerfil) as CircleImageView
        btnAvaliar = findViewById(R.id.btnAvaliar) as Button
        btnIniciarChat = findViewById(R.id.btnIniciarChat) as Button
    }

    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this@PerfilActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBd = bd!!.reference
    }

    fun pesquisarInfo() {
        val q = referenceBd!!.child("Usuario").child(codigoUsuario)
        q.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                u = dataSnapshot.getValue(Usuario::class.java)
                if (u == null) {
                    u = Usuario()
                }
                txtNome.text = u!!.nome.toString()
                txtEmail.text = u!!.email.toString()
                txtCategoria.text = "Categoria: " + u!!.categoria.toString()
                txtNMakes.text = u!!.numeroMakes.toString() + " makes"
                if (u!!.urlPerfil != null) {
                    Glide.with(applicationContext).load(u!!.urlPerfil).into(imgPerfil)
                }
                if (java.lang.Double.valueOf(u!!.avaliacao) < 2 && java.lang.Double.valueOf(u!!.avaliacao) >= 1) {
                    imgPStar1.setBackgroundResource(R.drawable.staryellow)
                } else if (java.lang.Double.valueOf(u!!.avaliacao) < 3 && java.lang.Double.valueOf(u!!.avaliacao) >= 2) {
                    imgPStar1.setBackgroundResource(R.drawable.staryellow)
                    imgPStar2.setBackgroundResource(R.drawable.staryellow)
                } else if (java.lang.Double.valueOf(u!!.avaliacao) < 4 && java.lang.Double.valueOf(u!!.avaliacao) >= 3) {
                    imgPStar1.setBackgroundResource(R.drawable.staryellow)
                    imgPStar2.setBackgroundResource(R.drawable.staryellow)
                    imgPStar3.setBackgroundResource(R.drawable.staryellow)
                } else if (java.lang.Double.valueOf(u!!.avaliacao) < 5 && java.lang.Double.valueOf(u!!.avaliacao) >= 4) {
                    imgPStar1.setBackgroundResource(R.drawable.staryellow)
                    imgPStar2.setBackgroundResource(R.drawable.staryellow)
                    imgPStar3.setBackgroundResource(R.drawable.staryellow)
                    imgPStar4.setBackgroundResource(R.drawable.staryellow)
                } else {
                    imgPStar1.setBackgroundResource(R.drawable.staryellow)
                    imgPStar2.setBackgroundResource(R.drawable.staryellow)
                    imgPStar3.setBackgroundResource(R.drawable.staryellow)
                    imgPStar4.setBackgroundResource(R.drawable.staryellow)
                    imgPStar5.setBackgroundResource(R.drawable.staryellow)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
