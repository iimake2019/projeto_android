package com.example.imake.imake.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imake.R;
import com.example.imake.imake.Activity.DAO.ImageAdapter;
import com.example.imake.imake.Activity.Entidades.Contact;
import com.example.imake.imake.Activity.Entidades.Foto;
import com.example.imake.imake.Activity.Entidades.Message;
import com.example.imake.imake.Activity.Entidades.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    String codigoSessao, codigoUsuario;
    String urlFrom, urlTo, nameTo, nameFrom;
    private GroupAdapter adapter;
    private FirebaseDatabase bd;
    private DatabaseReference referenceBd;

    Button btnChat;
    EditText edtChat;
    private DatabaseReference referenceBd2;
    private DatabaseReference referenceBd3;

    Usuario me;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        codigoSessao = (String) bundle.getSerializable("codigoSessao");
        codigoUsuario = (String) bundle.getSerializable("codigoUsuario");

        bundle.clear();
        inicializarFirebase();
        Query q2 = referenceBd.child("Usuario").child(codigoSessao);
        q2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                urlFrom = usuario.getUrlPerfil();
                nameFrom = usuario.getNome();
                getSupportActionBar().setTitle(usuario.getNome());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //NOME NA ACTION BAR

        Query q = referenceBd.child("Usuario").child(codigoUsuario);
        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                urlTo = usuario.getUrlPerfil();
                nameTo = usuario.getNome();
                getSupportActionBar().setTitle(usuario.getNome());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        inicializarComponentes();
        RecyclerView rv = findViewById(R.id.recycler_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        adapter = new GroupAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        fetchMessages();

    }

    public void fetchMessages(){

        FirebaseFirestore.getInstance().collection("/conversas")
                .document(codigoSessao)
                .collection(codigoUsuario)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                        if(documentChanges!=null){
                            for (DocumentChange doc: documentChanges) {
                                if(doc.getType() == DocumentChange.Type.ADDED){
                                    Message message = doc.getDocument().toObject(Message.class);
                                    Log.d("Teste: ", message.getText());
                                   // Toast.makeText(ChatActivity.this, "PEgou!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
                                    adapter.add(new MessageItem(message));
                                }
                            }
                        }
                    }
                });
    }

    private void sendMessage() {
        String text = edtChat.getText().toString();
        edtChat.setText(null);

        String fromId = codigoSessao;
        String toId = codigoUsuario;

        long timestamp = System.currentTimeMillis();
        final Message message = new Message();

        message.setFromId(fromId);
        message.setToId(toId);
        message.setTimestamp(timestamp);
        message.setText(text);

       // Log.d("Ids", codigoSessao + codigoUsuario);
        if(!message.getText().isEmpty()){

            FirebaseFirestore.getInstance().collection("/conversas")
                    .document(codigoSessao)
                    .collection(codigoUsuario)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Tteste: ", "Vez");
                            Contact contact = new Contact();
                            contact.setUuid(codigoUsuario);
                            contact.setPhotoUrl(urlTo);
                            contact.setTimestamp(message.getTimestamp());
                            contact.setLastMessage(message.getText());
                            contact.setUsername(nameTo);
                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(codigoSessao)
                                    .collection("contacts")
                                    .document(codigoUsuario)
                                    .set(contact);
                        }
                    });

            FirebaseFirestore.getInstance().collection("/conversas")
                    .document(codigoUsuario)
                    .collection(codigoSessao)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Contact contact = new Contact();
                            contact.setUuid(codigoSessao);
                            contact.setPhotoUrl(urlFrom);
                            contact.setUsername(nameFrom);
                            contact.setTimestamp(message.getTimestamp());
                            contact.setLastMessage(message.getText());
                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(codigoUsuario)
                                    .collection("contacts")
                                    .document(codigoSessao)
                                    .set(contact);
                        }
                    });
        }
    }

    private void inicializarComponentes() {
        btnChat = (Button) findViewById(R.id.btnEnviar);
        edtChat = (EditText) findViewById(R.id.edtChat);
    }

    private class MessageItem extends Item<ViewHolder>{

        private final Message message;

        private MessageItem(Message message) {

            this.message = message;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            Log.d("CodigoUsuario: ", codigoUsuario);
            Log.d("CodigoSessao: ", codigoSessao);


            TextView txtMsg = viewHolder.itemView.findViewById(R.id.txtMsg);
            ImageView imgMessage = viewHolder.itemView.findViewById(R.id.imgMessageUser);

            txtMsg.setText(message.getText());
            Log.d("FromID: ", codigoSessao);

            if(message.getFromId().equals(codigoSessao)){
                Picasso.get().load(urlFrom).into(imgMessage);
            }
            else{
                Picasso.get().load(urlTo).into(imgMessage);
            }
        }

        @Override
        public int getLayout() {
            return message.getFromId().equals(codigoSessao)
                    ? R.layout.item_to_message
                    : R.layout.item_from_message;
        }
    }
    private void inicializarFirebase(){
        FirebaseApp.initializeApp(ChatActivity.this);
        bd = FirebaseDatabase.getInstance("https://imake-b81bb.firebaseio.com/");
        referenceBd = bd.getReference();
        referenceBd2 = bd.getReference();
        referenceBd3 = bd.getReference();
    }
}
