package com.lucaspw8.gproductfirebase;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment {

    private FirebaseAuth autenticacao;
    private DatabaseReference referenceFirebase;
    private BootstrapEditText email;
    private BootstrapEditText senha;
    private BootstrapButton btnLogin;
    private Usuario usuario;
    private TextView novaConta;
    private MenuLateral menuLateral;
    private ProgressDialog progressDialog;

    public Login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Obter a view do fragmento
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email = view.findViewById(R.id.edtEmailLogin2);
        senha =  view.findViewById(R.id.edtSenhaLogin2);
        btnLogin =  view.findViewById(R.id.btnLogin2);
        novaConta = view.findViewById(R.id.txtCrieConta2);
        usuario = new Usuario();
        menuLateral = new MenuLateral();
        referenceFirebase = FirebaseDatabase.getInstance().getReference();


        if(usuarioLogado()){
            progressDialog = ProgressDialog.show(getActivity(), "Aguarde.",
                    "Entrando no sistema..!", true);
            tipoUsuario();
        }else {
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!email.getText().toString().equals("") && !senha.getText().toString().equals("")) {

                        usuario.setEmail(email.getText().toString());
                        usuario.setSenha(senha.getText().toString());
                        progressDialog = ProgressDialog.show(getActivity(), "Aguarde.",
                                "Entrando no sistema..!", true);
                        validarlogin();
                    } else {
                        Toast.makeText(getActivity(), "Preencha os campos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //Chamar tela de cadastro de usuario
        novaConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),CadastroUsuario.class);
                startActivity(intent);
            }
        });


        // Inflate the layout for this fragment
        return view;
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
                    Toast.makeText(getActivity().getApplicationContext(),"Login feito com sucesso!", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(),"Usuário ou senha invalidos", Toast.LENGTH_SHORT).show();
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
                    Preferencias preferencias = new Preferencias(getContext());
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
                                        EmpresaPreferencias empresaPreferencias =  new EmpresaPreferencias(getContext());
                                        empresaPreferencias.salvarEmpresa(empresa);

                                        //Abrindo tela de empresa

                                        //Intent intent = new Intent(getActivity().getApplicationContext(),EmpresaPrincipalActivity.class);
                                        //startActivity(intent);

                                        progressDialog.dismiss();
                                        //menuLateral.abrirEmpresaPrincipalActivity();

                                    }
                                    //Não possui empresa
                                } else{
                                    Intent intent = new Intent(getActivity().getApplicationContext(),CadastroEmpresaActivity.class);
                                    progressDialog.dismiss();
                                    startActivity(intent);

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });


                    } else if (usuario.getTipoUsuario().equals("CONSUMIDOR")) {
                        //Tipo Consumidor
                        Intent intent = new Intent(getActivity().getApplicationContext(),ListarProdutos.class);
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
