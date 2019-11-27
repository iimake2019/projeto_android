package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

class PesquisarActivity : AppCompatActivity(), ImageAdapter.OnItemClickListener {

    private lateinit var edtPesquisar: EditText
    private lateinit var btnPesquisar: Button

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ImageAdapter? = null

    private var mDatabaseRef: DatabaseReference? = null
    private var mDatabaseRef2: DatabaseReference? = null

    private var mUploads: MutableList<Foto>? = null

    private var u: Usuario? = null

    private lateinit var codigoSessao: String
    private var bd: FirebaseDatabase? = null
    private var referenceBd: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesquisar)

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

        btnPesquisar.setOnClickListener {
            mUploads!!.clear()
            mRecyclerView!!.removeAllViews()
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("FotosGeral")
            val q = mDatabaseRef!!.orderByChild("nomeAutor").startAt(edtPesquisar.text.toString())
                    .endAt(edtPesquisar.text.toString() + "\uf8ff")
            q.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {
                        val upload = postSnapshot.getValue(Foto::class.java)
                        mUploads!!.add(upload!!)
                    }
                    mAdapter = ImageAdapter(this@PesquisarActivity, mUploads)

                    mRecyclerView!!.adapter = mAdapter

                    mAdapter!!.setOnItemClickListener(this@PesquisarActivity)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
            val q2 = mDatabaseRef!!.orderByChild("categoria").startAt(edtPesquisar.text.toString())
                    .endAt(edtPesquisar.text.toString() + "\uf8ff")
            q2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {
                        val upload = postSnapshot.getValue(Foto::class.java)
                        mUploads!!.add(upload!!)
                    }
                    mAdapter = ImageAdapter(this@PesquisarActivity, mUploads)

                    mRecyclerView!!.adapter = mAdapter

                    mAdapter!!.setOnItemClickListener(this@PesquisarActivity)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }


    private fun inicializaComponentes() {

        edtPesquisar = findViewById(R.id.edtPesquisar) as EditText
        btnPesquisar = findViewById(R.id.btnPesquisar) as Button
        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)

        mUploads = ArrayList()
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

    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this@PesquisarActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBd = bd!!.reference
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(this@PesquisarActivity, "Normal click at position: $position", Toast.LENGTH_SHORT).show()
    }

    override fun onFavoritarClick(position: Int) {
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("Favoritos")
        var foto = Foto()
        foto = mUploads!![position]
        mDatabaseRef2!!.child(codigoSessao).child(foto.codigoFoto).setValue(foto)
    }

    override fun onDesejoClick(position: Int) {
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("Desejos")
        var foto = Foto()
        foto = mUploads!![position]
        mDatabaseRef2!!.child(codigoSessao).child(foto.codigoFoto).setValue(foto)
    }

    override fun onPerfilClick(position: Int) {
        val codigoUsuario = mUploads!![position].codigo
        val bundle = Bundle()
        bundle.putString("codigoUsuario", codigoUsuario)
        val intent = Intent(this@PesquisarActivity, PerfilActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}
