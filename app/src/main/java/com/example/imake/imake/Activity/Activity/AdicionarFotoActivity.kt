package com.example.imake.imake.Activity.Activity

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

import com.example.imake.R
import com.example.imake.imake.Activity.Entidades.Foto
import com.example.imake.imake.Activity.Entidades.Usuario
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AdicionarFotoActivity : AppCompatActivity() {
    private val GALERIA_IMAGENS = 1
    private val PERMISSAO_REQUEST = 2

    private var bd: FirebaseDatabase? = null
    private var referenceBd: DatabaseReference? = null
    private var referenceBd2: DatabaseReference? = null
    private var mStorageRef: StorageReference? = null
    private var mDatabaseRef: DatabaseReference? = null
    private var mDatabaseRef2: DatabaseReference? = null
    private var selectedImage: Uri? = null

    private var u: Usuario? = null
    private var numeroMake: Int = 0

    private lateinit var codigoSessao: String


    private lateinit var imgAdd: ImageView
    private lateinit var btnAdicionar: Button
    private lateinit var btnEscolherFoto: Button
    private lateinit var edtNomeFoto: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adicionar_foto)

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSAO_REQUEST)
            }
        }



        inicializarFirebase()
        inicializaComponentes()


        val intent = this.intent
        val bundle = intent.extras
        codigoSessao = bundle!!.getSerializable("codigoUsuario") as String
        bundle.clear()

        pesquisarInfo()

        btnEscolherFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }
        btnAdicionar.setOnClickListener { uploadFile() }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun uploadFile() {
        if (selectedImage != null) {
            val fileReference = mStorageRef!!.child(System.currentTimeMillis().toString()
                    + "." + getFileExtension(selectedImage!!))

            fileReference.putFile(selectedImage!!)
                    .addOnSuccessListener {
                        fileReference.downloadUrl.addOnSuccessListener { uri ->
                            Toast.makeText(this@AdicionarFotoActivity, "Upload Sucessful", Toast.LENGTH_SHORT).show()
                            val foto = Foto()
                            foto.url = uri.toString()
                            foto.codigoFoto = foto.gerarCodigo(edtNomeFoto.text.toString())
                            foto.categoria = u!!.categoria
                            foto.codigo = codigoSessao
                            foto.nomeAutor = u!!.nome

                            mDatabaseRef2!!.child(foto.codigoFoto).setValue(foto)

                            mDatabaseRef!!.child(codigoSessao).child(foto.codigoFoto).setValue(foto)

                            numeroMake = Integer.valueOf(u!!.numeroMakes) + 1
                            u!!.numeroMakes = numeroMake.toString()
                            referenceBd!!.child("Usuario").child(codigoSessao).setValue(u)
                            edtNomeFoto.setText("")
                        }
                    }
                    .addOnFailureListener { e -> Toast.makeText(this@AdicionarFotoActivity, e.message, Toast.LENGTH_SHORT).show() }
        } else {
            Toast.makeText(this@AdicionarFotoActivity, "No File Selected.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == GALERIA_IMAGENS) {
            selectedImage = data!!.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val c = contentResolver.query(selectedImage!!, filePath, null, null, null)
            c!!.moveToFirst()
            val columnIndex = c.getColumnIndex(filePath[0])
            val picturePath = c.getString(columnIndex)
            c.close()
            val thumbnail = BitmapFactory.decodeFile(picturePath)
            imgAdd.setImageBitmap(thumbnail)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSAO_REQUEST) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // A permissão foi concedida. Pode continuar
            } else {
                // A permissão foi negada. Precisa ver o que deve ser desabilitado
            }
            return
        }
    }

    fun inicializaComponentes() {
        imgAdd = findViewById(R.id.imgAdd) as ImageView
        btnAdicionar = findViewById(R.id.btnConfirmar) as Button
        btnEscolherFoto = findViewById(R.id.btnEscolherFoto) as Button
        edtNomeFoto = findViewById(R.id.edtNomeFoto) as EditText
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
        FirebaseApp.initializeApp(this@AdicionarFotoActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBd = bd!!.reference
        referenceBd2 = bd!!.reference

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads")

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("FotosUsuario")

        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("FotosGeral")
    }
}
