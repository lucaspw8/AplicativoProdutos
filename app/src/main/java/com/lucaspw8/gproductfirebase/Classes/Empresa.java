package com.lucaspw8.gproductfirebase.Classes;

/**
 * Created by lucas on 29/05/2018.
 */

public class Empresa {
    private String nome;
    private String telefone;
    private String rua;
    private String bairro;
    private String numero;
    private String complemento;
    private String uidUsuario;
    private String keyEmpresa;

    public String getKeyEmpresa() {
        return keyEmpresa;
    }

    public void setKeyEmpresa(String keyEmpresa) {
        this.keyEmpresa = keyEmpresa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getUidUsuario() {
        return uidUsuario;
    }

    public void setUidUsuario(String uidUsuario) {
        this.uidUsuario = uidUsuario;
    }
}
