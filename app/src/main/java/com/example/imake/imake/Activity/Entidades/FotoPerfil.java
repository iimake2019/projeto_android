package com.example.imake.imake.Activity.Entidades;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class FotoPerfil {
    String mImageUrl;
    String mName;

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> hasMapFotoPerfil = new HashMap<>();
        hasMapFotoPerfil.put("mName", getName());
        hasMapFotoPerfil.put("mImageUrl", getImageUrl());
        return hasMapFotoPerfil;
    }
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }


    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }
}
