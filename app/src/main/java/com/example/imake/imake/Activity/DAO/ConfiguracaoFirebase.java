package com.example.imake.imake.Activity.DAO;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {
    private static DatabaseReference refernceBD;
    private FirebaseDatabase bd;
    private static FirebaseAuth autenticacao;

    public ConfiguracaoFirebase(Context context){
        FirebaseApp.initializeApp(context);
    }

    public static DatabaseReference getFirebase(){
        if(refernceBD == null){
            refernceBD = FirebaseDatabase.getInstance().getReference();
        }
        return refernceBD;
    }

    public static FirebaseAuth getFirebaseAutenticacao(){
        if(autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }

        return autenticacao;
    }
}
