package br.com.flordemandacaru.model;

import java.math.BigDecimal;
import java.util.Objects;

public class ItemPedido {
    private Long id;
    private final Produto produto;
    private final int quantidade;
    private final BigDecimal precoUnitario, subtotal;
    private final String descricaoPersonalizacao;

    public ItemPedido(Produto produto, int quantidade, BigDecimal precoUnitario, String descricaoPersonalizacao) {
        this.produto = Objects.requireNonNull(produto, "produto obrigatório");
        if (quantidade <= 0)
            throw new IllegalArgumentException("quantidade deve ser positiva");
        if (precoUnitario == null || precoUnitario.signum() < 0)
            throw new IllegalArgumentException("preço unitário inválido");
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.descricaoPersonalizacao = descricaoPersonalizacao == null ? "" : descricaoPersonalizacao;
        this.subtotal = calcularSubtotal();
    }

    public static ItemPedido copiarDe(ItemCarrinho item) {
        String desc = "";
        if (item.getPersonalizacao() != null) {
            var p = item.getPersonalizacao();
            StringBuilder s = new StringBuilder();
            for (var planta : p.getItens()) {
                if (s.length() > 0)
                    s.append("; ");
                s.append(planta.getQuantidade()).append("x ").append(planta.getPlanta().getNome());
            }
            if (p.getPapel() != null)
                s.append("; papel ").append(p.getPapel().getNome());
            if (p.getLaco() != null)
                s.append("; laço ").append(p.getLaco().getMaterial()).append(" ").append(p.getLaco().getCor());
            if (p.getVaso() != null)
                s.append("; vaso ").append(p.getVaso().getNome());
            desc = s.toString();
        }
        BigDecimal unit = item.getPrecoUnitario().add(
                item.getPersonalizacao() == null ? BigDecimal.ZERO : item.getPersonalizacao().calcularValorAdicional());
        return new ItemPedido(item.getProduto(), item.getQuantidade(), unit, desc);
    }

    public BigDecimal calcularSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
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

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public String getDescricaoPersonalizacao() {
        return descricaoPersonalizacao;
    }
}
