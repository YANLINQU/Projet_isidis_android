package com.example.yanlin.projet_isidis;

/**
 * Created by yanlin on 2018/1/24.
 * un objet singleton pour assurer qu'une classe ou unique dans l'application
 */

public class UserEntity {

    private static UserEntity instance = null;

    private int id;
    private String nom;

    private UserEntity() {
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id=id;
    }

    public String getNom(){
        return this.nom;
    }

    public void setNom(String nom){
        this.nom=nom;
    }

    public static UserEntity getEntity() {
        if (instance == null) {
            instance = new UserEntity();
        }
        return instance;
    }

    public String toString(){
        return "id:"+id+",nom:"+nom;
    }
}
