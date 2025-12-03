package br.com.fatec.modelo;

public class ServicoEncontro {

    private int id;
    private Encontro encontro;
    private ServicoFixo servicoFixo;
    private Mae maeResponsavel;
    private String descricaoAtividade;

    public ServicoEncontro(int id, Encontro encontro, ServicoFixo servicoFixo, Mae maeResponsavel, String descricaoAtividade) {
        this.id = id;
        this.encontro = encontro;
        this.servicoFixo = servicoFixo;
        this.maeResponsavel = maeResponsavel;
        this.descricaoAtividade = descricaoAtividade;
    }

    public ServicoEncontro(Encontro encontro, ServicoFixo servicoFixo, Mae maeResponsavel, String descricaoAtividade) {
        this.encontro = encontro;
        this.servicoFixo = servicoFixo;
        this.maeResponsavel = maeResponsavel;
        this.descricaoAtividade = descricaoAtividade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Encontro getEncontro() {
        return encontro;
    }

    public void setEncontro(Encontro encontro) {
        this.encontro = encontro;
    }

    public ServicoFixo getServicoFixo() {
        return servicoFixo;
    }

    public void setServicoFixo(ServicoFixo servicoFixo) {
        this.servicoFixo = servicoFixo;
    }

    public Mae getMaeResponsavel() {
        return maeResponsavel;
    }

    public void setMaeResponsavel(Mae maeResponsavel) {
        this.maeResponsavel = maeResponsavel;
    }

    public String getDescricaoAtividade() {
        return descricaoAtividade;
    }

    public void setDescricaoAtividade(String descricaoAtividade) {
        this.descricaoAtividade = descricaoAtividade;
    }
}