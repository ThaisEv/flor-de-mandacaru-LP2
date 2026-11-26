package br.com.flordemandacaru.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public final class PersonalizacaoPedido {
    private final TamanhoBuque tamanhoBuque;
    private final String tamanhoCodigoSnapshot;
    private final Integer quantidadeFloresSnapshot;
    private final Papel papel;
    private final String papelCodigoSnapshot;
    private final String papelNomeSnapshot;
    private final BigDecimal papelPrecoSnapshot;
    private final Laco laco;
    private final String lacoCodigoSnapshot;
    private final String lacoNomeSnapshot;
    private final BigDecimal lacoPrecoSnapshot;
    private final Vaso vaso;
    private final String vasoCodigoSnapshot;
    private final String vasoNomeSnapshot;
    private final BigDecimal vasoPrecoSnapshot;
    private final BigDecimal valorAdicionalSnapshot;
    private final List<FlorPedido> flores;

    private PersonalizacaoPedido(Personalizacao personalizacao) {
        tamanhoBuque = personalizacao.getTamanhoBuque();
        tamanhoCodigoSnapshot = tamanhoBuque == null ? null : tamanhoBuque.getCodigo();
        quantidadeFloresSnapshot = tamanhoBuque == null ? null : tamanhoBuque.getQuantidadeFlores();

        papel = personalizacao.getPapel();
        papelCodigoSnapshot = papel == null ? null : papel.getCodigo();
        papelNomeSnapshot = papel == null ? null : papel.getNome();
        papelPrecoSnapshot = papel == null ? null : papel.getPrecoAdicional();

        laco = personalizacao.getLaco();
        lacoCodigoSnapshot = laco == null ? null : laco.getCodigo();
        lacoNomeSnapshot = laco == null ? null : laco.getMaterial();
        lacoPrecoSnapshot = laco == null ? null : laco.getPrecoAdicional();

        vaso = personalizacao.getVaso();
        vasoCodigoSnapshot = vaso == null ? null : vaso.getCodigo();
        vasoNomeSnapshot = vaso == null ? null : vaso.getNome();
        vasoPrecoSnapshot = vaso == null ? null : vaso.getPreco();

        valorAdicionalSnapshot = personalizacao.calcularValorAdicional();
        flores = personalizacao.getItens().stream()
                .map(item -> new FlorPedido(item.getPlanta(), item.getQuantidade(),
                        item.getPosicao(), item.getPrecoUnitario()))
                .collect(Collectors.toUnmodifiableList());
    }

    public static PersonalizacaoPedido copiarDe(Personalizacao personalizacao) {
        if (personalizacao == null)
            throw new IllegalArgumentException("personalização obrigatória");
        return new PersonalizacaoPedido(personalizacao);
    }

    public TamanhoBuque getTamanhoBuque() {
        return tamanhoBuque;
    }

    public String getTamanhoCodigoSnapshot() {
        return tamanhoCodigoSnapshot;
    }

    public Integer getQuantidadeFloresSnapshot() {
        return quantidadeFloresSnapshot;
    }

    public Papel getPapel() {
        return papel;
    }

    public String getPapelCodigoSnapshot() {
        return papelCodigoSnapshot;
    }

    public String getPapelNomeSnapshot() {
        return papelNomeSnapshot;
    }

    public BigDecimal getPapelPrecoSnapshot() {
        return papelPrecoSnapshot;
    }

    public Laco getLaco() {
        return laco;
    }

    public String getLacoCodigoSnapshot() {
        return lacoCodigoSnapshot;
    }

    public String getLacoNomeSnapshot() {
        return lacoNomeSnapshot;
    }

    public BigDecimal getLacoPrecoSnapshot() {
        return lacoPrecoSnapshot;
    }

    public Vaso getVaso() {
        return vaso;
    }

    public String getVasoCodigoSnapshot() {
        return vasoCodigoSnapshot;
    }

    public String getVasoNomeSnapshot() {
        return vasoNomeSnapshot;
    }

    public BigDecimal getVasoPrecoSnapshot() {
        return vasoPrecoSnapshot;
    }

    public BigDecimal getValorAdicionalSnapshot() {
        return valorAdicionalSnapshot;
    }

    public List<FlorPedido> getFlores() {
        return flores;
    }
}
