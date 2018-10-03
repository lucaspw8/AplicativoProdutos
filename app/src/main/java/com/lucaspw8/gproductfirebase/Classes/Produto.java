package com.lucaspw8.gproductfirebase.Classes;

import android.net.Uri;

/**
 * Created by lucas on 16/04/2018.
 */

public class Produto {
    private String nome;
    private String produtoKey;
    private float valor;
    private String descricao;
    private String emailEmpresa;
    private String imagemUrl;

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public String getProdutoKey() {
        return produtoKey;
    }

    public void setProdutoKey(String produtoKey) {
        this.produtoKey = produtoKey;
    }
}
