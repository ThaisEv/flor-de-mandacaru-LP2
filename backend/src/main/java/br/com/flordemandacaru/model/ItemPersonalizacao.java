package br.com.flordemandacaru.model;

import java.math.BigDecimal;
import java.util.Objects;

public class ItemPersonalizacao {
    private Long id;
    private final Planta planta;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemPersonalizacao(Planta planta, int quantidade, BigDecimal precoUnitario) {
        this.planta = Objects.requireNonNull(planta, "planta obrigatória");
        atualizarQuantidade(quantidade);
        atualizarPrecoUnitario(precoUnitario);
    }

    public BigDecimal calcularSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public void atualizarQuantidade(int v) {
        if (v <= 0)
            throw new IllegalArgumentException("quantidade deve ser positiva");
        planta.validarDisponibilidade(v);
        quantidade = v;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public Planta getPlanta() {
        return planta;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void atualizarPrecoUnitario(BigDecimal v) {
        if (v == null || v.signum() < 0)
            throw new IllegalArgumentException("preço unitário inválido");
        precoUnitario = v;
    }
}
