-- Flor de Mandacaru
-- 03_integridade.sql
-- Regras que dependem de mais de uma tabela.

BEGIN;

-- No máximo uma imagem principal por proprietário.
CREATE UNIQUE INDEX uq_imagem_principal_produto
    ON imagem (produto_id) WHERE principal = TRUE AND produto_id IS NOT NULL;

CREATE UNIQUE INDEX uq_imagem_principal_papel
    ON imagem (papel_id) WHERE principal = TRUE AND papel_id IS NOT NULL;

CREATE UNIQUE INDEX uq_imagem_principal_laco
    ON imagem (laco_id) WHERE principal = TRUE AND laco_id IS NOT NULL;

-- Índices auxiliares para FKs e consultas frequentes.
CREATE INDEX idx_endereco_cliente ON endereco(cliente_id);
CREATE INDEX idx_produto_nome ON produto(nome);
CREATE INDEX idx_item_composicao_componente ON item_composicao(componente_id);
CREATE INDEX idx_item_carrinho_carrinho ON item_carrinho(carrinho_id);
CREATE INDEX idx_item_carrinho_produto ON item_carrinho(produto_id);
CREATE INDEX idx_pedido_cliente_data ON pedido(cliente_id, data_pedido DESC);
CREATE INDEX idx_item_pedido_pedido ON item_pedido(pedido_id);
CREATE INDEX idx_pagamento_pedido ON pagamento(pedido_id);

-- Garante que cada PRODUTO tenha exatamente o subtipo indicado em tipo_produto.
CREATE OR REPLACE FUNCTION fn_validar_especializacao_produto()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_produto_id BIGINT;
    v_tipo VARCHAR(20);
    v_total INTEGER;
    v_esperado INTEGER;
BEGIN
    IF TG_TABLE_NAME = 'produto' THEN
        v_produto_id := CASE WHEN TG_OP = 'DELETE' THEN OLD.id ELSE NEW.id END;
    ELSE
        v_produto_id := CASE WHEN TG_OP = 'DELETE' THEN OLD.produto_id ELSE NEW.produto_id END;
    END IF;

    SELECT tipo_produto
      INTO v_tipo
      FROM produto
     WHERE id = v_produto_id;

    -- Produto removido: não há especialização a validar.
    IF NOT FOUND THEN
        RETURN NULL;
    END IF;

    SELECT
        (EXISTS (SELECT 1 FROM planta WHERE produto_id = v_produto_id))::INTEGER +
        (EXISTS (SELECT 1 FROM vaso WHERE produto_id = v_produto_id))::INTEGER +
        (EXISTS (SELECT 1 FROM buque WHERE produto_id = v_produto_id))::INTEGER +
        (EXISTS (SELECT 1 FROM arranjo WHERE produto_id = v_produto_id))::INTEGER
      INTO v_total;

    SELECT CASE v_tipo
        WHEN 'PLANTA' THEN (EXISTS (SELECT 1 FROM planta WHERE produto_id = v_produto_id))::INTEGER
        WHEN 'VASO' THEN (EXISTS (SELECT 1 FROM vaso WHERE produto_id = v_produto_id))::INTEGER
        WHEN 'BUQUE' THEN (EXISTS (SELECT 1 FROM buque WHERE produto_id = v_produto_id))::INTEGER
        WHEN 'ARRANJO' THEN (EXISTS (SELECT 1 FROM arranjo WHERE produto_id = v_produto_id))::INTEGER
        ELSE 0
    END
    INTO v_esperado;

    IF v_total <> 1 OR v_esperado <> 1 THEN
        RAISE EXCEPTION
            'Produto % (tipo %) deve possuir exatamente um registro no subtipo correspondente.',
            v_produto_id, v_tipo;
    END IF;

    RETURN NULL;
END;
$$;

CREATE CONSTRAINT TRIGGER ct_produto_especializacao
AFTER INSERT OR UPDATE OR DELETE ON produto
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION fn_validar_especializacao_produto();

CREATE CONSTRAINT TRIGGER ct_planta_especializacao
AFTER INSERT OR UPDATE OR DELETE ON planta
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION fn_validar_especializacao_produto();

CREATE CONSTRAINT TRIGGER ct_vaso_especializacao
AFTER INSERT OR UPDATE OR DELETE ON vaso
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION fn_validar_especializacao_produto();

CREATE CONSTRAINT TRIGGER ct_buque_especializacao
AFTER INSERT OR UPDATE OR DELETE ON buque
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION fn_validar_especializacao_produto();

CREATE CONSTRAINT TRIGGER ct_arranjo_especializacao
AFTER INSERT OR UPDATE OR DELETE ON arranjo
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION fn_validar_especializacao_produto();

-- O produto proprietário de uma composição precisa ser BUQUE ou ARRANJO.
CREATE OR REPLACE FUNCTION fn_validar_produto_composicao()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_tipo VARCHAR(20);
BEGIN
    SELECT tipo_produto
      INTO v_tipo
      FROM produto
     WHERE id = NEW.produto_composicao_id;

    IF v_tipo NOT IN ('BUQUE', 'ARRANJO') THEN
        RAISE EXCEPTION
            'O produto proprietário da composição deve ser BUQUE ou ARRANJO (produto_id=%).',
            NEW.produto_composicao_id;
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validar_produto_composicao
BEFORE INSERT OR UPDATE OF produto_composicao_id ON item_composicao
FOR EACH ROW EXECUTE FUNCTION fn_validar_produto_composicao();

-- A personalização só pode pertencer a um item de BUQUE ou ARRANJO.
-- Buquê exige tamanho; arranjo não utiliza tamanho/layout de buquê.
CREATE OR REPLACE FUNCTION fn_validar_tipo_personalizacao()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_tipo VARCHAR(20);
BEGIN
    SELECT p.tipo_produto
      INTO v_tipo
      FROM item_carrinho ic
      JOIN produto p ON p.id = ic.produto_id
     WHERE ic.id = NEW.item_carrinho_id;

    IF v_tipo NOT IN ('BUQUE', 'ARRANJO') THEN
        RAISE EXCEPTION 'Somente BUQUE ou ARRANJO pode receber personalização.';
    END IF;

    IF v_tipo = 'BUQUE' AND NEW.tamanho_buque_id IS NULL THEN
        RAISE EXCEPTION 'A personalização de BUQUE exige um tamanho.';
    END IF;

    IF v_tipo = 'ARRANJO' AND NEW.tamanho_buque_id IS NOT NULL THEN
        RAISE EXCEPTION 'A personalização de ARRANJO não utiliza tamanho de buquê.';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_validar_tipo_personalizacao
BEFORE INSERT OR UPDATE OF item_carrinho_id, tamanho_buque_id ON personalizacao
FOR EACH ROW EXECUTE FUNCTION fn_validar_tipo_personalizacao();

-- Impede cadastrar mais posições do que a quantidade de flores definida.
-- A quantidade exata será validada no fechamento do pedido, pois os layouts
-- PP, M, G e GG ainda dependem da definição visual da interface.
CREATE OR REPLACE FUNCTION fn_impedir_excesso_posicoes_tamanho()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_tamanho_id BIGINT;
    v_quantidade INTEGER;
    v_posicoes INTEGER;
BEGIN
    IF TG_TABLE_NAME = 'tamanho_buque' THEN
        v_tamanho_id := CASE WHEN TG_OP = 'DELETE' THEN OLD.id ELSE NEW.id END;
    ELSE
        v_tamanho_id := CASE WHEN TG_OP = 'DELETE' THEN OLD.tamanho_buque_id ELSE NEW.tamanho_buque_id END;
    END IF;

    SELECT quantidade_flores
      INTO v_quantidade
      FROM tamanho_buque
     WHERE id = v_tamanho_id;

    IF NOT FOUND THEN
        RETURN NULL;
    END IF;

    SELECT COUNT(*)
      INTO v_posicoes
      FROM posicao_buque
     WHERE tamanho_buque_id = v_tamanho_id;

    IF v_posicoes > v_quantidade THEN
        RAISE EXCEPTION
            'Tamanho % permite % posições, mas possui % posições cadastradas.',
            v_tamanho_id, v_quantidade, v_posicoes;
    END IF;

    RETURN NULL;
END;
$$;

CREATE CONSTRAINT TRIGGER ct_posicao_limite_tamanho
AFTER INSERT OR UPDATE OR DELETE ON posicao_buque
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION fn_impedir_excesso_posicoes_tamanho();

CREATE CONSTRAINT TRIGGER ct_quantidade_limite_tamanho
AFTER UPDATE OF quantidade_flores ON tamanho_buque
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION fn_impedir_excesso_posicoes_tamanho();

COMMIT;
