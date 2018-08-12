package com.lucaspw8.gproductfirebase.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.lucaspw8.gproductfirebase.Classes.Empresa;

public class EmpresaPreferencias {
    private Context context;
    private SharedPreferences preferences;
    private String  NOME_ARQUIVO = "app.empresapreferencias";
    private int MODE =0;
    private SharedPreferences.Editor editor;

    private final String NOME_EMPRESA = "nome_empresa";
    private final String TELEFONE = "telefone";
    private final String RUA = "rua";
    private final String BAIRRO = "bairro";
    private final String NUMERO = "numero";
    private final String COMPLEMENTO ="complemento";

    public EmpresaPreferencias(Context contextParametro) {
        context = contextParametro;
        preferences = context.getSharedPreferences(NOME_ARQUIVO,MODE);

        editor = preferences.edit();
    }

    /**
     * Salva os dados da empresa no SharedPreferences
     * @param empresa
     */
    public void salvarEmpresa(Empresa empresa){
        //Salva dentro do arquivo de preferencias
        editor.putString(NOME_EMPRESA,empresa.getNome());
        editor.putInt(TELEFONE,empresa.getTelefone());
        editor.putString(RUA,empresa.getRua());
        editor.putString(BAIRRO,empresa.getBairro());
        editor.putInt(NUMERO,empresa.getNumero());
        editor.putString(COMPLEMENTO,empresa.getComplemento());

        editor.commit();
    }
    //Retorna os dados da empresa
    public Empresa getEmpresa(){
        Empresa empresa = new Empresa();
        empresa.setNome(preferences.getString(NOME_EMPRESA,null));
        empresa.setBairro(preferences.getString(BAIRRO,null));
        empresa.setTelefone(preferences.getInt(TELEFONE,-1));
        empresa.setRua(preferences.getString(RUA,null));
        empresa.setNumero(preferences.getInt(NUMERO,-1));
        empresa.setComplemento(preferences.getString(COMPLEMENTO,null));

        return empresa;
    }

    public void limparDados(){
        editor.putString(NOME_EMPRESA,null);
        editor.putInt(TELEFONE,-1);
        editor.putString(RUA,null);
        editor.putString(BAIRRO,null);
        editor.putInt(NUMERO,-1);
        editor.putString(COMPLEMENTO,null);
        editor.commit();
    }
}
