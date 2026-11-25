package br.com.flordemandacaru.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.flordemandacaru.enums.FinalidadeImagem;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ImagemTest {
    @Test
    void criaImagemValidaEDistingueCodigoDoId() {
        Imagem imagem = imagem("PLT-001", FinalidadeImagem.EDITORIAL, null, false, 1);
        imagem.setId(42L);

        assertEquals("PLT-001-EDI", imagem.getCodigo());
        assertEquals("PLT-001-EDI.webp", imagem.getNomeArquivo());
        assertEquals("webp", imagem.getFormato());
        assertEquals("uploads/plantas/PLT-001", imagem.getCaminho());
        assertEquals(42L, imagem.getId());
    }

    @Test
    void rejeitaMetadadosObrigatoriosOuNomenclaturaInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Imagem(
                "PLT-001-EDI", "PLT-001-EDI.webp", "uploads/plantas/PLT-001",
                "Rosa branca", null, false, 0));
        assertThrows(IllegalArgumentException.class, () -> new Imagem(
                "PLT-001-EDI", "PLT-001-CMP.webp", "uploads/plantas/PLT-001",
                "Rosa branca", FinalidadeImagem.EDITORIAL, false, 0));
        assertThrows(IllegalArgumentException.class, () -> new Imagem(
                "PLT-001-EDI", "PLT-001-EDI.gif", "uploads/plantas/PLT-001",
                "Rosa branca", FinalidadeImagem.EDITORIAL, false, 0));
        assertThrows(IllegalArgumentException.class, () -> new Imagem(
                "PLT-001-EDI", "PLT-001-EDI.webp", "../plantas/PLT-001",
                "Rosa branca", FinalidadeImagem.EDITORIAL, false, 0));
        assertThrows(IllegalArgumentException.class, () -> new Imagem(
                "PLT-001-EDI", "PLT-001-EDI.webp", "uploads/plantas/PLT-001",
                " ", FinalidadeImagem.EDITORIAL, false, 0));
    }

    @Test
    void geraCodigosParaTodasAsFinalidades() {
        assertEquals("PLT-001-ICO", Imagem.gerarCodigo("PLT-001", FinalidadeImagem.ICONE, null));
        assertEquals("PLT-001-CMP", Imagem.gerarCodigo("PLT-001", FinalidadeImagem.COMPOSICAO, null));
        assertEquals("PLT-001-EDI", Imagem.gerarCodigo("PLT-001", FinalidadeImagem.EDITORIAL, null));
        assertEquals("PLT-001-GAL-01", Imagem.gerarCodigo("PLT-001", FinalidadeImagem.GALERIA, 1));
        assertThrows(IllegalArgumentException.class,
                () -> Imagem.gerarCodigo("PLT-001", FinalidadeImagem.GALERIA, 0));
    }

    @Test
    void produtoGerenciaImagensPorFinalidadeEUmaPrincipal() {
        Planta rosa = planta("PLT-001");
        Imagem icone = imagem("PLT-001", FinalidadeImagem.ICONE, null, true, 0);
        Imagem editorial = imagem("PLT-001", FinalidadeImagem.EDITORIAL, 1, true, 1);
        Imagem editorial02 = imagem("PLT-001", FinalidadeImagem.EDITORIAL, 2, false, 2);

        rosa.adicionarImagem(icone);
        rosa.adicionarImagem(editorial);
        rosa.adicionarImagem(editorial02);
        assertEquals(3, rosa.getImagens().size());
        assertEquals(2, rosa.getImagensPorFinalidade(FinalidadeImagem.EDITORIAL).size());
        assertEquals(icone, rosa.getImagemIcone().orElseThrow());
        assertEquals(editorial, rosa.getImagemEditorial().orElseThrow());
        assertFalse(icone.isPrincipal());
        assertEquals(editorial, rosa.getImagemPrincipal().orElseThrow());

        rosa.definirImagemPrincipal(editorial02);
        assertFalse(editorial.isPrincipal());
        assertTrue(editorial02.isPrincipal());
        assertTrue(rosa.removerImagem(editorial02));
        assertTrue(rosa.getImagemPrincipal().isEmpty());
    }

    @Test
    void impedeDuplicacaoProprietarioInvalidoEFinalidadeRepetidaSemSequencial() {
        Planta rosa = planta("PLT-001");
        Imagem editorial = imagem("PLT-001", FinalidadeImagem.EDITORIAL, null, false, 0);
        rosa.adicionarImagem(editorial);
        assertThrows(IllegalArgumentException.class, () -> rosa.adicionarImagem(editorial));
        assertThrows(IllegalArgumentException.class, () -> rosa.adicionarImagem(
                imagem("PLT-001", FinalidadeImagem.EDITORIAL, 2, false, 1)));
        assertThrows(IllegalArgumentException.class,
                () -> planta("PLT-002").adicionarImagem(editorial));
        assertThrows(IllegalStateException.class, () -> rosa.setCodigo("PLT-002"));
    }

    @Test
    void exigeSequencialEmTodasAsImagensQuandoFinalidadeSeRepete() {
        Planta rosa = planta("PLT-001");
        rosa.adicionarImagem(imagem("PLT-001", FinalidadeImagem.GALERIA, 1, false, 1));
        rosa.adicionarImagem(imagem("PLT-001", FinalidadeImagem.GALERIA, 2, false, 2));
        assertEquals(2, rosa.getImagensGaleria().size());
    }

    @Test
    void codigosDeProdutosDeCategoriasDiferentesGeramImagensDistintas() {
        Planta rosa = planta("PLT-001");
        Vaso vaso = new Vaso("Vaso de barro", "Vaso artesanal", BigDecimal.TEN, 3,
                "barro", BigDecimal.TEN, BigDecimal.TEN, "terracota");
        vaso.setCodigo("VAS-001");
        Imagem imagemPlanta = imagem("PLT-001", FinalidadeImagem.ICONE, null, false, 0);
        Imagem imagemVaso = imagem("VAS-001", FinalidadeImagem.ICONE, null, false, 0);

        rosa.adicionarImagem(imagemPlanta);
        vaso.adicionarImagem(imagemVaso);
        assertNotEquals(imagemPlanta.getCodigo(), imagemVaso.getCodigo());
        assertEquals("VAS-001-ICO.webp", imagemVaso.getNomeArquivo());
    }

    @Test
    void buqueArranjoPapelELacoTambemPossuemImagens() {
        Papel papel = new Papel("Kraft", "natural", BigDecimal.ZERO);
        papel.setCodigo("PAP-001");
        Laco laco = new Laco("Cetim", "verde", BigDecimal.ZERO);
        laco.setCodigo("LAC-001");
        Buque buque = new Buque("Buquê", "Flores do campo", BigDecimal.TEN, 2,
                "médio", "campestre", papel, laco);
        buque.setCodigo("BUQ-001");
        Arranjo arranjo = new Arranjo("Arranjo", "Rosas brancas", BigDecimal.TEN, 2,
                "clássico", "pequeno", null);
        arranjo.setCodigo("ARR-001");

        papel.adicionarImagem(imagem("PAP-001", FinalidadeImagem.ICONE, null, false, 0));
        laco.adicionarImagem(imagem("LAC-001", FinalidadeImagem.ICONE, null, false, 0));
        buque.adicionarImagem(imagem("BUQ-001", FinalidadeImagem.COMPOSICAO, null, false, 0));
        buque.adicionarImagem(imagem("BUQ-001", FinalidadeImagem.EDITORIAL, null, true, 1));
        arranjo.adicionarImagem(imagem("ARR-001", FinalidadeImagem.EDITORIAL, null, true, 0));

        assertEquals(1, papel.getImagens().size());
        assertEquals(1, laco.getImagens().size());
        assertTrue(buque.getImagemComposicao().isPresent());
        assertTrue(buque.getImagemEditorial().isPresent());
        assertTrue(arranjo.getImagemPrincipal().isPresent());
    }

    private static Planta planta(String codigo) {
        Planta planta = new Planta("Rosa branca", "Rosa de corte", BigDecimal.TEN, 5,
                "Rosa alba", "moderada", "sol", "regar", "flor", true,
                "branca", "primavera", "perene");
        planta.setCodigo(codigo);
        return planta;
    }

    private static Imagem imagem(String codigoProprietario, FinalidadeImagem finalidade,
            Integer sequencial, boolean principal, int ordem) {
        String codigoImagem = Imagem.gerarCodigo(codigoProprietario, finalidade, sequencial);
        String pasta = switch (codigoProprietario.substring(0, 3)) {
            case "PLT" -> "plantas";
            case "VAS" -> "vasos";
            case "BUQ" -> "buques";
            case "ARR" -> "arranjos";
            case "PAP" -> "papeis";
            case "LAC" -> "lacos";
            default -> throw new IllegalArgumentException("categoria de teste inválida");
        };
        return new Imagem(codigoImagem, codigoImagem + ".webp",
                "uploads/" + pasta + "/" + codigoProprietario,
                "Imagem de teste", finalidade, principal, ordem);
    }
}
