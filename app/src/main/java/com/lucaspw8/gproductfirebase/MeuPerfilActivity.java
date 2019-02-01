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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lucaspw8.gproductfirebase.Classes.Empresa;
import com.lucaspw8.gproductfirebase.Classes.Produto;
import com.lucaspw8.gproductfirebase.Classes.Usuario;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;
import com.lucaspw8.gproductfirebase.Helper.EmpresaPreferencias;
import com.lucaspw8.gproductfirebase.Helper.UsuarioPreferencias;

public class MeuPerfilActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private LinearLayout layoutEmpresa;

    private TextView txtNomeUsu;
    private TextView txtEmailUsu;
    private TextView txtTipoUsu;

    private TextView txtNomeEmpresa;
    private TextView txtTelefoneEmpresa;
    private TextView txtEnderecoEmpresa;
    private TextView txtComplementoEmpresa;

    private BootstrapButton btEditarConta;

    private BootstrapButton btExcluirConta;

    private BootstrapButton btEditarEmpresa;
    private BootstrapButton btExcluirEmpresa;

    private DatabaseReference databaseReference;
    private FirebaseAuth autenticacao;
    private StorageReference storageReference;

    //Shared Preferences
    private UsuarioPreferencias usuPref;
    private EmpresaPreferencias empresaPreferencias;

    Empresa empresa;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_perfil);

        layoutEmpresa = findViewById(R.id.lEmpresaPerfil);

        databaseReference = ConfiguracaoFirebase.getFirebase();
        autenticacao = FirebaseAuth.getInstance();
        storageReference = ConfiguracaoFirebase.getStorageReference();

        btEditarConta = findViewById(R.id.btnEditarPerfil);
        btEditarEmpresa = findViewById(R.id.btnEditarEmpresaPerfil);
        btExcluirConta = findViewById(R.id.btnExcluirPerfil);
        btExcluirEmpresa = findViewById(R.id.btnExcluirEmpresaPerfil);

        txtNomeUsu = findViewById(R.id.txtNomeUsuPerfil);
        txtEmailUsu = findViewById(R.id.txtEmailUsuPerfil);
        txtTipoUsu = findViewById(R.id.txtTipoUsuPerfil);

        txtNomeEmpresa = findViewById(R.id.txtNomeEmpresaPerfil);
        txtTelefoneEmpresa = findViewById(R.id.txtTelefoneEmpresaPerfil);
        txtEnderecoEmpresa = findViewById(R.id.txtEnderecoEmpresaPerfil);
        txtComplementoEmpresa = findViewById(R.id.txtComplementoEmpresaPerfil);





        btEditarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeuPerfilActivity.this,EditarPerfil.class);
                startActivity(intent);
            }
        });

        btExcluirConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogExclusaoConta();
            }
        });

        btEditarEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeuPerfilActivity.this,EditarEmpresa.class);
                startActivity(intent);
            }
        });

        btExcluirEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogExclusaoEmpresa();
            }
        });

        try {
            actionBar = getSupportActionBar();
            //Adiciona o botao de voltar no action bar
            actionBar.setDisplayHomeAsUpEnabled(true); //Mostrar o botão
            actionBar.setTitle("Meu perfil");
            //getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
            //getSupportActionBar().setTitle("Seu titulo aqui");
        } catch (NullPointerException e) {
            Log.w("Erro", e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //instanciando as preferencias
        usuPref = new UsuarioPreferencias(this);
        empresaPreferencias = new EmpresaPreferencias(this);
        empresa = empresaPreferencias.getEmpresa();

        txtNomeUsu.setText(usuPref.getNomeUsu());
        txtEmailUsu.setText(usuPref.getEmailUsu());
        txtTipoUsu.setText(usuPref.getTipoUsu());
        if (empresaPreferencias.getEmpresa().getNome() != null) {
            layoutEmpresa.setVisibility(View.VISIBLE);
            txtNomeEmpresa.setText(empresa.getNome());
            txtTelefoneEmpresa.setText(empresa.getTelefone());
            txtEnderecoEmpresa.setText(empresa.getRua() + " " + empresa.getNumero() + " " + empresa.getBairro());
            txtComplementoEmpresa.setText(empresa.getComplemento());
        } else {
            layoutEmpresa.setVisibility(View.GONE);
        }
    }

    /**
     * Responsavel por excluir todos os dados do usuario
     */
    private void excluirUsuarioDeslogar() {

        if(empresaPreferencias.getEmpresa().getNome()!= null){
            excluirEmpresa();
        }
        final FirebaseUser user;
        user = autenticacao.getCurrentUser();
        Log.d("Luk", usuPref.getEmailUsu()+""+" "+usuPref.getSenhaUsu());
        final AuthCredential credential = EmailAuthProvider.getCredential(usuPref.getEmailUsu(),usuPref.getSenhaUsu());


        databaseReference = ConfiguracaoFirebase.getFirebase();
        databaseReference.child("usuarios").orderByChild("uidUsuario").equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    final Usuario usuario = postSnapshot.getValue(Usuario.class);
                    user.reauthenticate(credential);

                    user. delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("USUARIO_EXCLUIDO", "User Deleted");

                                        Toast.makeText(MeuPerfilActivity.this, "O usuário foi excluido!", Toast.LENGTH_LONG).show();

                                        databaseReference = ConfiguracaoFirebase.getFirebase();
                                        databaseReference.child("usuarios").child(usuario.getKeyUsuario()).removeValue();


                                        autenticacao.signOut();
                                        limparPrefEmpresa();
                                        limparPrefUsuario();
                                        finish();

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

    /**
     * Exclui a empresa e chama o excluir seus produtos
     */
    private void excluirEmpresa() {
        try {
            excluirProdutos();
                databaseReference = ConfiguracaoFirebase.getFirebase();
                databaseReference.child("empresa").child(empresaPreferencias.getEmpresa().getKeyEmpresa()).removeValue();
                limparPrefEmpresa();
                finish();

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao deletar empresa " + e.getMessage(), Toast.LENGTH_LONG).show();

        }

    }

    /**
     * Deleta a pasta com as fotos dos produtos e os dados do banco
     *
     * @return boolean
     */
    private void excluirProdutos() {
        try {
            //Buscando todos os produtos relacionados
            databaseReference.child("produto").orderByChild("uidUsuario").equalTo(usuPref.getUidUsu()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Produto produto = postSnapshot.getValue(Produto.class);

                        //Apaga imagem do produto
                        if(!produto.getImagemUrl().equals("")) {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            storage.getReferenceFromUrl(produto.getImagemUrl()).delete();
                        }
                        //Remove produto
                        databaseReference.child("produto").child(produto.getKeyProduto()).removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao apagar produtos", Toast.LENGTH_LONG).show();

        }
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

    /**
     * Exibe caixa de dialogo para confirmar exclusao da conta
     */
    private void abrirDialogExclusaoConta() {
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

    /**
     * Exibe caixa de dialogo para confirmar exclusao da empresa
     */
    private void abrirDialogExclusaoEmpresa() {
        final Dialog dialog = new Dialog(MeuPerfilActivity.this);
        dialog.setContentView(R.layout.alert_excluir_empresa);
        final BootstrapButton btConfirmaExcluir = dialog.findViewById(R.id.btnConfirmaExcluirEmpresa);
        final BootstrapButton btNegaExcluir = dialog.findViewById(R.id.btnNegaExcluirEmpresa);

        //Ação do botao de comfirmar exclusão
        btConfirmaExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                excluirEmpresa();
                    Toast.makeText(MeuPerfilActivity.this, "Empresa excluida com sucesso!", Toast.LENGTH_LONG).show();
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

    private void limparPrefEmpresa() {
        empresaPreferencias.limparDados();
    }

    private void limparPrefUsuario() {
        usuPref.limparDados();
    }
}
