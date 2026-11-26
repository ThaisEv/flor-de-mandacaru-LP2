package br.com.flordemandacaru.model;

import java.util.Objects;
import java.util.Optional;

public final class EnderecoPedido {
    private final Endereco enderecoOrigem;
    private final String cep;
    private final String logradouro;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String cidade;
    private final String estado;

    public EnderecoPedido(Endereco endereco) {
        this(Objects.requireNonNull(endereco, "endereço obrigatório"),
                endereco.getCep(), endereco.getLogradouro(), endereco.getNumero(),
                endereco.getComplemento(), endereco.getBairro(), endereco.getCidade(), endereco.getEstado());
    }

    private EnderecoPedido(Endereco enderecoOrigem, String cep, String logradouro, String numero,
            String complemento, String bairro, String cidade, String estado) {
        new Endereco(cep, logradouro, numero, complemento, bairro, cidade, estado);
        this.enderecoOrigem = enderecoOrigem;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
    }

    public static EnderecoPedido restaurar(String cep, String logradouro, String numero,
            String complemento, String bairro, String cidade, String estado, Endereco enderecoOrigem) {
        return new EnderecoPedido(enderecoOrigem, cep, logradouro, numero,
                complemento, bairro, cidade, estado);
    }

    public Optional<Endereco> getEnderecoOrigem() {
        return Optional.ofNullable(enderecoOrigem);
    }

    public String getCep() {
        return cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }
}
