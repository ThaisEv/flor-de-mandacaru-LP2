package br.com.flordemandacaru.model;

import java.math.BigDecimal;

public class Planta extends Produto {
    private String nomeCientifico, necessidadeAgua, luminosidade, cuidados, tipoPlanta;
    private boolean possuiFlor;
    private String epocaFloracao, tipoFolhagem;

    public Planta(String nome, String descricao, BigDecimal preco, int estoque, String nomeCientifico,
            String necessidadeAgua, String luminosidade, String cuidados, String tipoPlanta,
            boolean possuiFlor, String epocaFloracao, String tipoFolhagem) {
        super(nome, descricao, preco, estoque);
        this.nomeCientifico = nomeCientifico;
        this.necessidadeAgua = necessidadeAgua;
        this.luminosidade = luminosidade;
        this.cuidados = cuidados;
        this.tipoPlanta = tipoPlanta;
        this.possuiFlor = possuiFlor;
        this.epocaFloracao = possuiFlor ? epocaFloracao : null;
        this.tipoFolhagem = tipoFolhagem;
    }

    @Override
    protected String getPrefixoCodigo() {
        return "PLT";
    }

    public String getNomeCientifico() {
        return nomeCientifico;
    }

    public void setNomeCientifico(String v) {
        nomeCientifico = v;
    }

    public String getNecessidadeAgua() {
        return necessidadeAgua;
    }

    public void setNecessidadeAgua(String v) {
        necessidadeAgua = v;
    }

    public String getLuminosidade() {
        return luminosidade;
    }

    public void setLuminosidade(String v) {
        luminosidade = v;
    }

    public String getCuidados() {
        return cuidados;
    }

    public void setCuidados(String v) {
        cuidados = v;
    }

    public String getTipoPlanta() {
        return tipoPlanta;
    }

    public void setTipoPlanta(String v) {
        tipoPlanta = v;
    }

    public boolean isPossuiFlor() {
        return possuiFlor;
    }

    @Override
    public void adicionarCor(Cor cor) {
        if (!possuiFlor)
            throw new IllegalStateException("planta sem flor não pode ter cores de flor associadas");
        super.adicionarCor(cor);
    }

    public void setPossuiFlor(boolean v) {
        possuiFlor = v;
        if (!v) {
            removerTodasCores();
            epocaFloracao = null;
        }
    }

    public String getEpocaFloracao() {
        return epocaFloracao;
    }

    public void setEpocaFloracao(String v) {
        if (possuiFlor)
            epocaFloracao = v;
    }

    public String getTipoFolhagem() {
        return tipoFolhagem;
    }

    public void setTipoFolhagem(String v) {
        tipoFolhagem = v;
    }
}
