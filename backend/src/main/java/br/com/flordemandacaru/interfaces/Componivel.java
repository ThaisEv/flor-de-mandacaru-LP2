package br.com.flordemandacaru.interfaces;

import br.com.flordemandacaru.model.ItemComposicao;
import br.com.flordemandacaru.model.Produto;
import java.util.List;

public interface Componivel {
    void adicionarComponente(Produto produto, int quantidade);

    void removerComponente(Produto produto);

    List<ItemComposicao> getComposicao();
}
