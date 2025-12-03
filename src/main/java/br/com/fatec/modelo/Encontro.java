package br.com.fatec.modelo;

import java.time.LocalDate;
import java.util.List;

public class Encontro {

    private int id;
    private LocalDate dataEncontro;
    private boolean excluidoLogico;

    private List<ServicoEncontro> servicos;

    public Encontro(int id, LocalDate dataEncontro, boolean excluidoLogico) {
        this.id = id;
        this.dataEncontro = dataEncontro;
        this.excluidoLogico = excluidoLogico;
    }

    public Encontro(LocalDate dataEncontro, boolean excluidoLogico) {
        this.dataEncontro = dataEncontro;
        this.excluidoLogico = excluidoLogico;
    }

    public Encontro(LocalDate dataEncontro) {
        this.dataEncontro = dataEncontro;
        this.excluidoLogico = false; // Valor padrão: Encontro ATIVO
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDataEncontro() {
        return dataEncontro;
    }

    public void setDataEncontro(LocalDate dataEncontro) {
        this.dataEncontro = dataEncontro;
    }

    public boolean isExcluidoLogico() {
        return excluidoLogico;
    }

    public void setExcluidoLogico(boolean excluidoLogico) {
        this.excluidoLogico = excluidoLogico;
    }

    public String getStatus() {
        return this.excluidoLogico ? "CANCELADO" : "ATIVO";
    }

    public List<ServicoEncontro> getServicos() {
        return servicos;
    }

    public void setServicos(List<ServicoEncontro> servicos) {
        this.servicos = servicos;
    }
}