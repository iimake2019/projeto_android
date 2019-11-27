package com.example.imake.imake.Activity.DAO;


import androidx.annotation.NonNull;
import java.text.DecimalFormat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.imake.R;
import com.example.imake.imake.Activity.Activity.FotosProfActivity;
import com.example.imake.imake.Activity.Activity.OpcoesContaActivity;
import com.example.imake.imake.Activity.Entidades.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfActivity extends AppCompatActivity {
    private final int GALERIA_IMAGENS = 1;
    private final int PERMISSAO_REQUEST = 2;

    private FirebaseDatabase bd;
    private DatabaseReference referenceBd;
    private DatabaseReference referenceBd2;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    Uri selectedImage;

    Usuario u;

    String codigoSessao;

    TextView txtNome;
    TextView txtEmail;
    TextView txtNMakes;
    TextView txtCategoria;
    TextView txtVerLocalizacao;
    Button btnVerVideos;
    TextView txtAlterarFotos;
    Button btnVerFotos;
    Button btnConfirmar;
    CircleImageView imgPerfil;
    ImageButton imgbConfirmar;
    Button btnOpConta;
    ImageView imgStar1;
    ImageView imgStar2;
    ImageView imgStar3;
    ImageView imgStar4;
    ImageView imgStar5;
    TextView txtAvaliacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof);

        getSupportActionBar().hide();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSAO_REQUEST);
            }
        }
        inicializarFirebase();
        inicializaComponentes();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        codigoSessao = (String) bundle.getSerializable("codigoUsuario");
        bundle.clear();

        pesquisarInfo();


        btnVerFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("codigoUsuario", u.getCodigo());
                Intent intent = new Intent(ProfActivity.this, FotosProfActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        txtAlterarFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);

            }
        });
        imgbConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
        btnOpConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("codigoUsuario", u.getCodigo());
                Intent intent = new Intent(ProfActivity.this, OpcoesContaActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

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

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (selectedImage != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(selectedImage));

            fileReference.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(ProfActivity.this, "Upload Sucessful", Toast.LENGTH_SHORT).show();
                                    Uri download = uri;
                                    u.setUrlPerfil(download.toString());
                                    mDatabaseRef.child(codigoSessao).setValue(u);
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(ProfActivity.this, "No File Selected.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GALERIA_IMAGENS) {
            selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            imgPerfil.setImageBitmap(thumbnail);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSAO_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // A permissão foi concedida. Pode continuar
            } else {
                // A permissão foi negada. Precisa ver o que deve ser desabilitado
            }
            return;
        }
    }

    public void inicializaComponentes() {
        txtAvaliacao = (TextView) findViewById(R.id.txtAvaliacao);
        txtNome = (TextView) findViewById(R.id.txtNome);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtCategoria = (TextView) findViewById(R.id.txtCategoria);
        txtNMakes = (TextView) findViewById(R.id.txtNMakes);
        txtAlterarFotos = (TextView) findViewById(R.id.txtAlterarFoto);
        btnVerFotos = (Button) findViewById(R.id.btnVerFotos);
        btnConfirmar = (Button) findViewById(R.id.btnConfirmar);
        imgbConfirmar = (ImageButton) findViewById(R.id.imgbConfirmar);
        imgPerfil = (CircleImageView) findViewById(R.id.imgPerfil);
        btnOpConta = (Button) findViewById(R.id.btnOpConta);

        imgStar1 = (ImageView) findViewById(R.id.imgStar1);
        imgStar2 = (ImageView) findViewById(R.id.imgStar2);
        imgStar3 = (ImageView) findViewById(R.id.imgStar3);
        imgStar4 = (ImageView) findViewById(R.id.imgStar4);
        imgStar5 = (ImageView) findViewById(R.id.imgStar5);
    }

    public void pesquisarImagemPerfil() {
        Query q = referenceBd2.child("Usuario").child(codigoSessao);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario u = dataSnapshot.getValue(Usuario.class);
                //String url = String.valueOf(perfil.getImageUrl());
                //String url = dataSnapshot.getValue().toString();
                //url = "https://firebasestorage.googleapis.com/v0/b/imake-b81bb.appspot.com/o/uploads%2F1569829996574.jpg?alt=media&token=05c3bdaa-6728-42d4-845a-b8b5fc63cc43";
                //String url = dataSnapshot.getValue().toString();
                String url = String.valueOf(u.getUrlPerfil());
                Glide.with(getApplicationContext()).load(url).into(imgPerfil);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void pesquisarInfo() {
        Query q = referenceBd.child("Usuario").child(codigoSessao);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                u = dataSnapshot.getValue(Usuario.class);
                if(u==null){
                    u = new Usuario();
                }
                txtNome.setText(String.valueOf(u.getNome()));
                txtEmail.setText(String.valueOf(u.getEmail()));
                txtCategoria.setText("Categoria: " + String.valueOf(u.getCategoria()));
                txtNMakes.setText(String.valueOf(u.getNumeroMakes()) + " makes");
                DecimalFormat df = new DecimalFormat("#,###.0");

                txtAvaliacao.setText(String.valueOf(df.format(Double.valueOf(u.getAvaliacao()))));
                if (u.getUrlPerfil() != null) {
                    Glide.with(getApplicationContext()).load(u.getUrlPerfil()).into(imgPerfil);
                }
                if(Double.valueOf(u.getAvaliacao()) == 0){
                    imgStar1.setBackgroundResource(R.drawable.starvoid);
                    imgStar2.setBackgroundResource(R.drawable.starvoid);
                    imgStar3.setBackgroundResource(R.drawable.starvoid);
                    imgStar4.setBackgroundResource(R.drawable.starvoid);
                    imgStar5.setBackgroundResource(R.drawable.starvoid);
                }
                else if(Double.valueOf(u.getAvaliacao())<2 && Double.valueOf(u.getAvaliacao())>=1){
                    imgStar1.setBackgroundResource(R.drawable.staryellow);
                }
                else if(Double.valueOf(u.getAvaliacao())<3 && Double.valueOf(u.getAvaliacao())>=2){
                    imgStar1.setBackgroundResource(R.drawable.staryellow);
                    imgStar2.setBackgroundResource(R.drawable.staryellow);
                }
                else if(Double.valueOf(u.getAvaliacao())<4 && Double.valueOf(u.getAvaliacao())>=3){
                    imgStar1.setBackgroundResource(R.drawable.staryellow);
                    imgStar2.setBackgroundResource(R.drawable.staryellow);
                    imgStar3.setBackgroundResource(R.drawable.staryellow);
                }
                else if(Double.valueOf(u.getAvaliacao())<5 && Double.valueOf(u.getAvaliacao())>=4){
                    imgStar1.setBackgroundResource(R.drawable.staryellow);
                    imgStar2.setBackgroundResource(R.drawable.staryellow);
                    imgStar3.setBackgroundResource(R.drawable.staryellow);
                    imgStar4.setBackgroundResource(R.drawable.staryellow);
                }
                else{
                    imgStar1.setBackgroundResource(R.drawable.staryellow);
                    imgStar2.setBackgroundResource(R.drawable.staryellow);
                    imgStar3.setBackgroundResource(R.drawable.staryellow);
                    imgStar4.setBackgroundResource(R.drawable.staryellow);
                    imgStar5.setBackgroundResource(R.drawable.staryellow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(ProfActivity.this);
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/");
        referenceBd = bd.getReference();
        referenceBd2 = bd.getReference();

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Usuario");
    }
}