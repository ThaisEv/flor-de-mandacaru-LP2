package br.com.flordemandacaru.enums;

public enum FinalidadeImagem {
    ICONE("ICO"),
    COMPOSICAO("CMP"),
    EDITORIAL("EDI"),
    GALERIA("GAL");

    private final String codigo;

    FinalidadeImagem(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
