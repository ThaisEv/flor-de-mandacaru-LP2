package br.com.flordemandacaru.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Personalizacao {
    private Long id;
    private BigDecimal valorAdicional = BigDecimal.ZERO;
    private final List<ItemPersonalizacao> itens = new ArrayList<>();
    private Papel papel;
    private Laco laco;
    private Vaso vaso;

    public BigDecimal calcularValorAdicional() {
        BigDecimal total = itens.stream().map(ItemPersonalizacao::calcularSubtotal).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        if (papel != null)
            total = total.add(papel.getPrecoAdicional());
        if (laco != null)
            total = total.add(laco.getPrecoAdicional());
        if (vaso != null)
            total = total.add(vaso.getPreco());
        valorAdicional = total;
        return total;
    }

    public boolean validar() {
        if (itens.isEmpty())
            return false;
        try {
            for (var i : itens)
                i.getPlanta().validarDisponibilidade(i.getQuantidade());
            if (papel != null)
                papel.validarDisponibilidade();
            if (laco != null)
                laco.validarDisponibilidade();
            if (vaso != null)
                vaso.validarDisponibilidade(1);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public void validarPara(Produto produto) {
        if (!(produto instanceof Buque || produto instanceof Arranjo))
            throw new IllegalArgumentException("personalização só é permitida para buquês e arranjos");
        if (!validar())
            throw new IllegalStateException("personalização inválida ou sem plantas");
        if (produto instanceof Buque && (papel == null || laco == null))
            throw new IllegalStateException("buquê personalizado exige papel e laço");
        calcularValorAdicional();
    }

    public void adicionarPlanta(Planta planta, int quantidade) {
        planta.validarDisponibilidade(quantidade);
        var item = itens.stream().filter(i -> i.getPlanta() == planta).findFirst().orElse(null);
        if (item == null)
            itens.add(new ItemPersonalizacao(planta, quantidade, planta.getPreco()));
        else
            item.atualizarQuantidade(item.getQuantidade() + quantidade);
        calcularValorAdicional();
    }

    public void removerPlanta(Planta planta) {
        if (!itens.removeIf(i -> i.getPlanta() == planta))
            throw new IllegalArgumentException("planta não encontrada na personalização");
        calcularValorAdicional();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public BigDecimal getValorAdicional() {
        return valorAdicional;
    }

    public List<ItemPersonalizacao> getItens() {
        return List.copyOf(itens);
    }

    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel v) {
        papel = v;
        calcularValorAdicional();
    }

    public Laco getLaco() {
        return laco;
    }

    public void setLaco(Laco v) {
        laco = v;
        calcularValorAdicional();
    }

    public Vaso getVaso() {
        return vaso;
    }

    public void setVaso(Vaso v) {
        vaso = v;
        calcularValorAdicional();
    }
}
