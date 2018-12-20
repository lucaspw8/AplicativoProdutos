package com.lucaspw8.gproductfirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

public class EditarPerfil extends AppCompatActivity {

    private BootstrapEditText editarNomeUsu;
    private BootstrapEditText editarSenhaUsu;
    private BootstrapEditText editarSenhaVerificaUsu;
    private BootstrapButton btnSalvar;
    private BootstrapButton btnCancelar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        editarNomeUsu = findViewById(R.id.edtEditarNomeUsu);
        editarSenhaUsu = findViewById(R.id.edtEditarSenhaUsu);
        editarSenhaVerificaUsu = findViewById(R.id.edtEditarRepetirSenhaUsu);
    }
}
