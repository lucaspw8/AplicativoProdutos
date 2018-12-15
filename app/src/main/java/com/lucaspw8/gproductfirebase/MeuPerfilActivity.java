package com.lucaspw8.gproductfirebase;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucaspw8.gproductfirebase.Classes.Usuario;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;
import com.lucaspw8.gproductfirebase.Helper.Preferencias;

public class MeuPerfilActivity extends AppCompatActivity {
    private ActionBar actionBar;

    private TextView txtNomeUsu;
    private TextView txtEmailUsu;
    private TextView txtTipoUsu;

    private BootstrapButton btEditar;
    private BootstrapButton btExcluirConta;

    private DatabaseReference databaseReference;
    private FirebaseAuth autenticacao;

    private Preferencias usuPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_perfil);

        databaseReference = ConfiguracaoFirebase.getFirebase();
        autenticacao = FirebaseAuth.getInstance();

        btEditar = findViewById(R.id.btnEditarPerfil);
        btExcluirConta =  findViewById(R.id.btnExcluirPerfil);
        txtNomeUsu = findViewById(R.id.txtNomeUsuPerfil);
        txtEmailUsu = findViewById(R.id.txtEmailUsuPerfil);
        txtTipoUsu = findViewById(R.id.txtTipoUsuPerfil);




        usuPref = new Preferencias(this);
        txtNomeUsu.setText(usuPref.getNOME_USU_LOGADO());
        txtEmailUsu.setText(usuPref.getEmailUsu());
        txtTipoUsu.setText(usuPref.getTipoUsu());
        /*
        databaseReference.child("usuarios").orderByChild("email")
                .equalTo(usuPref.getEmailUsu()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Usuario usuario = postSnapshot.getValue(Usuario.class);

                    txtNomeUsu.setText(usuario.getNome());
                    txtEmailUsu.setText(usuario.getEmail());
                    txtTipoUsu.setText(usuario.getTipoUsuario());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        btExcluirConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               abrirDialogExclusao();
            }
        });

        try {
            actionBar = getSupportActionBar();
            //Adiciona o botao de voltar no action bar
            actionBar.setDisplayHomeAsUpEnabled(true); //Mostrar o botão
            //getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
            //getSupportActionBar().setTitle("Seu titulo aqui");
        }catch (NullPointerException e){
            Log.w("Erro",e.getMessage());
        }
    }

    private void excluirUsuarioDeslogar() {

        String emailUsuarioLogado = autenticacao.getCurrentUser().getEmail();

        databaseReference = ConfiguracaoFirebase.getFirebase();

        databaseReference.child("usuarios").orderByChild("email").equalTo(emailUsuarioLogado).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final Usuario usuario = postSnapshot.getValue(Usuario.class);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("USUARIO_EXCLUIDO", "User Deleted");

                                        Toast.makeText(MeuPerfilActivity.this, "O usuário foi excluido!", Toast.LENGTH_LONG).show();

                                        databaseReference = ConfiguracaoFirebase.getFirebase();
                                        databaseReference.child("usuarios").child(usuario.getKeyUsuario()).removeValue();

                                        autenticacao.signOut();

                                        telaPrincipal();

                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void telaPrincipal(){
        Intent intent = new Intent(MeuPerfilActivity.this,MenuLateral.class);
        usuPref.limparDados();
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirDialogExclusao(){
        final Dialog dialog = new Dialog(MeuPerfilActivity.this);
        dialog.setContentView(R.layout.alert_personalizado_excluir);
        final BootstrapButton btConfirmaExcluir = dialog.findViewById(R.id.btnConfirmaExcluirUsu);
        final BootstrapButton btNegaExcluir = dialog.findViewById(R.id.btnNegaExcluirUsu);

        //Ação do botao de comfirmar exclusão
        btConfirmaExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excluirUsuarioDeslogar();
                dialog.dismiss();
            }
        });

        //Ação do botao de negar exclusão
        btNegaExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dialog.dismiss();
            }
        });

        dialog.show();
    }
}
