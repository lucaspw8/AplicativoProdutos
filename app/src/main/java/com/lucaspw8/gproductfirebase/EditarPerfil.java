package com.lucaspw8.gproductfirebase;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.lucaspw8.gproductfirebase.Classes.Usuario;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;
import com.lucaspw8.gproductfirebase.Helper.UsuarioPreferencias;

public class EditarPerfil extends AppCompatActivity {
    private ActionBar actionBar;
    private BootstrapEditText txteditarNomeUsu;
    private BootstrapEditText txteditarSenhaUsu;
    private BootstrapEditText txteditarSenhaVerificaUsu;
    private BootstrapButton btnSalvar;
    private BootstrapButton btnCancelar;

    private DatabaseReference reference;
    private FirebaseAuth autenticacao;

    private UsuarioPreferencias usuPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        usuPref = new UsuarioPreferencias(EditarPerfil.this);

        txteditarNomeUsu = findViewById(R.id.edtEditarNomeUsu);
        txteditarSenhaUsu = findViewById(R.id.edtEditarSenhaUsu);
        txteditarSenhaVerificaUsu = findViewById(R.id.edtEditarRepetirSenhaUsu);

        btnSalvar = findViewById(R.id.btnEditarUsu);
        btnCancelar = findViewById(R.id.btnEditarUsuCancelar);


        txteditarNomeUsu.setText(usuPref.getNomeUsu());

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSalvar.setEnabled(false);
                Usuario usuario = new Usuario();
                if (!txteditarNomeUsu.getText().toString().equals("")) {
                    usuario.setNome(txteditarNomeUsu.getText().toString());
                    usuario.setKeyUsuario(usuPref.getKeyUsu());
                    usuario.setUidUsuario(usuPref.getUidUsu());
                    usuario.setEmail(usuPref.getEmailUsu());
                    usuario.setTipoUsuario(usuPref.getTipoUsu());
                    atualizarDados(usuario);
                }

                if (!txteditarSenhaUsu.getText().toString().equals("")) {
                    if (txteditarSenhaUsu.getText().toString().equals(txteditarSenhaVerificaUsu.getText().toString())) {
                        atualizarSenha(txteditarSenhaUsu.getText().toString());
                    } else {
                        Toast.makeText(EditarPerfil.this, "Senhas não correspondem", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {
            actionBar = getSupportActionBar();
            //Adiciona o botao de voltar no action bar
            actionBar.setDisplayHomeAsUpEnabled(true); //Mostrar o botão
            actionBar.setTitle("Editar conta");
            //getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
            //getSupportActionBar().setTitle("Seu titulo aqui");
        } catch (NullPointerException e) {
            Log.w("Erro", e.getMessage());
        }
    }

    private boolean atualizarDados(final Usuario usuario) {
        try {
            reference = ConfiguracaoFirebase.getFirebase().child("usuarios");
            reference.child(usuPref.getKeyUsu()).setValue(usuario)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                usuPref.setNomeUsu(usuario.getNome());
                                Toast.makeText(EditarPerfil.this, "Dados salvos!", Toast.LENGTH_LONG).show();
                                btnSalvar.setEnabled(true);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void atualizarSenha(final String senha) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final AuthCredential credential = EmailAuthProvider.getCredential(usuPref.getEmailUsu(), usuPref.getSenhaUsu());
        user.reauthenticate(credential);
        user.updatePassword(senha).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Nova Senha", "Senha atualizada");
                    Toast.makeText(EditarPerfil.this, "Senha atualizada com sucesso!", Toast.LENGTH_LONG).show();
                    usuPref.setSenhaUsu(senha);
                    btnSalvar.setEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
