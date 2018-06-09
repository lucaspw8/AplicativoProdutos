package com.lucaspw8.gproductfirebase.Helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lucas on 09/06/2018.
 */

public class Preferencias {
    private Context context;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "app.preferencias";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private final  String  EMAIL_USU_LOGADO = "email_usu_logado";
    private final String SENHA_USU_LOGADO = "senha_usu_logado";
    private final String TIPO_USU_LOGADO = "tipo_usu_logado";

    public Preferencias(Context contextoParametro){
        context = contextoParametro;
        preferences = context.getSharedPreferences(NOME_ARQUIVO,MODE);

        //Associar o preferences.edit()
        editor = preferences.edit();
    }

    public void salvarUsu(String email, String senha){
        //Salvar dentro do arquivo de preferencias
        editor.putString(EMAIL_USU_LOGADO,email);
        editor.putString(SENHA_USU_LOGADO,senha);

        editor.commit();
    }

    public void salvarTipoUsu(String tipoUsu){
        editor.putString(TIPO_USU_LOGADO,tipoUsu);
        editor.commit();
    }
    //Retorna o email do usuário logado
    public String getEmailUsu(){
        return preferences.getString(EMAIL_USU_LOGADO,null);
    }
    //Retorna a senha do usuário logado
    public String getSenhaUsu(){
        return preferences.getString(SENHA_USU_LOGADO,null);
    }

    //Retorna o tipo do usuário logado
    public String getTipoUsu(){
        return preferences.getString(TIPO_USU_LOGADO,null);
    }
}


