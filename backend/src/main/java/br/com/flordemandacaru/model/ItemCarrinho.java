package br.com.flordemandacaru.model;

import java.math.BigDecimal;
import java.util.Objects;

public class ItemCarrinho {
    private Long id;
    private final Produto produto;
    private int quantidade;
    private BigDecimal precoUnitario;
    private Personalizacao personalizacao;

    public ItemCarrinho(Produto produto, int quantidade, BigDecimal precoUnitario) {
        this.produto = Objects.requireNonNull(produto, "produto obrigatório");
        atualizarQuantidade(quantidade);
        atualizarPrecoUnitario(precoUnitario);
    }

    public BigDecimal calcularSubtotal() {
        BigDecimal unit = precoUnitario
                .add(personalizacao == null ? BigDecimal.ZERO : personalizacao.calcularValorAdicional());
        return unit.multiply(BigDecimal.valueOf(quantidade));
    }

    public void atualizarQuantidade(int v) {
        produto.validarDisponibilidade(v);
        if (personalizacao != null)
            personalizacao.validarPara(produto);
        quantidade = v;
    }

    public void definirPersonalizacao(Personalizacao p) {
        if (p != null)
            p.validarPara(produto);
        personalizacao = p;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int v) {
        atualizarQuantidade(v);
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal v) {
        atualizarPrecoUnitario(v);
    }

    public Personalizacao getPersonalizacao() {
        return personalizacao;
    }

    private void atualizarPrecoUnitario(BigDecimal v) {
        if (v == null || v.signum() < 0)
            throw new IllegalArgumentException("preço unitário inválido");
        precoUnitario = v;
    }
}
