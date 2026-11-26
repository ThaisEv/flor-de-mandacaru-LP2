package br.com.flordemandacaru.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import br.com.flordemandacaru.enums.FinalidadeImagem;

public abstract class Produto implements ImagemAssociavel {
    private Long id;
    private String codigo;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int quantidadeEstoque;
    private boolean ativo = true;
    private final ColecaoImagens imagens = new ColecaoImagens();
    private final ColecaoCores cores = new ColecaoCores();

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

    protected abstract String getPrefixoCodigo();

    public void adicionarCor(Cor cor) {
        cores.adicionar(cor);
    }

    public boolean removerCor(Cor cor) {
        return cores.remover(cor);
    }

    public List<Cor> getCores() {
        return cores.todas();
    }

    protected void removerTodasCores() {
        cores.limpar();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        String validado = CodigoCatalogo.validar(codigo, getPrefixoCodigo());
        if (this.codigo != null && !this.codigo.equals(validado))
            throw new IllegalStateException("o código do produto é estável e não pode ser alterado");
        this.codigo = validado;
    }

    public void adicionarImagem(Imagem imagem) {
        imagens.adicionar(this, codigo, imagem);
    }

    public boolean removerImagem(Imagem imagem) {
        return imagens.remover(this, imagem);
    }

    public List<Imagem> getImagens() {
        return imagens.todas();
    }

    public List<Imagem> getImagensPorFinalidade(FinalidadeImagem finalidade) {
        return imagens.porFinalidade(finalidade);
    }

    public Optional<Imagem> getImagemPrincipal() {
        return imagens.principal();
    }

    public Optional<Imagem> getImagemIcone() {
        return imagens.primeira(FinalidadeImagem.ICONE);
    }

    public Optional<Imagem> getImagemComposicao() {
        return imagens.primeira(FinalidadeImagem.COMPOSICAO);
    }

    public Optional<Imagem> getImagemEditorial() {
        return imagens.primeira(FinalidadeImagem.EDITORIAL);
    }

    public List<Imagem> getImagensGaleria() {
        return imagens.porFinalidade(FinalidadeImagem.GALERIA);
    }

    public void definirImagemPrincipal(Imagem imagem) {
        imagens.definirPrincipal(this, imagem);
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
