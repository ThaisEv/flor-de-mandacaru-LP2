package br.com.flordemandacaru.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class SincronizacaoModeloTest {
    @Test
    void associaCoresSemDuplicarEPermiteCompartilharEntreEntidades() {
        Cor branca = new Cor("Branca", "#FFFFFF", "Branco floral");
        Planta rosa = planta("Rosa branca");
        Vaso vaso = new Vaso("Vaso claro", "Vaso de cerâmica", BigDecimal.TEN, 4,
                "cerâmica", BigDecimal.TEN, BigDecimal.TEN);
        Papel papel = new Papel("Papel seda", BigDecimal.ONE);

        rosa.adicionarCor(branca);
        vaso.adicionarCor(branca);
        papel.adicionarCor(branca);

        assertEquals(List.of(branca), rosa.getCores());
        assertEquals(List.of(branca), vaso.getCores());
        assertEquals(List.of(branca), papel.getCores());
        assertThrows(IllegalArgumentException.class, () -> rosa.adicionarCor(branca));
        assertTrue(rosa.removerCor(branca));
        assertFalse(rosa.removerCor(branca));
    }

    @Test
    void plantaSemFlorNaoMantemCoresDeFlor() {
        Planta rosa = planta("Rosa");
        rosa.adicionarCor(new Cor("Vermelha", "#FF0000", null));

        rosa.setPossuiFlor(false);

        assertTrue(rosa.getCores().isEmpty());
        assertThrows(IllegalStateException.class,
                () -> rosa.adicionarCor(new Cor("Amarela", "#FFFF00", null)));
    }

    @Test
    void tamanhoDefineQuantidadeELayoutValidaOrdensECoordenadas() {
        assertEquals(1, new TamanhoBuque("PP").getQuantidadeFlores());
        assertEquals(5, new TamanhoBuque("P").getQuantidadeFlores());
        assertEquals(7, new TamanhoBuque("M").getQuantidadeFlores());
        assertEquals(15, new TamanhoBuque("G").getQuantidadeFlores());
        assertEquals(20, new TamanhoBuque("GG").getQuantidadeFlores());
        assertThrows(IllegalArgumentException.class, () -> new TamanhoBuque("EXTRA"));

        TamanhoBuque pequeno = tamanhoP();
        pequeno.validarLayoutCompleto();
        assertEquals(5, pequeno.getPosicoes().size());
        assertEquals(2, pequeno.getPosicoes().get(1).getLinha());
        assertEquals(1, pequeno.getPosicoes().get(1).getColuna());
    }

    @Test
    void tamanhoRejeitaOrdemECoordenadaDuplicadas() {
        TamanhoBuque pequeno = new TamanhoBuque("P");
        pequeno.adicionarPosicao(new PosicaoBuque(1, 2, 2));
        assertThrows(IllegalArgumentException.class,
                () -> pequeno.adicionarPosicao(new PosicaoBuque(1, 3, 3)));
        assertThrows(IllegalArgumentException.class,
                () -> pequeno.adicionarPosicao(new PosicaoBuque(2, 2, 2)));
    }

    @Test
    void buqueUsaPosicoesUnicasEExigeLayoutCompletoAoCriarPedido() {
        TamanhoBuque pequeno = tamanhoP();
        Planta rosa = planta("Rosa branca");
        Papel papel = new Papel("Kraft", BigDecimal.ONE);
        papel.setCodigo("PAP-001");
        Laco laco = new Laco("Cetim", BigDecimal.ONE);
        laco.setCodigo("LAC-001");
        Buque buque = new Buque("Buquê de rosas", "Buquê personalizado", BigDecimal.TEN, 10,
                "clássico", papel, laco);
        buque.setCodigo("BUQ-001");

        Personalizacao incompleta = new Personalizacao();
        incompleta.setTamanhoBuque(pequeno);
        incompleta.setPapel(papel);
        incompleta.setLaco(laco);
        incompleta.adicionarPlanta(rosa, pequeno.getPosicoes().get(0));
        assertThrows(IllegalArgumentException.class,
                () -> incompleta.adicionarPlanta(rosa, pequeno.getPosicoes().get(0)));
        assertTrue(incompleta.validar());
        assertThrows(IllegalStateException.class, () -> incompleta.validarParaPedido(buque));
    }

    @Test
    void pedidoPreservaSnapshotsDeProdutoEnderecoEComposicaoDoBuque() {
        Cliente cliente = clienteComEndereco();
        Endereco endereco = cliente.getEnderecos().get(0);
        Planta rosa = planta("Rosa branca");
        rosa.setCodigo("PLT-001");
        Papel papel = new Papel("Kraft", BigDecimal.ONE);
        papel.setCodigo("PAP-001");
        Laco laco = new Laco("Cetim", BigDecimal.valueOf(2));
        laco.setCodigo("LAC-001");
        Buque buque = new Buque("Buquê de rosas", "Buquê personalizado", BigDecimal.TEN, 10,
                "clássico", papel, laco);
        buque.setCodigo("BUQ-001");

        TamanhoBuque pequeno = tamanhoP();
        Personalizacao personalizacao = new Personalizacao();
        personalizacao.setTamanhoBuque(pequeno);
        personalizacao.setPapel(papel);
        personalizacao.setLaco(laco);
        for (PosicaoBuque posicao : pequeno.getPosicoes())
            personalizacao.adicionarPlanta(rosa, posicao);

        Carrinho carrinho = new Carrinho(cliente);
        carrinho.adicionarItem(buque, 1, personalizacao);
        Pedido pedido = Pedido.criarDoCarrinho(carrinho, endereco);
        ItemPedido item = pedido.getItens().get(0);
        PersonalizacaoPedido snapshot = item.getPersonalizacao().orElseThrow();

        buque.setNome("Nome atualizado");
        rosa.setNome("Nome atualizado");
        papel.setNome("Papel atualizado");
        endereco.setLogradouro("Rua atualizada");

        assertEquals("BUQ-001", item.getProdutoCodigoSnapshot());
        assertEquals("Buquê de rosas", item.getProdutoNomeSnapshot());
        assertEquals("PLT-001", snapshot.getFlores().get(0).getPlantaCodigoSnapshot());
        assertEquals("Rosa branca", snapshot.getFlores().get(0).getPlantaNomeSnapshot());
        assertEquals("PAP-001", snapshot.getPapelCodigoSnapshot());
        assertEquals("Kraft", snapshot.getPapelNomeSnapshot());
        assertEquals("Rua das Flores", pedido.getEnderecoEntrega().getLogradouro());
        assertEquals(5, snapshot.getFlores().size());
        assertEquals(1, snapshot.getFlores().get(0).getQuantidade());
        assertEquals(2, snapshot.getFlores().get(0).getLinhaSnapshot());
        assertEquals(2, snapshot.getFlores().get(0).getColunaSnapshot());
        assertTrue(pedido.getEnderecoEntrega().getEnderecoOrigem().isPresent());
    }

    @Test
    void arranjoMantemQuantidadeAgrupadaSemTamanhoOuPosicao() {
        Planta rosa = planta("Rosa");
        Arranjo arranjo = new Arranjo("Arranjo", "Arranjo de rosas", BigDecimal.TEN, 4,
                "clássico", "médio", null);
        Personalizacao personalizacao = new Personalizacao();
        personalizacao.adicionarPlanta(rosa, 3);

        personalizacao.validarPara(arranjo);

        assertEquals(3, personalizacao.getItens().get(0).getQuantidade());
        assertNull(personalizacao.getItens().get(0).getPosicao());
        assertNull(personalizacao.getTamanhoBuque());

        arranjo.setCodigo("ARR-001");
        Cliente cliente = clienteComEndereco();
        Carrinho carrinho = new Carrinho(cliente);
        carrinho.adicionarItem(arranjo, 1, personalizacao);
        Pedido pedido = Pedido.criarDoCarrinho(carrinho, cliente.getEnderecos().get(0));
        FlorPedido florSnapshot = pedido.getItens().get(0).getPersonalizacao()
                .orElseThrow().getFlores().get(0);
        assertEquals(3, florSnapshot.getQuantidade());
        assertNull(florSnapshot.getPosicao());
    }

    private static Planta planta(String nome) {
        return new Planta(nome, "Flor de corte", BigDecimal.TEN, 30, "Rosa alba",
                "moderada", "sol", "regar", "flor", true, "primavera", "perene");
    }

    private static TamanhoBuque tamanhoP() {
        TamanhoBuque pequeno = new TamanhoBuque("P");
        pequeno.adicionarPosicao(new PosicaoBuque(1, 2, 2));
        pequeno.adicionarPosicao(new PosicaoBuque(2, 2, 1));
        pequeno.adicionarPosicao(new PosicaoBuque(3, 2, 3));
        pequeno.adicionarPosicao(new PosicaoBuque(4, 1, 2));
        pequeno.adicionarPosicao(new PosicaoBuque(5, 3, 2));
        return pequeno;
    }

    private static Cliente clienteComEndereco() {
        Cliente cliente = new Cliente("Cliente", "cliente@example.com", "hash", "11999999999");
        cliente.adicionarEndereco(new Endereco("12345-678", "Rua das Flores", "10",
                null, "Centro", "Recife", "PE"));
        return cliente;
    }
}
