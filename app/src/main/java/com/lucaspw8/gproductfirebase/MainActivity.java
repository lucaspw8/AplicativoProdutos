package com.lucaspw8.gproductfirebase;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucaspw8.gproductfirebase.Classes.Empresa;
import com.lucaspw8.gproductfirebase.Classes.Usuario;
import com.lucaspw8.gproductfirebase.DAO.ConfiguracaoFirebase;
import com.lucaspw8.gproductfirebase.Helper.EmpresaPreferencias;
import com.lucaspw8.gproductfirebase.Helper.UsuarioPreferencias;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference referenceFirebase;

    private BootstrapEditText email;
    private BootstrapEditText senha;
    private BootstrapButton btnLogin;
    private TextView txtnovaConta;
    private TextView txtrecuperarSenha;

    private Usuario usuario;
    private ProgressDialog progressDialog;
    private AlertDialog alerta;

    private ActionBar actionBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.edtEmailLogin);
        senha =  findViewById(R.id.edtSenhaLogin);
        btnLogin =  findViewById(R.id.btnLogin);
        txtnovaConta = findViewById(R.id.txtCrieConta);
        txtrecuperarSenha = findViewById(R.id.txtRecuperarSenha);
        usuario = new Usuario();
        referenceFirebase = FirebaseDatabase.getInstance().getReference();


            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!email.getText().toString().equals("") && !senha.getText().toString().equals("")) {

                        usuario.setEmail(email.getText().toString());
                        usuario.setSenha(senha.getText().toString());
                        progressDialog = ProgressDialog.show(MainActivity.this, "Aguarde.",
                                "Entrando no sistema..!", true);
                        validarlogin();
                    } else {
                        Toast.makeText(MainActivity.this, "Preencha os campos", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        //Chamar tela de cadastro de usuario
        txtnovaConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CadastroUsuario.class);
                startActivity(intent);
            }
        });
        //Recuperar senha
        txtrecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             abrirDialogRecuperarSenha();
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

    @Override
    protected void onStart() {
        super.onStart();
        if(usuarioLogado()){
            progressDialog = ProgressDialog.show(this, "Aguarde.",
                    "Entrando no sistema..!", true);
            tipoUsuario();
        }
    }

    /**
     * Valida o login do usuario no FirebaseAuth
     */
    private void validarlogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    tipoUsuario();
                    Toast.makeText(MainActivity.this,"Login feito com sucesso!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"Usuário ou senha invalidos", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    /**
     * Informa se o usuario ja está logado
     * @return Boolean
     */
    public Boolean usuarioLogado(){

        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            return true;
        }else{
            return false;
        }
    }


    /**
     * Responsavel por salvar as informações do usuario e empresa para em
     * seguida abrir a tela correspondente
     */

    private void tipoUsuario(){

        //Recebendo email do usuario logado no momento
        usuario.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        //Buscando usuario cujo email é igual o email do usuario logado no momento
        referenceFirebase.child("usuarios").orderByChild("email").equalTo(usuario.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    usuario.setTipoUsuario(postSnapshot.child("tipoUsuario").getValue().toString());
                    usuario.setNome(postSnapshot.child("nome").getValue().toString());
                    usuario.setSenha(senha.getText().toString());
                    usuario.setUidUsuario(postSnapshot.child("uidUsuario").getValue().toString());
                    usuario.setKeyUsuario(postSnapshot.child("keyUsuario").getValue().toString());
                    //Salvando nas usuarioPreferencias de usuario
                    UsuarioPreferencias usuarioPreferencias = new UsuarioPreferencias(MainActivity.this);
                    usuarioPreferencias.salvarUsu(usuario);

                    if (usuario.getTipoUsuario().equals("VENDEDOR")) {
                                            //Tipo Vendedor
                        //Buscando empresa do Vendedor
                        referenceFirebase.child("empresa").orderByChild("uidUsuario").equalTo(usuario.getUidUsuario()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Verifica se existe alguma empresa
                                if (dataSnapshot.hasChildren()) {
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        //Salvando os dados em um objeto Empresa
                                        Empresa empresa =  new Empresa();
                                        empresa.setNome(postSnapshot.child("nome").getValue().toString());
                                        empresa.setTelefone(postSnapshot.child("telefone").getValue().toString());
                                        empresa.setNumero( postSnapshot.child("numero").getValue().toString());
                                        empresa.setRua(postSnapshot.child("rua").getValue().toString());
                                        empresa.setBairro(postSnapshot.child("bairro").getValue().toString());
                                        empresa.setComplemento(postSnapshot.child("complemento").getValue().toString());
                                        empresa.setUidUsuario(postSnapshot.child("uidUsuario").getValue().toString());
                                        empresa.setKeyEmpresa(postSnapshot.child("keyEmpresa").getValue().toString());
                                        //Salvando no SharedPreferencias de Empresa
                                        EmpresaPreferencias empresaPreferencias =  new EmpresaPreferencias(MainActivity.this);
                                        empresaPreferencias.salvarEmpresa(empresa);

                                        //Abrindo tela de empresa
                                        progressDialog.dismiss();
                                        Intent i = new Intent(MainActivity.this,MenuLateral.class);
                                        startActivity(i);
                                        finish();


                                    }
                                    //Não possui empresa entao abrira tela para cadastrar a empresa
                                } else{
                                    Intent intent = new Intent(MainActivity.this,CadastroEmpresaActivity.class);
                                    progressDialog.dismiss();
                                    startActivity(intent);
                                    finish();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        //Se usuario for consumidor abre tela referente a consumidor
                    } else if (usuario.getTipoUsuario().equals("CONSUMIDOR")) {
                        //Tipo Consumidor
                        Intent i = new Intent(MainActivity.this,MenuLateral.class);
                        startActivity(i);
                        finish();
                        }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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

    private void abrirDialogRecuperarSenha(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.alert_recuperar_senha);
        final BootstrapButton btResetar = dialog.findViewById(R.id.btnRecuperarSenha);
        final BootstrapButton btCancelarRecuperarSenha = dialog.findViewById(R.id.btnCancelarRecuperarSenha);
        final BootstrapEditText emailRecuperar = dialog.findViewById(R.id.emailRecuperar);

        //Ação do botao de comfirmar exclusão
        btResetar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                recuperarSenha(emailRecuperar.getText().toString());
                dialog.dismiss();
            }
        });

        btCancelarRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void recuperarSenha(String email){
        if(!email.equals("")){
                    //recuperando instancia da autenticação
                    autenticacao = FirebaseAuth.getInstance();
                    autenticacao.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this,"Em instantes você receberá um email",
                                        Toast.LENGTH_LONG).show();


                            }else{
                                Toast.makeText(MainActivity.this,"Falha ao resetar senha",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }else{
            Toast.makeText(MainActivity.this,"Preencha o campo email",
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}


