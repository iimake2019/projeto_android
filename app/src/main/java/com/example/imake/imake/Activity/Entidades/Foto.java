package com.example.imake.imake.Activity.Entidades;

public class Foto {
    String nomeAutor;
    String codigo;
    String categoria;
    String codigoFoto;
    String url;

    public String getCodigoFoto() {
        return codigoFoto;
    }

    public void setCodigoFoto(String codigoFoto) {
        this.codigoFoto = codigoFoto;
    }

    public Foto() {
    }

    public Foto(String nomeAutor, String codigo, String categoria, String codigoFoto, String url) {
        this.nomeAutor = nomeAutor;
        this.codigo = codigo;
        this.categoria = categoria;
        this.codigoFoto = codigoFoto;
        this.url = url;
    }

    public String gerarCodigo(String nomeFoto){
        String c = String.valueOf(nomeFoto.hashCode());

        return c;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNomeAutor() {
        return nomeAutor;
    }

    public void setNomeAutor(String nomeAutor) {
        this.nomeAutor = nomeAutor;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
