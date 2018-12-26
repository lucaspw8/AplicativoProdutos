package com.lucaspw8.gproductfirebase.Classes;

/**
 * Created by lucas on 16/04/2018.
 */

public class Produto{
    private String nome;
    private String KeyProduto;
    private float valor;
    private String descricao;
    private String uidUsuario;
    private String imagemUrl;
    private String imagemReference;


    public String getImagemReference() {
        return imagemReference;
    }

    public void setImagemReference(String imagemReference) {
        this.imagemReference = imagemReference;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
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

    public String getKeyProduto() {
        return KeyProduto;
    }

    public void setKeyProduto(String keyProduto) {
        this.KeyProduto = keyProduto;
    }

}
