package com.lucaspw8.gproductfirebase.Classes;

/**
 * Created by lucas on 16/04/2018.
 */

public class Produto {
    private String nome;
    private float valor;
    private String emailEmpresa;

    public String getEmailEmpresa() {
        return emailEmpresa;
    }

    public void setEmailEmpresa(String emailEmpresa) {
        this.emailEmpresa = emailEmpresa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }
}
