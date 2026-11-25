package br.com.flordemandacaru.model;

import java.math.BigDecimal;

public class Laco {
    private Long id;
    private String material, cor;
    private BigDecimal precoAdicional;
    private boolean ativo = true;

    public Laco(String material, String cor, BigDecimal precoAdicional) {
        this.material = Produto.textoObrigatorio(material, "material");
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
            throw new IllegalStateException("laço indisponível");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String v) {
        material = Produto.textoObrigatorio(v, "material");
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
