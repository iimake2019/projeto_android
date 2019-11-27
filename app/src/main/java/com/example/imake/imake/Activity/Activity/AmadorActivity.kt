package com.example.imake.imake.Activity.Activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle

import com.bumptech.glide.Glide
import com.example.imake.R
import com.example.imake.imake.Activity.Entidades.Usuario

import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import androidx.appcompat.app.AppCompatActivity

import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class AmadorActivity : AppCompatActivity() {


    private val GALERIA_IMAGENS = 1
    private val PERMISSAO_REQUEST = 2

    private var bd: FirebaseDatabase? = null
    private var referenceBd: DatabaseReference? = null
    private var referenceBd2: DatabaseReference? = null
    private var mStorageRef: StorageReference? = null
    private var mDatabaseRef: DatabaseReference? = null
    private var selectedImage: Uri? = null

    private var u: Usuario? = null

    private lateinit var codigoSessao: String

    private lateinit var btnOpConta: Button
    private lateinit var imgPerfil: ImageView
    private lateinit var txtAlterarFoto: TextView
    private lateinit var imgbConfirmar: ImageButton
    private lateinit var txtNome: TextView
    private lateinit var txtEmail: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amador)

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

        imgbConfirmar.setOnClickListener { uploadFile() }
        btnOpConta.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("codigoUsuario", u!!.codigo)
            val intent = Intent(this@AmadorActivity, OpcaoContaAActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        txtAlterarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }
        /*btnDeletarConta.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   AlertDialog.Builder builder1 = new AlertDialog.Builder(AmadorActivity.this);
                   builder1.setMessage("Tem certeza?");
                   builder1.setCancelable(true);

                   builder1.setPositiveButton(
                           "Sim",
                           new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                   AuthCredential credential = EmailAuthProvider
                                           .getCredential(codigoSessao, u.getSenha());

                                   user.reauthenticate(credential)
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   user.delete()
                                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task) {
                                                                   Log.d("Resultado", "Deletou");
                                                                   referenceBd.child("Usuario").child(codigoSessao).removeValue();
                                                                   referenceBd.child("Desejos").child(codigoSessao).removeValue();
                                                                   referenceBd.child("Favoritos").child(codigoSessao).removeValue();
                                                                   finish();
                                                                   Intent intent = new Intent(AmadorActivity.this, SplashActivity.class);
                                                                   startActivity(intent);


                                                               }
                                                           });
                                               }
                                           });
                               }
                           });

                   builder1.setNegativeButton(
                           "Não",
                           new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   dialog.cancel();
                               }
                           });

                   AlertDialog alert11 = builder1.create();
                   alert11.show();
               }
           });*/
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
                            Toast.makeText(this@AmadorActivity, "Upload Sucessful", Toast.LENGTH_SHORT).show()
                            u!!.urlPerfil = uri.toString()
                            mDatabaseRef!!.child(codigoSessao).setValue(u)
                        }
                    }
                    .addOnFailureListener { e -> Toast.makeText(this@AmadorActivity, e.message, Toast.LENGTH_SHORT).show() }
        } else {
            Toast.makeText(this@AmadorActivity, "No File Selected.", Toast.LENGTH_SHORT).show()
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
            imgPerfil.setImageBitmap(thumbnail)
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
        txtNome = findViewById(R.id.txtNome) as TextView
        txtEmail = findViewById(R.id.txtEmail) as TextView
        txtAlterarFoto = findViewById(R.id.txtAlterarFoto) as TextView
        imgbConfirmar = findViewById(R.id.imgbConfirmar) as ImageButton
        btnOpConta = findViewById(R.id.btnOpConta) as Button
        imgPerfil = findViewById(R.id.imgPerfil) as ImageView
    }

    fun pesquisarImagemPerfil() {
        val q = referenceBd2!!.child("Usuario").child(codigoSessao)
        q.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val u = dataSnapshot.getValue(Usuario::class.java)
                //String url = String.valueOf(perfil.getImageUrl());
                //String url = dataSnapshot.getValue().toString();
                //url = "https://firebasestorage.googleapis.com/v0/b/imake-b81bb.appspot.com/o/uploads%2F1569829996574.jpg?alt=media&token=05c3bdaa-6728-42d4-845a-b8b5fc63cc43";
                //String url = dataSnapshot.getValue().toString();
                val url = u!!.urlPerfil.toString()
                Glide.with(applicationContext).load(url).into(imgPerfil)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun pesquisarInfo() {
        val q = referenceBd!!.child("Usuario").child(codigoSessao)
        q.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                u = dataSnapshot.getValue(Usuario::class.java)
                if (u == null) {
                    u = Usuario()
                }
                txtNome.text = u!!.nome.toString()
                txtEmail.text = u!!.email.toString()
                if (u!!.urlPerfil != null) {
                    Glide.with(applicationContext).load(u!!.urlPerfil).into(imgPerfil)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this@AmadorActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBd = bd!!.reference
        referenceBd2 = bd!!.reference

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads")
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Usuario")
    }

}