package br.com.flordemandacaru.model;

public class Cor {
    private Long id;
    private String nome;
    private String codigoHexadecimal;
    private String descricao;

    public Cor(String nome, String codigoHexadecimal, String descricao) {
        this.nome = Produto.textoObrigatorio(nome, "nome");
        this.codigoHexadecimal = Produto.textoObrigatorio(codigoHexadecimal, "código hexadecimal");
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id <= 0)
            throw new IllegalArgumentException("id deve ser positivo");
        if (this.id != null && !this.id.equals(id))
            throw new IllegalStateException("o id da cor não pode ser alterado");
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Produto.textoObrigatorio(nome, "nome");
    }

    public String getCodigoHexadecimal() {
        return codigoHexadecimal;
    }

    public void setCodigoHexadecimal(String codigoHexadecimal) {
        this.codigoHexadecimal = Produto.textoObrigatorio(codigoHexadecimal, "código hexadecimal");
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
