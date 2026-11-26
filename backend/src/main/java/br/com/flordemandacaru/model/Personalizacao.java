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
    private TamanhoBuque tamanhoBuque;

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
            for (var i : itens) {
                i.getPlanta().validarDisponibilidade(i.getQuantidade());
                if (tamanhoBuque == null && i.getPosicao() != null)
                    return false;
                if (tamanhoBuque != null && (i.getPosicao() == null || i.getQuantidade() != 1
                        || !tamanhoBuque.contem(i.getPosicao())))
                    return false;
            }
            if (tamanhoBuque != null && itens.stream()
                    .map(ItemPersonalizacao::getPosicao).distinct().count() != itens.size())
                return false;
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
        if (produto instanceof Buque && tamanhoBuque == null)
            throw new IllegalStateException("buquê personalizado exige tamanho");
        if (produto instanceof Arranjo && tamanhoBuque != null)
            throw new IllegalStateException("tamanho de buquê não se aplica a arranjos");
        if (!validar())
            throw new IllegalStateException("personalização inválida ou sem plantas");
        if (produto instanceof Buque && (papel == null || laco == null))
            throw new IllegalStateException("buquê personalizado exige papel e laço");
        calcularValorAdicional();
    }

    public void validarParaPedido(Produto produto) {
        validarPara(produto);
        if (produto instanceof Buque) {
            tamanhoBuque.validarLayoutCompleto();
            if (itens.size() != tamanhoBuque.getQuantidadeFlores())
                throw new IllegalStateException("o buquê deve ter uma flor selecionada em cada posição");
        }
    }

    public void adicionarPlanta(Planta planta, int quantidade) {
        if (tamanhoBuque != null)
            throw new IllegalStateException("selecione uma posição para cada flor do buquê");
        planta.validarDisponibilidade(quantidade);
        var item = itens.stream().filter(i -> i.getPlanta() == planta && i.getPosicao() == null)
                .findFirst().orElse(null);
        if (item == null)
            itens.add(new ItemPersonalizacao(planta, quantidade, planta.getPreco()));
        else
            item.atualizarQuantidade(item.getQuantidade() + quantidade);
        calcularValorAdicional();
    }

    public void adicionarPlanta(Planta planta, PosicaoBuque posicao) {
        if (tamanhoBuque == null)
            throw new IllegalStateException("selecione o tamanho do buquê antes de preencher a grade");
        if (itens.stream().anyMatch(item -> item.getPosicao() == null))
            throw new IllegalStateException("não misture itens agrupados com flores posicionadas");
        if (posicao == null || !tamanhoBuque.contem(posicao))
            throw new IllegalArgumentException("posição não pertence ao tamanho selecionado");
        if (itens.stream().anyMatch(item -> item.getPosicao() == posicao))
            throw new IllegalArgumentException("posição já possui uma flor selecionada");
        planta.validarDisponibilidade(1);
        itens.add(new ItemPersonalizacao(planta, posicao, planta.getPreco()));
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

    public TamanhoBuque getTamanhoBuque() {
        return tamanhoBuque;
    }

    public void setTamanhoBuque(TamanhoBuque tamanhoBuque) {
        if (tamanhoBuque == null && itens.stream().anyMatch(item -> item.getPosicao() != null))
            throw new IllegalStateException("remova as flores posicionadas antes de limpar o tamanho");
        if (tamanhoBuque != null) {
            if (itens.stream().anyMatch(item -> item.getPosicao() == null || item.getQuantidade() != 1))
                throw new IllegalStateException("remova itens agrupados antes de selecionar tamanho de buquê");
            if (itens.stream().anyMatch(item -> !tamanhoBuque.contem(item.getPosicao())))
                throw new IllegalArgumentException("uma posição selecionada não pertence ao novo tamanho");
        }
        this.tamanhoBuque = tamanhoBuque;
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
