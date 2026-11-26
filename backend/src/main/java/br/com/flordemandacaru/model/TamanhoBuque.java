package br.com.flordemandacaru.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TamanhoBuque {
    private static final Map<String, Integer> QUANTIDADES = Map.of(
            "PP", 1,
            "P", 5,
            "M", 7,
            "G", 15,
            "GG", 20);

    private Long id;
    private final String codigo;
    private final int quantidadeFlores;
    private final List<PosicaoBuque> posicoes = new ArrayList<>();

    public TamanhoBuque(String codigo) {
        String valor = Produto.textoObrigatorio(codigo, "código").toUpperCase(java.util.Locale.ROOT);
        Integer quantidade = QUANTIDADES.get(valor);
        if (quantidade == null)
            throw new IllegalArgumentException("tamanho de buquê inválido: " + codigo);
        this.codigo = valor;
        this.quantidadeFlores = quantidade;
    }

    public void adicionarPosicao(PosicaoBuque posicao) {
        Objects.requireNonNull(posicao, "posição obrigatória");
        if (posicoes.size() >= quantidadeFlores)
            throw new IllegalStateException("o layout excede a quantidade de flores do tamanho");
        if (posicao.getOrdemSelecao() > quantidadeFlores)
            throw new IllegalArgumentException("ordem de seleção excede a quantidade de flores");
        if (posicao.getTamanhoBuque() != null && posicao.getTamanhoBuque() != this)
            throw new IllegalArgumentException("posição já pertence a outro tamanho");
        if (posicoes.stream().anyMatch(atual -> atual.getOrdemSelecao() == posicao.getOrdemSelecao()))
            throw new IllegalArgumentException("ordem de seleção duplicada");
        if (posicoes.stream().anyMatch(atual -> atual.getLinha() == posicao.getLinha()
                && atual.getColuna() == posicao.getColuna()))
            throw new IllegalArgumentException("coordenada da grade duplicada");
        posicao.associarA(this);
        posicoes.add(posicao);
    }

    public boolean removerPosicao(PosicaoBuque posicao) {
        if (posicao == null || !posicoes.remove(posicao))
            return false;
        posicao.desassociarDe(this);
        return true;
    }

    public void validarLayoutCompleto() {
        if (posicoes.size() != quantidadeFlores)
            throw new IllegalStateException("o layout deve possuir uma posição para cada flor do tamanho");
        for (int ordem = 1; ordem <= quantidadeFlores; ordem++) {
            final int ordemEsperada = ordem;
            if (posicoes.stream().noneMatch(posicao -> posicao.getOrdemSelecao() == ordemEsperada))
                throw new IllegalStateException("o layout deve possuir ordens de seleção consecutivas");
        }
    }

    boolean contem(PosicaoBuque posicao) {
        return posicoes.contains(posicao);
    }

    public List<PosicaoBuque> getPosicoes() {
        return List.copyOf(posicoes);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id <= 0)
            throw new IllegalArgumentException("id deve ser positivo");
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getQuantidadeFlores() {
        return quantidadeFlores;
    }
}
