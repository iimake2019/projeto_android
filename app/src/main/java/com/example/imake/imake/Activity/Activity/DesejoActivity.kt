package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.os.Bundle

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

class DesejoActivity : AppCompatActivity() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ImageAdapter? = null

    private var mDatabaseRef: DatabaseReference? = null

    private var mUploads: MutableList<Foto>? = null

    private var u: Usuario? = null

    private lateinit var codigoSessao: String
    private var bd: FirebaseDatabase? = null
    private var referenceBd: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desejo)

        val intent = this.intent
        val bundle = intent.extras
        codigoSessao = bundle!!.getSerializable("codigoUsuario") as String
        bundle.clear()

        inicializaComponentes()
        inicializarFirebase()
        pesquisarInfo()

        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
    }

    private fun inicializaComponentes() {
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)

        mUploads = ArrayList()

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Desejos")
        val q = mDatabaseRef!!.child(codigoSessao)
        q.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val upload = postSnapshot.getValue(Foto::class.java)
                    mUploads!!.add(upload!!)
                }
                mAdapter = ImageAdapter(this@DesejoActivity, mUploads)

                mRecyclerView!!.adapter = mAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this@DesejoActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBd = bd!!.reference
    }

    fun pesquisarInfo() {
        val q = referenceBd!!.child("Usuario").child(codigoSessao)
        q.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                u = dataSnapshot.getValue(Usuario::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
}
