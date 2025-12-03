package br.com.fatec.modelo;

import java.time.LocalDate;

public class Mae {

    private int id;
    private String nome;
    private String telefone;
    private String endereco;
    private LocalDate dataAniversario;

    public Mae(int id, String nome, String telefone, String endereco, LocalDate dataAniversario) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.dataAniversario = dataAniversario;
    }

    public Mae(String nome, String telefone, String endereco, LocalDate dataAniversario) {
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.dataAniversario = dataAniversario;
    }

    public Mae(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.telefone = "";
        this.endereco = "";
        this.dataAniversario = null;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public LocalDate getDataAniversario() {
        return dataAniversario;
    }

    public void setDataAniversario(LocalDate dataAniversario) {
        this.dataAniversario = dataAniversario;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}