-- Flor de Mandacaru
-- 04_dados_iniciais.sql
-- Dados iniciais definidos no DER.
-- Os layouts diferentes de P ainda dependem da definição visual da interface.

BEGIN;

INSERT INTO tamanho_buque (codigo, quantidade_flores)
VALUES
    ('PP', 1),
    ('P', 5),
    ('M', 7),
    ('G', 15),
    ('GG', 20)
ON CONFLICT (codigo) DO UPDATE
SET quantidade_flores = EXCLUDED.quantidade_flores;

-- Layout P conforme o DER:
-- ordem 1 = centro; 2 = esquerda; 3 = direita; 4 = topo; 5 = base.
INSERT INTO posicao_buque (tamanho_buque_id, ordem_selecao, linha, coluna)
SELECT tb.id, dados.ordem_selecao, dados.linha, dados.coluna
FROM tamanho_buque tb
CROSS JOIN (
    VALUES
        (1, 2, 2),
        (2, 2, 1),
        (3, 2, 3),
        (4, 1, 2),
        (5, 3, 2)
) AS dados(ordem_selecao, linha, coluna)
WHERE tb.codigo = 'P'
ON CONFLICT (tamanho_buque_id, ordem_selecao)
DO UPDATE SET linha = EXCLUDED.linha, coluna = EXCLUDED.coluna;

COMMIT;

-- Os tamanhos PP, M, G e GG ficam cadastrados, mas seus layouts ainda
-- não possuem posições porque as coordenadas dependem da definição visual.
-- A aplicação deve impedir a personalização/fechamento de buquês cujo
-- layout ainda não esteja completo. O script 05 ajuda a identificar isso.
