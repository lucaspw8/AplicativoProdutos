package com.lucaspw8.gproductfirebase.DAO;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by lucas on 16/04/2018.
 */

public class ConfiguracaoFirebase {
    private static FirebaseDatabase firebaseDatabase;
    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth autenticacao;
    private static FirebaseStorage storage;
    private static StorageReference storageReference;

    public static DatabaseReference getFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        if(referenciaFirebase == null){
            referenciaFirebase = firebaseDatabase.getReference();
        }

        return referenciaFirebase;
    }

    public static FirebaseAuth getFirebaseAuth(){
        if(autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }

        return  autenticacao;
    }

    public static FirebaseStorage getFirebaseStorage(){
        if(storage == null){
            storage = FirebaseStorage.getInstance();
        }

        return storage;
    }

    public static StorageReference getStorageReference(){
        if(storageReference == null){
            storageReference = FirebaseStorage.getInstance().getReference();
        }
        return storageReference;
    }
}
