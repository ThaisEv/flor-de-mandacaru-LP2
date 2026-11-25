package br.com.flordemandacaru.model;

import br.com.flordemandacaru.interfaces.Componivel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Buque extends Produto implements Componivel {
    private String tamanho, estilo;
    private Papel papelPadrao;
    private Laco lacoPadrao;
    private final List<ItemComposicao> composicao = new ArrayList<>();

    public Buque(String nome, String descricao, BigDecimal preco, int estoque, String tamanho, String estilo,
            Papel papel, Laco laco) {
        super(nome, descricao, preco, estoque);
        this.tamanho = textoObrigatorio(tamanho, "tamanho");
        this.estilo = textoObrigatorio(estilo, "estilo");
        papelPadrao = Objects.requireNonNull(papel, "buquê exige papel padrão");
        lacoPadrao = Objects.requireNonNull(laco, "buquê exige laço padrão");
        papel.validarDisponibilidade();
        laco.validarDisponibilidade();
    }

    @Override
    protected String getPrefixoCodigo() {
        return "BUQ";
    }

    @Override
    public void adicionarComponente(Produto p, int qtd) {
        if (p == this)
            throw new IllegalArgumentException("produto não pode compor a si mesmo");
        if (!(p instanceof Planta))
            throw new IllegalArgumentException("buquês aceitam plantas como componentes");
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

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String v) {
        tamanho = textoObrigatorio(v, "tamanho");
    }

    public String getEstilo() {
        return estilo;
    }

    public void setEstilo(String v) {
        estilo = textoObrigatorio(v, "estilo");
    }

    public Papel getPapelPadrao() {
        return papelPadrao;
    }

    public void setPapelPadrao(Papel v) {
        papelPadrao = Objects.requireNonNull(v);
        v.validarDisponibilidade();
    }

    public Laco getLacoPadrao() {
        return lacoPadrao;
    }

    public void setLacoPadrao(Laco v) {
        lacoPadrao = Objects.requireNonNull(v);
        v.validarDisponibilidade();
    }
}
