package br.com.flordemandacaru.model;

import java.util.Objects;

public class ItemComposicao {
    private Long id;
    private final Produto produto;
    private int quantidade;

    public ItemComposicao(Produto produto, int quantidade) {
        this.produto = Objects.requireNonNull(produto, "produto obrigatório");
        atualizarQuantidade(quantidade);
    }

    public void atualizarQuantidade(int v) {
        if (v <= 0)
            throw new IllegalArgumentException("quantidade deve ser positiva");
        quantidade = v;
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
}
