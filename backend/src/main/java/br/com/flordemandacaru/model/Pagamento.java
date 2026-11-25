package br.com.flordemandacaru.model;

import br.com.flordemandacaru.enums.MetodoPagamento;
import br.com.flordemandacaru.enums.StatusPagamento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Pagamento {
    private Long id;
    private final BigDecimal valor;
    private final MetodoPagamento metodo;
    private StatusPagamento status = StatusPagamento.PENDENTE;
    private LocalDateTime dataPagamento;
    private String codigoTransacao;
    private Pedido pedido;

    public Pagamento(BigDecimal valor, MetodoPagamento metodo) {
        if (valor == null || valor.signum() <= 0)
            throw new IllegalArgumentException("valor do pagamento deve ser positivo");
        this.valor = valor;
        this.metodo = Objects.requireNonNull(metodo, "método obrigatório");
    }

    void associarPedido(Pedido pedido) {
        if (this.pedido != null && this.pedido != pedido)
            throw new IllegalStateException("pagamento já associado a outro pedido");
        if (valor.compareTo(pedido.getValorTotal()) > 0)
            throw new IllegalArgumentException("pagamento não pode exceder o total do pedido");
        this.pedido = pedido;
    }

    public void processar() {
        if (status != StatusPagamento.PENDENTE)
            throw new IllegalStateException("somente pagamentos pendentes podem ser processados");
        if (pedido == null)
            throw new IllegalStateException("pagamento precisa estar associado a um pedido");
        dataPagamento = LocalDateTime.now();
    }

    public void confirmar() {
        if (status != StatusPagamento.PENDENTE || dataPagamento == null)
            throw new IllegalStateException("processe o pagamento antes de confirmar");
        status = StatusPagamento.APROVADO;
        if (pedido != null && valor.compareTo(pedido.getValorTotal()) == 0)
            pedido.confirmarPagamento();
    }

    public void recusar() {
        if (status != StatusPagamento.PENDENTE)
            throw new IllegalStateException("somente pagamento pendente pode ser recusado");
        status = StatusPagamento.RECUSADO;
    }

    public void estornar() {
        if (status != StatusPagamento.APROVADO)
            throw new IllegalStateException("somente pagamento aprovado pode ser estornado");
        status = StatusPagamento.ESTORNADO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public MetodoPagamento getMetodo() {
        return metodo;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public String getCodigoTransacao() {
        return codigoTransacao;
    }

    public void setCodigoTransacao(String v) {
        codigoTransacao = v;
    }

    public Pedido getPedido() {
        return pedido;
    }
}
