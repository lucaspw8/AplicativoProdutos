package com.lucaspw8.gproductfirebase;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.lucaspw8.gproductfirebase.Helper.Preferencias;

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

        final EditText edtEmailSenha =  new EditText(MainActivity.this);
        edtEmailSenha.setHint("exemplo@exemplo.com");

        if(usuarioLogado()){
            progressDialog = ProgressDialog.show(this, "Aguarde.",
                    "Entrando no sistema..!", true);
           tipoUsuario();
        }else {
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
        }

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
                final AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Recuperar senha");
                builder.setMessage("Informe seu email");
                builder.setView(edtEmailSenha);

                if(!edtEmailSenha.getText().equals("")){
                    builder.setPositiveButton("Recuperar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //recuperando instancia da autenticação
                            autenticacao = FirebaseAuth.getInstance();
                            String emailRecuperar = edtEmailSenha.getText().toString();

                            autenticacao.sendPasswordResetEmail(emailRecuperar).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this,"Em instantes você receberá um email",
                                                Toast.LENGTH_LONG).show();

                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(MainActivity.this,"Falha ao resetar senha",
                                                Toast.LENGTH_LONG).show();

                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    }
                                }
                            });


                        }
                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this,"Preencha o campo email",
                            Toast.LENGTH_LONG).show();
                }

                alerta = builder.create();
                alerta.show();
            }
        });

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
        referenceFirebase.child("usuarios").orderByChild("email").equalTo(usuario.getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    usuario.setTipoUsuario(postSnapshot.child("tipoUsuario").getValue().toString());
                    usuario.setNome(postSnapshot.child("nome").getValue().toString());
                    //Salvando nas preferencias de usuario
                    Preferencias preferencias = new Preferencias(MainActivity.this);
                    preferencias.salvarUsu(usuario);

                    if (usuario.getTipoUsuario().equals("VENDEDOR")) {
                                            //Tipo Vendedor
                        //Buscando empresa do Vendedor
                        referenceFirebase.child("empresa").orderByChild("emailDono").equalTo(usuario.getEmail()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Verifica se existe alguma empresa
                                if (dataSnapshot.hasChildren()) {
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                        //Salvando os dados em um objeto Empresa
                                        Empresa empresa =  new Empresa();
                                        empresa.setNome(postSnapshot.child("nome").getValue().toString());
                                        empresa.setTelefone(Integer.parseInt(postSnapshot.child("telefone").getValue().toString()));
                                        empresa.setNumero(Integer.parseInt( postSnapshot.child("numero").getValue().toString()));
                                        empresa.setRua(postSnapshot.child("rua").getValue().toString());
                                        empresa.setBairro(postSnapshot.child("bairro").getValue().toString());
                                        empresa.setComplemento(postSnapshot.child("complemento").getValue().toString());
                                        empresa.setEmailDono(postSnapshot.child("emailDono").getValue().toString());
                                        //Salvando no SharedPreferencias de Empresa
                                        EmpresaPreferencias empresaPreferencias =  new EmpresaPreferencias(MainActivity.this);
                                        empresaPreferencias.salvarEmpresa(empresa);

                                        //Abrindo tela de empresa
                                        Intent intent = new Intent(MainActivity.this,EmpresaPrincipalActivity.class);
                                        progressDialog.dismiss();
                                        startActivity(intent);
                                        finish();


                                    }
                                    //Não possui empresa
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


                    } else if (usuario.getTipoUsuario().equals("CONSUMIDOR")) {
                        //Tipo Consumidor
                        Intent intent = new Intent(MainActivity.this,ListarProdutos.class);
                        finish();
                        startActivity(intent);
                        }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}


