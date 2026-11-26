package br.com.flordemandacaru.model;

public class PosicaoBuque {
    private Long id;
    private final int ordemSelecao;
    private final int linha;
    private final int coluna;
    private TamanhoBuque tamanhoBuque;

    public PosicaoBuque(int ordemSelecao, int linha, int coluna) {
        if (ordemSelecao <= 0 || linha <= 0 || coluna <= 0)
            throw new IllegalArgumentException("ordem, linha e coluna devem ser positivas");
        this.ordemSelecao = ordemSelecao;
        this.linha = linha;
        this.coluna = coluna;
    }

    void associarA(TamanhoBuque tamanhoBuque) {
        if (this.tamanhoBuque != null && this.tamanhoBuque != tamanhoBuque)
            throw new IllegalArgumentException("posição já pertence a outro tamanho");
        this.tamanhoBuque = tamanhoBuque;
    }

    void desassociarDe(TamanhoBuque tamanhoBuque) {
        if (this.tamanhoBuque == tamanhoBuque)
            this.tamanhoBuque = null;
    }

    TamanhoBuque getTamanhoBuque() {
        return tamanhoBuque;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id <= 0)
            throw new IllegalArgumentException("id deve ser positivo");
        this.id = id;
    }

    public int getOrdemSelecao() {
        return ordemSelecao;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }
}
