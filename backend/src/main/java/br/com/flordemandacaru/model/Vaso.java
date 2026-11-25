package br.com.flordemandacaru.model;

import java.math.BigDecimal;

public class Vaso extends Produto {
    private String material, cor;
    private BigDecimal altura, largura;

    public Vaso(String nome, String descricao, BigDecimal preco, int estoque, String material,
            BigDecimal altura, BigDecimal largura, String cor) {
        super(nome, descricao, preco, estoque);
        this.material = textoObrigatorio(material, "material");
        this.altura = dimensao(altura);
        this.largura = dimensao(largura);
        this.cor = textoObrigatorio(cor, "cor");
    }

    @Override
    protected String getPrefixoCodigo() {
        return "VAS";
    }

    private static BigDecimal dimensao(BigDecimal n) {
        if (n == null || n.signum() <= 0)
            throw new IllegalArgumentException("dimensão deve ser positiva");
        return n;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String v) {
        material = textoObrigatorio(v, "material");
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public void setAltura(BigDecimal v) {
        altura = dimensao(v);
    }

    public BigDecimal getLargura() {
        return largura;
    }

    public void setLargura(BigDecimal v) {
        largura = dimensao(v);
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String v) {
        cor = textoObrigatorio(v, "cor");
    }
}
