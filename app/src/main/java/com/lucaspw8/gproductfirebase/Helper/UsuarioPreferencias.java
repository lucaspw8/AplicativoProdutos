package com.lucaspw8.gproductfirebase.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.lucaspw8.gproductfirebase.Classes.Usuario;

/**
 * Created by lucas on 09/06/2018.
 */

public class UsuarioPreferencias {
    private Context context;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "app.preferencias";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String NOME_USU_LOGADO = "nome_usu_logado";
    private final  String  EMAIL_USU_LOGADO = "email_usu_logado";
    private final  String  SENHA_USU_LOGADO = "senha_usu_logado";
    private final String TIPO_USU_LOGADO = "tipo_usu_logado";
    private final String UID_USU_LOGADO = "uid_usu_logado";
    private final String KEY_USU_LOGADO = "key_usu_logado";

    public UsuarioPreferencias(Context contextoParametro){
        context = contextoParametro;
        preferences = context.getSharedPreferences(NOME_ARQUIVO,MODE);

        //Associar o preferences.edit()
        editor = preferences.edit();
    }

    /**
     * Salva os dados do usuario logado
     * @param usuario
     */
    public void salvarUsu(Usuario usuario){
        //Seta os valores de preferencias
        editor.putString(NOME_USU_LOGADO,usuario.getNome());
        editor.putString(EMAIL_USU_LOGADO,usuario.getEmail());
        if(!usuario.getSenha().equals("")) {
            editor.putString(SENHA_USU_LOGADO, usuario.getSenha());
        }
        editor.putString(TIPO_USU_LOGADO,usuario.getTipoUsuario());
        editor.putString(UID_USU_LOGADO,usuario.getUidUsuario());
        editor.putString(KEY_USU_LOGADO,usuario.getKeyUsuario());
        //Comita os dados no arquivo
        editor.commit();
    }

    /**
     * Limpa todas as informações referentes ao usuario
     */
    public void limparDados(){
        editor.putString(NOME_USU_LOGADO,null);
        editor.putString(EMAIL_USU_LOGADO,null);
        editor.putString(SENHA_USU_LOGADO,null);
        editor.putString(TIPO_USU_LOGADO,null);
        editor.putString(UID_USU_LOGADO,null);
        editor.putString(KEY_USU_LOGADO,null);
        editor.commit();
    }

    public String getNomeUsu(){
        return preferences.getString(NOME_USU_LOGADO,null);
    }
    //Retorna o email do usuário logado
    public String getEmailUsu(){
        return preferences.getString(EMAIL_USU_LOGADO,null);
    }
    public String getSenhaUsu(){
        return preferences.getString(SENHA_USU_LOGADO,null);
    }
    //Retorna o tipo do usuário logado
    public String getTipoUsu(){
        return preferences.getString(TIPO_USU_LOGADO,null);
    }
    //Retorna o UID do usuário logado
    public String getUidUsu(){
        return preferences.getString(UID_USU_LOGADO,null);
    }
    //Retorna o UID do usuário logado
    public String getKeyUsu(){
        return preferences.getString(KEY_USU_LOGADO,null);
    }

    public void setSenhaUsu(String senha){
        editor.putString(SENHA_USU_LOGADO,senha);
        editor.commit();
    }

    public void setNomeUsu(String nome){
        editor.putString(NOME_USU_LOGADO,nome);
        editor.commit();
    }

}


