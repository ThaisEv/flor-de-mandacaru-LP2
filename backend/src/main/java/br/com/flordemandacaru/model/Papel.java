package br.com.flordemandacaru.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.List;
import java.util.Optional;

import br.com.flordemandacaru.enums.FinalidadeImagem;

public class Papel implements ImagemAssociavel {
    private Long id;
    private String codigo;
    private String nome;
    private BigDecimal precoAdicional;
    private boolean ativo = true;
    private final ColecaoImagens imagens = new ColecaoImagens();
    private final ColecaoCores cores = new ColecaoCores();

    public Papel(String nome, BigDecimal precoAdicional) {
        this.nome = Produto.textoObrigatorio(nome, "nome");
        atualizarPrecoAdicional(precoAdicional);
    }

    public void atualizarPrecoAdicional(BigDecimal v) {
        if (v == null || v.signum() < 0)
            throw new IllegalArgumentException("preço adicional inválido");
        precoAdicional = v;
    }

    public void adicionarCor(Cor cor) {
        cores.adicionar(cor);
    }

    public boolean removerCor(Cor cor) {
        return cores.remover(cor);
    }

    public List<Cor> getCores() {
        return cores.todas();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        String validado = CodigoCatalogo.validar(codigo, "PAP");
        if (this.codigo != null && !this.codigo.equals(validado))
            throw new IllegalStateException("o código do papel é estável e não pode ser alterado");
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

    public void definirImagemPrincipal(Imagem imagem) {
        imagens.definirPrincipal(this, imagem);
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
