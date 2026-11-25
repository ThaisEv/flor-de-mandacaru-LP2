package br.com.flordemandacaru.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Papel {
    private Long id;
    private String nome, cor;
    private BigDecimal precoAdicional;
    private boolean ativo = true;

    public Papel(String nome, String cor, BigDecimal precoAdicional) {
        this.nome = Produto.textoObrigatorio(nome, "nome");
        this.cor = Produto.textoObrigatorio(cor, "cor");
        atualizarPrecoAdicional(precoAdicional);
    }

    public void atualizarPrecoAdicional(BigDecimal v) {
        if (v == null || v.signum() < 0)
            throw new IllegalArgumentException("preço adicional inválido");
        precoAdicional = v;
    }

    public void validarDisponibilidade() {
        if (!ativo)
            throw new IllegalStateException("papel indisponível");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String v) {
        nome = Produto.textoObrigatorio(v, "nome");
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String v) {
        cor = Produto.textoObrigatorio(v, "cor");
    }

    public BigDecimal getPrecoAdicional() {
        return precoAdicional;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean v) {
        ativo = v;
    }
}
