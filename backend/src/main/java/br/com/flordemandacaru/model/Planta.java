package br.com.flordemandacaru.model;

import java.math.BigDecimal;

public class Planta extends Produto {
    private String nomeCientifico, necessidadeAgua, luminosidade, cuidados, tipoPlanta;
    private boolean possuiFlor;
    private String corFlor, epocaFloracao, tipoFolhagem;

    public Planta(String nome, String descricao, BigDecimal preco, int estoque, String nomeCientifico,
            String necessidadeAgua, String luminosidade, String cuidados, String tipoPlanta,
            boolean possuiFlor, String corFlor, String epocaFloracao, String tipoFolhagem) {
        super(nome, descricao, preco, estoque);
        this.nomeCientifico = nomeCientifico;
        this.necessidadeAgua = necessidadeAgua;
        this.luminosidade = luminosidade;
        this.cuidados = cuidados;
        this.tipoPlanta = tipoPlanta;
        this.possuiFlor = possuiFlor;
        this.corFlor = possuiFlor ? corFlor : null;
        this.epocaFloracao = possuiFlor ? epocaFloracao : null;
        this.tipoFolhagem = tipoFolhagem;
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

    public void setPossuiFlor(boolean v) {
        possuiFlor = v;
        if (!v) {
            corFlor = null;
            epocaFloracao = null;
        }
    }

    public String getCorFlor() {
        return corFlor;
    }

    public void setCorFlor(String v) {
        if (possuiFlor)
            corFlor = v;
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
