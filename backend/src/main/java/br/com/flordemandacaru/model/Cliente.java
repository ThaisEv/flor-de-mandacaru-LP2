package br.com.flordemandacaru.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cliente {
    private Long id;
    private String nome, email, senhaHash, telefone;
    private final List<Endereco> enderecos = new ArrayList<>();
    private Carrinho carrinho;

    public Cliente(String nome, String email, String senhaHash, String telefone) {
        this.nome = Produto.textoObrigatorio(nome, "nome");
        atualizarEmail(email);
        this.senhaHash = Produto.textoObrigatorio(senhaHash, "hash da senha");
        this.telefone = Produto.textoObrigatorio(telefone, "telefone");
    }

    public void cadastrar() {
        if (nome.isBlank() || email.isBlank() || senhaHash.isBlank())
            throw new IllegalStateException("dados obrigatórios ausentes");
    }

    public void atualizarPerfil(String nome, String email, String telefone) {
        this.nome = Produto.textoObrigatorio(nome, "nome");
        atualizarEmail(email);
        this.telefone = Produto.textoObrigatorio(telefone, "telefone");
    }

    public void alterarSenha(String senhaHash) {
        this.senhaHash = Produto.textoObrigatorio(senhaHash, "hash da senha");
    }

    private void atualizarEmail(String v) {
        if (v == null || !v.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new IllegalArgumentException("e-mail inválido");
        email = v.toLowerCase();
    }

    public void adicionarEndereco(Endereco e) {
        Objects.requireNonNull(e, "endereço obrigatório");
        if (!e.validar())
            throw new IllegalArgumentException("endereço inválido");
        if (!enderecos.contains(e))
            enderecos.add(e);
    }

    public void removerEndereco(Endereco e) {
        if (!enderecos.remove(e))
            throw new IllegalArgumentException("endereço não pertence ao cliente");
    }

    public void definirCarrinho(Carrinho c) {
        if (c != null && carrinho != null && carrinho != c)
            throw new IllegalStateException("cliente já possui carrinho ativo");
        if (c != null && c.getCliente() != this)
            throw new IllegalArgumentException("carrinho pertence a outro cliente");
        carrinho = c;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        atualizarEmail(v);
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String v) {
        senhaHash = Produto.textoObrigatorio(v, "hash da senha");
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String v) {
        telefone = Produto.textoObrigatorio(v, "telefone");
    }

    public List<Endereco> getEnderecos() {
        return List.copyOf(enderecos);
    }

    public Carrinho getCarrinho() {
        return carrinho;
    }
}
