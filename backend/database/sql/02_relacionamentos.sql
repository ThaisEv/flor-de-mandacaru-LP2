-- Flor de Mandacaru
-- 02_relacionamentos.sql
-- Adiciona as chaves estrangeiras depois da criação das tabelas.

BEGIN;

ALTER TABLE endereco
    ADD CONSTRAINT fk_endereco_cliente
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE;

ALTER TABLE planta
    ADD CONSTRAINT fk_planta_produto
    FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE CASCADE;

ALTER TABLE vaso
    ADD CONSTRAINT fk_vaso_produto
    FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE CASCADE;

ALTER TABLE buque
    ADD CONSTRAINT fk_buque_produto
    FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_buque_papel
    FOREIGN KEY (papel_padrao_id) REFERENCES papel(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_buque_laco
    FOREIGN KEY (laco_padrao_id) REFERENCES laco(id) ON DELETE SET NULL;

ALTER TABLE arranjo
    ADD CONSTRAINT fk_arranjo_produto
    FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_arranjo_vaso
    FOREIGN KEY (vaso_padrao_id) REFERENCES vaso(produto_id) ON DELETE SET NULL;

ALTER TABLE imagem
    ADD CONSTRAINT fk_imagem_produto
    FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_imagem_papel
    FOREIGN KEY (papel_id) REFERENCES papel(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_imagem_laco
    FOREIGN KEY (laco_id) REFERENCES laco(id) ON DELETE CASCADE;

ALTER TABLE produto_cor
    ADD CONSTRAINT fk_produto_cor_produto_tipo
    FOREIGN KEY (produto_id, tipo_produto) REFERENCES produto(id, tipo_produto) ON DELETE CASCADE,
    ADD CONSTRAINT fk_produto_cor_cor
    FOREIGN KEY (cor_id) REFERENCES cor(id) ON DELETE CASCADE;

ALTER TABLE vaso_cor
    ADD CONSTRAINT fk_vaso_cor_vaso
    FOREIGN KEY (vaso_id) REFERENCES vaso(produto_id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_vaso_cor_cor
    FOREIGN KEY (cor_id) REFERENCES cor(id) ON DELETE CASCADE;

ALTER TABLE papel_cor
    ADD CONSTRAINT fk_papel_cor_papel
    FOREIGN KEY (papel_id) REFERENCES papel(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_papel_cor_cor
    FOREIGN KEY (cor_id) REFERENCES cor(id) ON DELETE CASCADE;

ALTER TABLE laco_cor
    ADD CONSTRAINT fk_laco_cor_laco
    FOREIGN KEY (laco_id) REFERENCES laco(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_laco_cor_cor
    FOREIGN KEY (cor_id) REFERENCES cor(id) ON DELETE CASCADE;

ALTER TABLE posicao_buque
    ADD CONSTRAINT fk_posicao_tamanho
    FOREIGN KEY (tamanho_buque_id) REFERENCES tamanho_buque(id) ON DELETE CASCADE;

ALTER TABLE item_composicao
    ADD CONSTRAINT fk_item_composicao_produto
    FOREIGN KEY (produto_composicao_id) REFERENCES produto(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_item_composicao_componente
    FOREIGN KEY (componente_id) REFERENCES produto(id) ON DELETE RESTRICT;

ALTER TABLE carrinho
    ADD CONSTRAINT fk_carrinho_cliente
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE;

ALTER TABLE item_carrinho
    ADD CONSTRAINT fk_item_carrinho_carrinho
    FOREIGN KEY (carrinho_id) REFERENCES carrinho(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_item_carrinho_produto
    FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE RESTRICT;

ALTER TABLE personalizacao
    ADD CONSTRAINT fk_personalizacao_item_carrinho
    FOREIGN KEY (item_carrinho_id) REFERENCES item_carrinho(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_personalizacao_tamanho
    FOREIGN KEY (tamanho_buque_id) REFERENCES tamanho_buque(id) ON DELETE RESTRICT,
    ADD CONSTRAINT fk_personalizacao_papel
    FOREIGN KEY (papel_id) REFERENCES papel(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_personalizacao_laco
    FOREIGN KEY (laco_id) REFERENCES laco(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_personalizacao_vaso
    FOREIGN KEY (vaso_id) REFERENCES vaso(produto_id) ON DELETE SET NULL;

ALTER TABLE item_personalizacao
    ADD CONSTRAINT fk_item_personalizacao_personalizacao_tamanho
    FOREIGN KEY (personalizacao_id, tamanho_buque_id)
    REFERENCES personalizacao(id, tamanho_buque_id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_item_personalizacao_posicao_tamanho
    FOREIGN KEY (posicao_buque_id, tamanho_buque_id)
    REFERENCES posicao_buque(id, tamanho_buque_id) ON DELETE RESTRICT,
    ADD CONSTRAINT fk_item_personalizacao_planta
    FOREIGN KEY (planta_id) REFERENCES planta(produto_id) ON DELETE RESTRICT;

ALTER TABLE pedido
    ADD CONSTRAINT fk_pedido_cliente
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE RESTRICT;

ALTER TABLE endereco_pedido
    ADD CONSTRAINT fk_endereco_pedido_pedido
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_endereco_pedido_endereco
    FOREIGN KEY (endereco_id) REFERENCES endereco(id) ON DELETE SET NULL;

ALTER TABLE item_pedido
    ADD CONSTRAINT fk_item_pedido_pedido
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_item_pedido_produto
    FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE SET NULL;

ALTER TABLE personalizacao_pedido
    ADD CONSTRAINT fk_personalizacao_pedido_item
    FOREIGN KEY (item_pedido_id) REFERENCES item_pedido(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_personalizacao_pedido_tamanho
    FOREIGN KEY (tamanho_buque_id) REFERENCES tamanho_buque(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_personalizacao_pedido_papel
    FOREIGN KEY (papel_id) REFERENCES papel(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_personalizacao_pedido_laco
    FOREIGN KEY (laco_id) REFERENCES laco(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_personalizacao_pedido_vaso
    FOREIGN KEY (vaso_id) REFERENCES vaso(produto_id) ON DELETE SET NULL;

ALTER TABLE flor_pedido
    ADD CONSTRAINT fk_flor_pedido_item
    FOREIGN KEY (item_pedido_id) REFERENCES item_pedido(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_flor_pedido_planta
    FOREIGN KEY (planta_id) REFERENCES planta(produto_id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_flor_pedido_posicao
    FOREIGN KEY (posicao_buque_id) REFERENCES posicao_buque(id) ON DELETE SET NULL;

ALTER TABLE pagamento
    ADD CONSTRAINT fk_pagamento_pedido
    FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE;

COMMIT;
