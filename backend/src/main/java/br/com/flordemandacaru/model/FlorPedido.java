package br.com.flordemandacaru.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class FlorPedido {
    private final Planta planta;
    private final String plantaCodigoSnapshot;
    private final String plantaNomeSnapshot;
    private final PosicaoBuque posicao;
    private final Integer ordemSelecaoSnapshot;
    private final Integer linhaSnapshot;
    private final Integer colunaSnapshot;
    private final int quantidade;
    private final BigDecimal precoUnitarioSnapshot;

    public FlorPedido(Planta planta, int quantidade, PosicaoBuque posicao, BigDecimal precoUnitario) {
        this.planta = Objects.requireNonNull(planta, "planta obrigatória");
        if (quantidade <= 0)
            throw new IllegalArgumentException("quantidade deve ser positiva");
        if (posicao != null && quantidade != 1)
            throw new IllegalArgumentException("flor posicionada em buquê deve ter quantidade unitária");
        if (precoUnitario == null || precoUnitario.signum() < 0)
            throw new IllegalArgumentException("preço unitário inválido");
        this.plantaCodigoSnapshot = planta.getCodigo();
        this.plantaNomeSnapshot = planta.getNome();
        this.posicao = posicao;
        this.ordemSelecaoSnapshot = posicao == null ? null : posicao.getOrdemSelecao();
        this.linhaSnapshot = posicao == null ? null : posicao.getLinha();
        this.colunaSnapshot = posicao == null ? null : posicao.getColuna();
        this.quantidade = quantidade;
        this.precoUnitarioSnapshot = precoUnitario;
    }

    public Planta getPlanta() {
        return planta;
    }

    public String getPlantaCodigoSnapshot() {
        return plantaCodigoSnapshot;
    }

    public String getPlantaNomeSnapshot() {
        return plantaNomeSnapshot;
    }

    public PosicaoBuque getPosicao() {
        return posicao;
    }

    public Integer getOrdemSelecaoSnapshot() {
        return ordemSelecaoSnapshot;
    }

    public Integer getLinhaSnapshot() {
        return linhaSnapshot;
    }

    public Integer getColunaSnapshot() {
        return colunaSnapshot;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitarioSnapshot() {
        return precoUnitarioSnapshot;
    }
}
