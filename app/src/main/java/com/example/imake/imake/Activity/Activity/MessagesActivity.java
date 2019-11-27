package com.example.imake.imake.Activity.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imake.R;
import com.example.imake.imake.Activity.Entidades.Contact;
import com.example.imake.imake.Activity.Entidades.Message;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    String codigoSessao;
    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        final Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        codigoSessao = (String) bundle.getSerializable("codigoSessao");
        bundle.clear();
        RecyclerView rv = findViewById(R.id.recycler_contact);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                ContactItem contactItem = (ContactItem) item;
                //intent.putExtra("contact", contactItem.contact.getUuid());

                Bundle bundle = new Bundle();
                bundle.putString("codigoUsuario", contactItem.contact.getUuid());
                bundle.putString("codigoSessao", codigoSessao);
                //Log.d("CodigoUsuario: ", contactItem.contact.getUuid());
                //Log.d("CodigoSessao: ", codigoSessao);
                //bundle.putString("codigoSessao", );
                Intent intent = new Intent(MessagesActivity.this, ChatActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        fetchLastMessage();
    }

    private void fetchLastMessage() {
        FirebaseFirestore.getInstance().collection("last-messages")
                .document(codigoSessao)
                .collection( "contacts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                        if(documentChanges != null){
                            for (DocumentChange doc: documentChanges) {
                                if(doc.getType() == DocumentChange.Type.ADDED){
                                    Contact contact = doc.getDocument().toObject(Contact.class);
                                    adapter.add(new ContactItem(contact));
                                }
                            }
                        }
                    }
                });
    }

    private class ContactItem extends Item<ViewHolder>{

        private final Contact contact;

        private ContactItem(Contact contact) {
            this.contact = contact;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView username = viewHolder.itemView.findViewById(R.id.textView);
            TextView message = viewHolder.itemView.findViewById(R.id.textView2);
            ImageView imgPhoto = viewHolder.itemView.findViewById(R.id.CircleImageView);

            username.setText(contact.getUsername());
            message.setText(contact.getLastMessage());
            Picasso.get().load(contact.getPhotoUrl()).into(imgPhoto);


        }

        @Override
        public int getLayout() {
            return R.layout.item_user_message;
        }
    }
}
