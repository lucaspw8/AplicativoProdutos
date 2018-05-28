package com.lucaspw8.gproductfirebase.Classes;

import com.google.firebase.database.Exclude;

/**
 * Created by lucas on 16/04/2018.
 */

public class Usuario {

    private String Nome;
    private String Email;
    private String Senha;
    private String TipoUsuario;


    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
    @Exclude
    public String getSenha() {
        return Senha;
    }
    @Exclude
    public void setSenha(String senha) {
        Senha = senha;
    }

    public String getTipoUsuario() {
        return TipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        TipoUsuario = tipoUsuario;
    }
}
