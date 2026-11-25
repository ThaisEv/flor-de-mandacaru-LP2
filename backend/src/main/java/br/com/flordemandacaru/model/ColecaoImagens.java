package br.com.flordemandacaru.model;

import br.com.flordemandacaru.enums.FinalidadeImagem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class ColecaoImagens {
    private final List<Imagem> imagens = new ArrayList<>();

    void adicionar(ImagemAssociavel proprietario, String codigoProprietario, Imagem imagem) {
        Objects.requireNonNull(imagem, "imagem obrigatória");
        if (codigoProprietario == null)
            throw new IllegalStateException("atribua o código do catálogo antes de adicionar imagens");
        imagem.validarProprietario(codigoProprietario);
        if (imagens.stream().anyMatch(atual -> atual.getCodigo().equals(imagem.getCodigo())))
            throw new IllegalArgumentException("imagem já cadastrada para este item do catálogo");

        List<Imagem> mesmaFinalidade = imagens.stream()
                .filter(atual -> atual.getFinalidade() == imagem.getFinalidade())
                .toList();
        if (!mesmaFinalidade.isEmpty()
                && (!imagem.temSequencial() || mesmaFinalidade.stream().anyMatch(atual -> !atual.temSequencial())))
            throw new IllegalArgumentException("imagens repetidas da mesma finalidade exigem sequenciais");

        imagem.associarA(proprietario);
        if (imagem.isPrincipal())
            imagens.forEach(atual -> atual.marcarPrincipal(false));
        imagens.add(imagem);
    }

    boolean remover(ImagemAssociavel proprietario, Imagem imagem) {
        if (imagem == null || !imagens.remove(imagem))
            return false;
        imagem.desassociarDe(proprietario);
        return true;
    }

    List<Imagem> todas() {
        return imagens.stream().sorted(Comparator.comparingInt(Imagem::getOrdem)
                .thenComparing(Imagem::getCodigo)).toList();
    }

    List<Imagem> porFinalidade(FinalidadeImagem finalidade) {
        Objects.requireNonNull(finalidade, "finalidade obrigatória");
        return todas().stream().filter(imagem -> imagem.getFinalidade() == finalidade).toList();
    }

    Optional<Imagem> principal() {
        return imagens.stream().filter(Imagem::isPrincipal).findFirst();
    }

    Optional<Imagem> primeira(FinalidadeImagem finalidade) {
        return porFinalidade(finalidade).stream().findFirst();
    }

    void definirPrincipal(ImagemAssociavel proprietario, Imagem imagem) {
        if (imagem == null || imagem.getProprietario() != proprietario || !imagens.contains(imagem))
            throw new IllegalArgumentException("a imagem principal deve pertencer a este item do catálogo");
        imagens.forEach(atual -> atual.marcarPrincipal(atual == imagem));
    }
}
