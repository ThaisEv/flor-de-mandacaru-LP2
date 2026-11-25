package br.com.flordemandacaru.model;

public class Endereco {
    private Long id;
    private String cep, logradouro, numero, complemento, bairro, cidade, estado;

    public Endereco(String cep, String logradouro, String numero, String complemento, String bairro, String cidade,
            String estado) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        if (!validar())
            throw new IllegalArgumentException("endereço incompleto ou inválido");
    }

    public boolean validar() {
        return cep != null && cep.matches("\\d{5}-?\\d{3}") && naoVazio(logradouro) && naoVazio(numero)
                && naoVazio(bairro) && naoVazio(cidade) && estado != null && estado.matches("[A-Za-z]{2}");
    }

    private static boolean naoVazio(String s) {
        return s != null && !s.isBlank();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        id = v;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String v) {
        cep = v;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String v) {
        logradouro = v;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String v) {
        numero = v;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String v) {
        complemento = v;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String v) {
        bairro = v;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String v) {
        cidade = v;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String v) {
        estado = v;
    }
}
