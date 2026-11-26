package br.com.flordemandacaru.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public class ItemPedido {
    private Long id;
    private final Produto produto;
    private final String produtoCodigoSnapshot;
    private final String produtoNomeSnapshot;
    private final int quantidade;
    private final BigDecimal precoUnitario;
    private final BigDecimal subtotal;
    private final PersonalizacaoPedido personalizacao;

    public ItemPedido(Produto produto, int quantidade, BigDecimal precoUnitario,
            PersonalizacaoPedido personalizacao) {
        this(Objects.requireNonNull(produto, "produto obrigatório"),
                produto.getCodigo(), produto.getNome(), quantidade, precoUnitario, personalizacao);
    }

    private ItemPedido(Produto produto, String produtoCodigoSnapshot, String produtoNomeSnapshot,
            int quantidade, BigDecimal precoUnitario, PersonalizacaoPedido personalizacao) {
        if (quantidade <= 0)
            throw new IllegalArgumentException("quantidade deve ser positiva");
        if (precoUnitario == null || precoUnitario.signum() < 0)
            throw new IllegalArgumentException("preço unitário inválido");
        this.produto = produto;
        this.produtoCodigoSnapshot = Produto.textoObrigatorio(produtoCodigoSnapshot, "código do produto");
        this.produtoNomeSnapshot = Produto.textoObrigatorio(produtoNomeSnapshot, "nome do produto");
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.personalizacao = personalizacao;
        this.subtotal = calcularSubtotal();
    }

    public static ItemPedido restaurar(Produto produto, String produtoCodigoSnapshot,
            String produtoNomeSnapshot, int quantidade, BigDecimal precoUnitario,
            PersonalizacaoPedido personalizacao) {
        return new ItemPedido(produto, produtoCodigoSnapshot, produtoNomeSnapshot,
                quantidade, precoUnitario, personalizacao);
    }

    public static ItemPedido copiarDe(ItemCarrinho item) {
        Objects.requireNonNull(item, "item do carrinho obrigatório");
        PersonalizacaoPedido snapshot = null;
        BigDecimal adicional = BigDecimal.ZERO;
        if (item.getPersonalizacao() != null) {
            item.getPersonalizacao().validarParaPedido(item.getProduto());
            snapshot = PersonalizacaoPedido.copiarDe(item.getPersonalizacao());
            adicional = snapshot.getValorAdicionalSnapshot();
        }
        BigDecimal unit = item.getPrecoUnitario().add(adicional);
        return new ItemPedido(item.getProduto(), item.getQuantidade(), unit, snapshot);
    }

    public BigDecimal calcularSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id <= 0)
            throw new IllegalArgumentException("id deve ser positivo");
        this.id = id;
    }

    public Optional<Produto> getProduto() {
        return Optional.ofNullable(produto);
    }

    public String getProdutoCodigoSnapshot() {
        return produtoCodigoSnapshot;
    }

    public String getProdutoNomeSnapshot() {
        return produtoNomeSnapshot;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public Optional<PersonalizacaoPedido> getPersonalizacao() {
        return Optional.ofNullable(personalizacao);
    }
}
