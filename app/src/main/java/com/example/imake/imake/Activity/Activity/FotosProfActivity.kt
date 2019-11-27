package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

import com.example.imake.R
import com.example.imake.imake.Activity.DAO.ImageAdapter
import com.example.imake.imake.Activity.Entidades.Foto
import com.example.imake.imake.Activity.Entidades.Usuario
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import java.util.ArrayList

class FotosProfActivity : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ImageAdapter? = null


    private var mDatabaseRef3: DatabaseReference? = null
    private var mUploads: MutableList<Foto>? = null

    private lateinit var btnAdicionarFoto: Button

    private var u: Usuario? = null

    private lateinit var codigoSessao: String
    private var bd: FirebaseDatabase? = null
    private var referenceBd: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotos_prof)

        val intent = this.intent
        val bundle = intent.extras
        codigoSessao = bundle!!.getSerializable("codigoUsuario") as String
        bundle.clear()
        //pesquisarInfo();

        inicializaComponentes()
        inicializarFirebase()
        btnAdicionarFoto.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("codigoUsuario", codigoSessao)
            val intent = Intent(this@FotosProfActivity, AdicionarFotoActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            mUploads!!.clear()
        }
        mDatabaseRef3!!.child(codigoSessao).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val upload = postSnapshot.getValue(Foto::class.java)
                    mUploads!!.add(upload!!)
                }
                mAdapter = ImageAdapter(this@FotosProfActivity, mUploads)

                mRecyclerView!!.adapter = mAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@FotosProfActivity, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun pesquisarInfo() {
        val q = referenceBd!!.child("FotosUsuario").child(codigoSessao)
        q.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                u = dataSnapshot.getValue(Usuario::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@FotosProfActivity, databaseError.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun inicializaComponentes() {
        btnAdicionarFoto = findViewById(R.id.btnAdiconarFoto) as Button
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)

        mUploads = ArrayList()

        mDatabaseRef3 = FirebaseDatabase.getInstance().getReference("FotosUsuario")
    }

    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this@FotosProfActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBd = bd!!.reference
    }
}
