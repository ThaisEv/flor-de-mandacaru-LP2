package br.com.flordemandacaru.model;

import br.com.flordemandacaru.enums.StatusPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pedido {
    private Long id;
    private final Cliente cliente;
    private final Endereco enderecoEntrega;
    private final LocalDateTime dataPedido = LocalDateTime.now();
    private StatusPedido status = StatusPedido.AGUARDANDO_PAGAMENTO;
    private BigDecimal valorTotal = BigDecimal.ZERO;
    private final List<ItemPedido> itens = new ArrayList<>();
    private final List<Pagamento> pagamentos = new ArrayList<>();

    public Pedido(Cliente cliente, Endereco enderecoEntrega) {
        this.cliente = Objects.requireNonNull(cliente, "cliente obrigatório");
        this.enderecoEntrega = Objects.requireNonNull(enderecoEntrega, "endereço de entrega obrigatório");
        if (!cliente.getEnderecos().contains(enderecoEntrega))
            throw new IllegalArgumentException("endereço não pertence ao cliente");
    }

    public static Pedido criarDoCarrinho(Carrinho carrinho, Endereco endereco) {
        Objects.requireNonNull(carrinho, "carrinho obrigatório");
        if (carrinho.getItens().isEmpty())
            throw new IllegalStateException("não é possível criar pedido vazio");
        Pedido p = new Pedido(carrinho.getCliente(), endereco);
        for (ItemCarrinho i : carrinho.getItens())
            p.itens.add(ItemPedido.copiarDe(i));
        p.calcularTotal();
        return p;
    }

    public void adicionarItem(ItemPedido item) {
        if (status != StatusPedido.AGUARDANDO_PAGAMENTO)
            throw new IllegalStateException("pedido já finalizado");
        itens.add(Objects.requireNonNull(item));
        calcularTotal();
    }

    public void adicionarPagamento(Pagamento pagamento) {
        if (status == StatusPedido.CANCELADO || status == StatusPedido.ENTREGUE)
            throw new IllegalStateException("pedido não aceita pagamentos neste estado");
        Objects.requireNonNull(pagamento);
        pagamento.associarPedido(this);
        pagamentos.add(pagamento);
    }

    public void finalizar() {
        if (itens.isEmpty())
            throw new IllegalStateException("pedido deve conter ao menos um item");
        calcularTotal();
    }

    public void cancelar() {
        if (status == StatusPedido.CANCELADO)
            throw new IllegalStateException("pedido já cancelado");
        if (status == StatusPedido.EM_PREPARACAO || status == StatusPedido.ENVIADO || status == StatusPedido.ENTREGUE)
            throw new IllegalStateException("pedido não pode ser cancelado neste estado");
        status = StatusPedido.CANCELADO;
    }

    public BigDecimal calcularTotal() {
        valorTotal = itens.stream().map(ItemPedido::calcularSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return valorTotal;
    }

    public void atualizarStatus(StatusPedido novo) {
        Objects.requireNonNull(novo);
        boolean permitido = switch (status) {
            case AGUARDANDO_PAGAMENTO -> novo == StatusPedido.PAGO || novo == StatusPedido.CANCELADO;
            case PAGO -> novo == StatusPedido.EM_PREPARACAO || novo == StatusPedido.CANCELADO;
            case EM_PREPARACAO -> novo == StatusPedido.ENVIADO;
            case ENVIADO -> novo == StatusPedido.ENTREGUE;
            default -> false;
        };
        if (!permitido)
            throw new IllegalStateException("transição de status inválida: " + status + " -> " + novo);
        status = novo;
    }

    public void confirmarPagamento() {
        if (pagamentos.stream().noneMatch(p -> p.getStatus() == br.com.flordemandacaru.enums.StatusPagamento.APROVADO))
            throw new IllegalStateException("não há pagamento aprovado");
        if (status == StatusPedido.AGUARDANDO_PAGAMENTO)
            atualizarStatus(StatusPedido.PAGO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public List<ItemPedido> getItens() {
        return List.copyOf(itens);
    }

    public List<Pagamento> getPagamentos() {
        return List.copyOf(pagamentos);
    }
}
