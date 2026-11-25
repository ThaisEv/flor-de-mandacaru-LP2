package br.com.flordemandacaru.model;

import br.com.flordemandacaru.enums.FinalidadeImagem;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Imagem {
    private static final Pattern CODIGO_IMAGEM = Pattern.compile(
            "^(PLT|VAS|BUQ|ARR|PAP|LAC)-(\\d{3,})-(ICO|CMP|EDI|GAL)(-\\d{2,})?$");
    private static final Pattern NOME_ARQUIVO = Pattern.compile(
            "^([A-Z]{3}-\\d{3,}-(?:ICO|CMP|EDI|GAL)(?:-\\d{2,})?)\\.(webp|png|jpe?g)$");
    private static final Map<String, String> PASTAS_CATEGORIA = Map.of(
            "PLT", "plantas",
            "VAS", "vasos",
            "BUQ", "buques",
            "ARR", "arranjos",
            "PAP", "papeis",
            "LAC", "lacos");

    private Long id;
    private final String codigo;
    private final String nomeArquivo;
    private final String caminho;
    private final String descricao;
    private final FinalidadeImagem finalidade;
    private boolean principal;
    private final int ordem;
    private ImagemAssociavel proprietario;

    public Imagem(String codigo, String nomeArquivo, String caminho, String descricao,
            FinalidadeImagem finalidade, boolean principal, int ordem) {
        this.codigo = validarCodigo(codigo, finalidade);
        this.nomeArquivo = validarNomeArquivo(nomeArquivo, this.codigo);
        this.caminho = validarCaminho(caminho, codigoProprietario(this.codigo));
        this.descricao = validarDescricao(descricao);
        this.finalidade = Objects.requireNonNull(finalidade, "finalidade obrigatória");
        if (ordem < 0)
            throw new IllegalArgumentException("ordem não pode ser negativa");
        this.principal = principal;
        this.ordem = ordem;
    }

    public static String gerarCodigo(String codigoProprietario, FinalidadeImagem finalidade, Integer sequencial) {
        String codigoValidado = validarCodigoProprietario(codigoProprietario);
        if (finalidade == null)
            throw new IllegalArgumentException("finalidade é obrigatória");
        if (sequencial != null && sequencial <= 0)
            throw new IllegalArgumentException("sequencial deve ser positivo");
        String codigo = codigoValidado + "-" + finalidade.getCodigo();
        return sequencial == null ? codigo : codigo + "-" + String.format(Locale.ROOT, "%02d", sequencial);
    }

    private static String validarCodigo(String codigo, FinalidadeImagem finalidade) {
        if (finalidade == null)
            throw new IllegalArgumentException("finalidade é obrigatória");
        String valor = Produto.textoObrigatorio(codigo, "código");
        Matcher matcher = CODIGO_IMAGEM.matcher(valor);
        if (valor.length() > 40 || !matcher.matches() || !matcher.group(3).equals(finalidade.getCodigo()))
            throw new IllegalArgumentException("código da imagem incompatível com sua finalidade");
        return valor;
    }

    private static String validarNomeArquivo(String nomeArquivo, String codigo) {
        String nome = Produto.textoObrigatorio(nomeArquivo, "nome do arquivo");
        Matcher matcher = NOME_ARQUIVO.matcher(nome);
        if (nome.length() > 255 || !matcher.matches() || !matcher.group(1).equals(codigo))
            throw new IllegalArgumentException("nome do arquivo deve corresponder ao código e usar webp, png, jpg ou jpeg");
        return nome;
    }

    private static String validarDescricao(String descricao) {
        String valor = Produto.textoObrigatorio(descricao, "descrição alternativa");
        if (valor.length() > 255)
            throw new IllegalArgumentException("descrição alternativa não pode exceder 255 caracteres");
        return valor;
    }

    private static String validarCaminho(String caminho, String codigoProprietario) {
        String valor = Produto.textoObrigatorio(caminho, "caminho");
        if (valor.length() > 500 || valor.contains("\\") || valor.contains(":")
                || valor.startsWith("/") || valor.endsWith("/"))
            throw new IllegalArgumentException("caminho deve ser relativo e usar barras '/'");
        String[] partes = valor.split("/", -1);
        if (partes.length < 3 || !partes[0].equals("uploads")
                || !partes[partes.length - 1].equals(codigoProprietario))
            throw new IllegalArgumentException("caminho deve terminar na pasta do item dentro de uploads");
        for (String parte : partes) {
            if (parte.isBlank() || parte.equals(".") || parte.equals("..")
                    || !parte.matches("[a-z0-9-]+|[A-Z]{3}-\\d{3,}"))
                throw new IllegalArgumentException("caminho contém um segmento inválido");
        }
        String prefixo = codigoProprietario.substring(0, 3);
        if (!partes[1].equals(PASTAS_CATEGORIA.get(prefixo)))
            throw new IllegalArgumentException("categoria do caminho incompatível com o código do item");
        return valor;
    }

    private static String validarCodigoProprietario(String codigo) {
        String valor = Produto.textoObrigatorio(codigo, "código do item");
        if (valor.length() > 20 || !valor.matches("(PLT|VAS|BUQ|ARR|PAP|LAC)-\\d{3,}"))
            throw new IllegalArgumentException("código do item deve seguir o padrão CAT-001");
        return valor;
    }

    private static String codigoProprietario(String codigoImagem) {
        Matcher matcher = CODIGO_IMAGEM.matcher(codigoImagem);
        if (!matcher.matches())
            throw new IllegalArgumentException("código de imagem inválido");
        return matcher.group(1) + "-" + matcher.group(2);
    }

    void validarProprietario(String codigoProprietario) {
        if (!codigoProprietario(this.codigo).equals(codigoProprietario))
            throw new IllegalArgumentException("código da imagem não corresponde ao código do item");
    }

    void associarA(ImagemAssociavel proprietario) {
        Objects.requireNonNull(proprietario, "proprietário obrigatório");
        if (this.proprietario != null && this.proprietario != proprietario)
            throw new IllegalArgumentException("imagem já está associada a outro item do catálogo");
        this.proprietario = proprietario;
    }

    void desassociarDe(ImagemAssociavel proprietario) {
        if (this.proprietario == proprietario)
            this.proprietario = null;
    }

    ImagemAssociavel getProprietario() {
        return proprietario;
    }

    void marcarPrincipal(boolean principal) {
        this.principal = principal;
    }

    boolean temSequencial() {
        Matcher matcher = CODIGO_IMAGEM.matcher(codigo);
        return matcher.matches() && matcher.group(4) != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id <= 0)
            throw new IllegalArgumentException("id deve ser positivo");
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public String getCaminho() {
        return caminho;
    }

    public String getDescricao() {
        return descricao;
    }

    public FinalidadeImagem getFinalidade() {
        return finalidade;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public String getFormato() {
        return nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1);
    }

    public int getOrdem() {
        return ordem;
    }
}
