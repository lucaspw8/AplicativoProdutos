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
    private final String UIDUSUARIO ="uidusuario";
    private final String KEY_EMPRESA ="key_empresa";

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
        editor.putString(TELEFONE,empresa.getTelefone());
        editor.putString(RUA,empresa.getRua());
        editor.putString(BAIRRO,empresa.getBairro());
        editor.putString(NUMERO,empresa.getNumero());
        editor.putString(COMPLEMENTO,empresa.getComplemento());
        editor.putString(UIDUSUARIO,empresa.getUidUsuario());
        editor.putString(KEY_EMPRESA,empresa.getKeyEmpresa());

        editor.commit();
    }

    /**
     * Retorna os dados da empresa
     * @return Empresa
     */
    public Empresa getEmpresa(){
        Empresa empresa = new Empresa();
        empresa.setNome(preferences.getString(NOME_EMPRESA,null));
        empresa.setBairro(preferences.getString(BAIRRO,null));
        empresa.setTelefone(preferences.getString(TELEFONE,null));
        empresa.setRua(preferences.getString(RUA,null));
        empresa.setNumero(preferences.getString(NUMERO,null));
        empresa.setComplemento(preferences.getString(COMPLEMENTO,null));
        empresa.setUidUsuario(preferences.getString(UIDUSUARIO,null));
        empresa.setKeyEmpresa(preferences.getString(KEY_EMPRESA,null));

        return empresa;
    }

    /**
     * Limpa todas as informações referentes a empresa
     */
    public void limparDados(){
        editor.putString(NOME_EMPRESA,null);
        editor.putString(TELEFONE,null);
        editor.putString(RUA,null);
        editor.putString(BAIRRO,null);
        editor.putString(NUMERO,null);
        editor.putString(COMPLEMENTO,null);
        editor.putString(UIDUSUARIO,null);
        editor.putString(KEY_EMPRESA,null);
        editor.commit();
    }
}
