package br.com.flordemandacaru.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class ColecaoCores {
    private final List<Cor> cores = new ArrayList<>();

    void adicionar(Cor cor) {
        Objects.requireNonNull(cor, "cor obrigatória");
        if (cores.stream().anyMatch(atual -> mesmaCor(atual, cor)))
            throw new IllegalArgumentException("cor já associada");
        cores.add(cor);
    }

    boolean remover(Cor cor) {
        if (cor == null)
            return false;
        return cores.removeIf(atual -> mesmaCor(atual, cor));
    }

    List<Cor> todas() {
        return List.copyOf(cores);
    }

    void limpar() {
        cores.clear();
    }

    private static boolean mesmaCor(Cor primeira, Cor segunda) {
        if (primeira == segunda)
            return true;
        return primeira.getId() != null && primeira.getId().equals(segunda.getId());
    }
}
