package com.developer.marcocicala.centrocampania;

/**
 * Created by utente on 09/01/2017.
 */

public class Negozi {
    private int id;
    private String nome;

    public Negozi() {
    }

    public Negozi(int id, String nome) {
        this.nome = nome;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
