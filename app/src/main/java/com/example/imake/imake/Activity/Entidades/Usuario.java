package com.example.imake.imake.Activity.Entidades;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Usuario {
    String codigo;
    String nome;
    String email;
    String cpf;
    String telefone;
    String endereco;
    String cidade;
    String idade;
    String senha;
    String categoria;
    String tipoUsuario;
    String numeroMakes;
    String urlPerfil;
    String avaliacao;
    String NAvaliacao;
    String somaAv;

    public String getSomaAv() {
        return somaAv;
    }

    public void setSomaAv(String somaAv) {
        this.somaAv = somaAv;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String gerarCodigo(String email){
        String c = String.valueOf(email.hashCode());

        return c;
    }

    public String getUrlPerfil() {
        return urlPerfil;
    }

    public void setUrlPerfil(String urlPerfil) {
        this.urlPerfil = urlPerfil;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> hasMapUsuario = new HashMap<>();
        hasMapUsuario.put("id", getCodigo());
        hasMapUsuario.put("nome", getNome());
        hasMapUsuario.put("email", getEmail());
        hasMapUsuario.put("cpf",getCpf ());
        hasMapUsuario.put("telefone", getTelefone());
        hasMapUsuario.put("endereco", getEndereco());
        hasMapUsuario.put("cidade", getCidade());
        hasMapUsuario.put("idade", getIdade());
        hasMapUsuario.put("senha", getSenha());
        hasMapUsuario.put("categoria", getCategoria());
        hasMapUsuario.put("tipoUsuario", getTipoUsuario());
        hasMapUsuario.put("numeroMakes", getNumeroMakes());
        hasMapUsuario.put("urlPerfil", getUrlPerfil());
        hasMapUsuario.put("avaliacao", getAvaliacao());
        hasMapUsuario.put("NAvaliacao", getAvaliacao());
        hasMapUsuario.put("somaAv", getSomaAv());
        return hasMapUsuario;
    }

    public String getNAvaliacao() {
        return NAvaliacao;
    }

    public void setNAvaliacao(String NAvaliacao) {
        this.NAvaliacao = NAvaliacao;
    }

    public String getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(String avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getNumeroMakes() {
        return numeroMakes;
    }

    public void setNumeroMakes(String numeroMakes) {
        this.numeroMakes = numeroMakes;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
