package br.com.flordemandacaru.model;

import br.com.flordemandacaru.interfaces.Componivel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Arranjo extends Produto implements Componivel {
    private String estilo, tamanho;
    private Vaso vasoPadrao;
    private final List<ItemComposicao> composicao = new ArrayList<>();

    public Arranjo(String nome, String descricao, BigDecimal preco, int estoque, String estilo, String tamanho,
            Vaso vaso) {
        super(nome, descricao, preco, estoque);
        this.estilo = textoObrigatorio(estilo, "estilo");
        this.tamanho = textoObrigatorio(tamanho, "tamanho");
        if (vaso != null)
            vaso.validarDisponibilidade(1);
        vasoPadrao = vaso;
    }

    @Override
    protected String getPrefixoCodigo() {
        return "ARR";
    }

    @Override
    public void adicionarComponente(Produto p, int qtd) {
        if (p == this)
            throw new IllegalArgumentException("produto não pode compor a si mesmo");
        if (!(p instanceof Planta))
            throw new IllegalArgumentException("arranjos aceitam plantas como componentes");
        p.validarDisponibilidade(qtd);
        ItemComposicao item = composicao.stream().filter(i -> i.getProduto() == p).findFirst().orElse(null);
        if (item == null)
            composicao.add(new ItemComposicao(p, qtd));
        else
            item.atualizarQuantidade(item.getQuantidade() + qtd);
    }

    @Override
    public void removerComponente(Produto p) {
        if (!composicao.removeIf(i -> i.getProduto() == p))
            throw new IllegalArgumentException("componente não encontrado");
    }

    @Override
    public List<ItemComposicao> getComposicao() {
        return List.copyOf(composicao);
    }

    public void adicionarVaso(Vaso vaso) {
        Objects.requireNonNull(vaso, "vaso obrigatório").validarDisponibilidade(1);
        vasoPadrao = vaso;
    }

    public void removerVaso() {
        vasoPadrao = null;
    }

    public String getEstilo() {
        return estilo;
    }

    public void setEstilo(String v) {
        estilo = textoObrigatorio(v, "estilo");
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String v) {
        tamanho = textoObrigatorio(v, "tamanho");
    }

    public Vaso getVasoPadrao() {
        return vasoPadrao;
    }
}
