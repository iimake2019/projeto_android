package com.example.imake.imake.Activity.Activity

import java.text.DecimalFormat
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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.example.imake.R
import com.example.imake.imake.Activity.Entidades.Usuario
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import de.hdodenhof.circleimageview.CircleImageView

class ProfActivity : AppCompatActivity() {
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

    private lateinit var txtNome: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtNMakes: TextView
    private lateinit var txtCategoria: TextView
    private lateinit var txtVerLocalizacao: TextView
    private  var btnVerVideos: Button? = null
    private lateinit var txtAlterarFotos: TextView
    private lateinit var btnVerFotos: Button
    private lateinit var imgPerfil: CircleImageView
    private lateinit var imgbConfirmar: ImageButton
    private lateinit var btnOpConta: Button
    private lateinit var imgStar1: ImageView
    private lateinit var imgStar2: ImageView
    private lateinit var imgStar3: ImageView
    private lateinit var imgStar4: ImageView
    private lateinit var imgStar5: ImageView
    private lateinit var txtAvaliacao: TextView

    private lateinit var intm: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prof)

        supportActionBar!!.hide()

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

        val bundle = intent.extras
        codigoSessao = bundle!!.getSerializable("codigoUsuario") as String
        bundle.clear()

        pesquisarInfo()


        btnVerFotos.setOnClickListener {
            intm = Intent(this@ProfActivity, FotosProfActivity::class.java)
            intm.putExtra("codigoUsuario", u!!.codigo)
            startActivity(intm)
        }
        txtAlterarFotos.setOnClickListener {
            intm = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intm, 1)
        }
        imgbConfirmar.setOnClickListener { uploadFile() }
        btnOpConta.setOnClickListener {
            intm = Intent(this@ProfActivity, OpcoesContaActivity::class.java)
            intm.putExtra("codigoUsuario", u!!.codigo)
            startActivity(intm)
        }

        /*
           btnDeletarConta.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfActivity.this);
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
                                                                   referenceBd.child("FotosUsuario").child(codigoSessao).removeValue();
                                                                   Query q = referenceBd.child("FotosGeral").orderByChild("codigo").equalTo(codigoSessao);
                                                                   q.addValueEventListener(new ValueEventListener() {
                                                                       @Override
                                                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                           referenceBd.child("Usuario").child(codigoSessao).removeValue();
                                                                           referenceBd.child("Desejos").child(codigoSessao).removeValue();
                                                                           referenceBd.child("Favoritos").child(codigoSessao).removeValue();
                                                                           referenceBd.child("FotosUsuario").child(codigoSessao).removeValue();
                                                                           Query q = referenceBd.child("FotosGeral").orderByChild("codigo").equalTo(codigoSessao);
                                                                           q.addValueEventListener(new ValueEventListener() {
                                                                               @Override
                                                                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                   referenceBd.removeEventListener(this);
                                                                                   Intent intent = new Intent(ProfActivity.this, SplashActivity.class);
                                                                                   startActivity(intent);

                                                                                   SharedPreferences.Editor editor = sessao.edit();
                                                                                   editor.remove("codigo");
                                                                                   editor.commit();

                                                                                   finish();
                                                                               }

                                                                               @Override
                                                                               public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                               }
                                                                           });
                                                                       }

                                                                       @Override
                                                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                       }
                                                                   });
                                                                   finish();
                                                                   Intent intent = new Intent(ProfActivity.this, SplashActivity.class);
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
                            Toast.makeText(this@ProfActivity, "Upload Sucessful", Toast.LENGTH_SHORT).show()
                            u!!.urlPerfil = uri.toString()
                            mDatabaseRef!!.child(codigoSessao).setValue(u)
                        }
                    }
                    .addOnFailureListener { e -> Toast.makeText(this@ProfActivity, e.message, Toast.LENGTH_SHORT).show() }
        } else {
            Toast.makeText(this@ProfActivity, "No File Selected.", Toast.LENGTH_SHORT).show()
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
        txtAvaliacao = findViewById(R.id.txtAvaliacao) as TextView
        txtNome = findViewById(R.id.txtNome) as TextView
        txtEmail = findViewById(R.id.txtEmail) as TextView
        txtCategoria = findViewById(R.id.txtCategoria) as TextView
        txtNMakes = findViewById(R.id.txtNMakes) as TextView
        txtAlterarFotos = findViewById(R.id.txtAlterarFoto) as TextView
        btnVerFotos = findViewById(R.id.btnVerFotos) as Button
        imgbConfirmar = findViewById(R.id.imgbConfirmar) as ImageButton
        imgPerfil = findViewById(R.id.imgPerfil) as CircleImageView
        btnOpConta = findViewById(R.id.btnOpConta) as Button

        imgStar1 = findViewById(R.id.imgStar1) as ImageView
        imgStar2 = findViewById(R.id.imgStar2) as ImageView
        imgStar3 = findViewById(R.id.imgStar3) as ImageView
        imgStar4 = findViewById(R.id.imgStar4) as ImageView
        imgStar5 = findViewById(R.id.imgStar5) as ImageView
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
                txtCategoria.text = "Categoria: " + u!!.categoria.toString()
                txtNMakes.text = u!!.numeroMakes.toString() + " makes"
                val df = DecimalFormat("#,###.0")

                txtAvaliacao.text = df.format(java.lang.Double.valueOf(u!!.avaliacao)).toString()
                if (u!!.urlPerfil != null) {
                    Glide.with(applicationContext).load(u!!.urlPerfil).into(imgPerfil)
                }
                if (u!!.avaliacao.equals("0")) {
                    imgStar1.setBackgroundResource(R.drawable.starvoid)
                    imgStar2.setBackgroundResource(R.drawable.starvoid)
                    imgStar3.setBackgroundResource(R.drawable.starvoid)
                    imgStar4.setBackgroundResource(R.drawable.starvoid)
                    imgStar5.setBackgroundResource(R.drawable.starvoid)
                } else if (java.lang.Double.valueOf(u!!.avaliacao) < 2 && java.lang.Double.valueOf(u!!.avaliacao) >= 1) {
                    imgStar1.setBackgroundResource(R.drawable.staryellow)
                } else if (java.lang.Double.valueOf(u!!.avaliacao) < 3 && java.lang.Double.valueOf(u!!.avaliacao) >= 2) {
                    imgStar1.setBackgroundResource(R.drawable.staryellow)
                    imgStar2.setBackgroundResource(R.drawable.staryellow)
                } else if (java.lang.Double.valueOf(u!!.avaliacao) < 4 && java.lang.Double.valueOf(u!!.avaliacao) >= 3) {
                    imgStar1.setBackgroundResource(R.drawable.staryellow)
                    imgStar2.setBackgroundResource(R.drawable.staryellow)
                    imgStar3.setBackgroundResource(R.drawable.staryellow)
                } else if (java.lang.Double.valueOf(u!!.avaliacao) < 5 && java.lang.Double.valueOf(u!!.avaliacao) >= 4) {
                    imgStar1.setBackgroundResource(R.drawable.staryellow)
                    imgStar2.setBackgroundResource(R.drawable.staryellow)
                    imgStar3.setBackgroundResource(R.drawable.staryellow)
                    imgStar4.setBackgroundResource(R.drawable.staryellow)
                } else {
                    imgStar1.setBackgroundResource(R.drawable.staryellow)
                    imgStar2.setBackgroundResource(R.drawable.staryellow)
                    imgStar3.setBackgroundResource(R.drawable.staryellow)
                    imgStar4.setBackgroundResource(R.drawable.staryellow)
                    imgStar5.setBackgroundResource(R.drawable.staryellow)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun inicializarFirebase() {
        FirebaseApp.initializeApp(this@ProfActivity)
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/")
        referenceBd = bd!!.reference
        referenceBd2 = bd!!.reference

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads")
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Usuario")
    }
}
