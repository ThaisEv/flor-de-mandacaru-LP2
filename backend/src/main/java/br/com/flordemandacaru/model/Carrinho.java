package br.com.flordemandacaru.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Carrinho {
    private Long id;
    private final Cliente cliente;
    private final LocalDateTime dataCriacao = LocalDateTime.now();
    private final List<ItemCarrinho> itens = new ArrayList<>();

    public Carrinho(Cliente cliente) {
        this.cliente = Objects.requireNonNull(cliente, "cliente obrigatório");
        cliente.definirCarrinho(this);
    }

    public ItemCarrinho adicionarItem(Produto produto, int quantidade) {
        return adicionarItem(produto, quantidade, null);
    }

    public ItemCarrinho adicionarItem(Produto produto, int quantidade, Personalizacao personalizacao) {
        produto.validarDisponibilidade(quantidade);
        if (personalizacao != null)
            personalizacao.validarPara(produto);
        ItemCarrinho item = new ItemCarrinho(produto, quantidade, produto.getPreco());
        item.definirPersonalizacao(personalizacao);
        itens.add(item);
        return item;
    }

    public void removerItem(ItemCarrinho item) {
        if (!itens.remove(item))
            throw new IllegalArgumentException("item não pertence ao carrinho");
    }

    public void atualizarQuantidade(ItemCarrinho item, int quantidade) {
        if (!itens.contains(item))
            throw new IllegalArgumentException("item não pertence ao carrinho");
        item.atualizarQuantidade(quantidade);
    }

    public BigDecimal calcularTotal() {
        return itens.stream().map(ItemCarrinho::calcularSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void limpar() {
        itens.clear();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public List<ItemCarrinho> getItens() {
        return List.copyOf(itens);
    }
}
