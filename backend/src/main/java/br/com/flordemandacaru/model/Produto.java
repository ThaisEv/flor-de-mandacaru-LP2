package br.com.flordemandacaru.model;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class Produto {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int quantidadeEstoque;
    private boolean ativo = true;

    protected Produto(String nome, String descricao, BigDecimal preco, int quantidadeEstoque) {
        this.nome = textoObrigatorio(nome, "nome");
        this.descricao = Objects.requireNonNull(descricao, "descrição não pode ser nula");
        atualizarPreco(preco);
        atualizarEstoque(quantidadeEstoque);
    }

    protected static String textoObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank())
            throw new IllegalArgumentException(campo + " é obrigatório");
        return valor.trim();
    }

    public void atualizarPreco(BigDecimal preco) {
        if (preco == null || preco.signum() < 0)
            throw new IllegalArgumentException("preço não pode ser nulo ou negativo");
        this.preco = preco;
    }

    public void atualizarEstoque(int quantidade) {
        if (quantidade < 0)
            throw new IllegalArgumentException("estoque não pode ser negativo");
        this.quantidadeEstoque = quantidade;
    }

    public void ativar() {
        ativo = true;
    }

    public void desativar() {
        ativo = false;
    }

    public void validarDisponibilidade(int quantidade) {
        if (quantidade <= 0)
            throw new IllegalArgumentException("quantidade deve ser positiva");
        if (!ativo || quantidadeEstoque < quantidade)
            throw new IllegalStateException("produto indisponível ou sem estoque suficiente: " + nome);
    }

    public void baixarEstoque(int quantidade) {
        validarDisponibilidade(quantidade);
        quantidadeEstoque -= quantidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id <= 0)
            throw new IllegalArgumentException("id deve ser positivo");
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = textoObrigatorio(nome, "nome");
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = Objects.requireNonNull(descricao, "descrição não pode ser nula");
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public boolean isAtivo() {
        return ativo;
    }
}
