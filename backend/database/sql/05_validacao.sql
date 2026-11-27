-- Flor de Mandacaru
-- 05_validacao.sql
-- Consultas para conferir a estrutura e os dados.

-- 1. Listar tabelas do schema public.
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_type = 'BASE TABLE'
ORDER BY table_name;

-- 2. Conferir os tamanhos cadastrados.
SELECT codigo, quantidade_flores
FROM tamanho_buque
ORDER BY CASE codigo
    WHEN 'PP' THEN 1 WHEN 'P' THEN 2 WHEN 'M' THEN 3
    WHEN 'G' THEN 4 WHEN 'GG' THEN 5 ELSE 6
END;

-- 3. Conferir quantas posições existem por tamanho.
SELECT
    tb.codigo,
    tb.quantidade_flores AS flores_esperadas,
    COUNT(pb.id) AS posicoes_cadastradas,
    (COUNT(pb.id) = tb.quantidade_flores) AS layout_completo
FROM tamanho_buque tb
LEFT JOIN posicao_buque pb ON pb.tamanho_buque_id = tb.id
GROUP BY tb.id, tb.codigo, tb.quantidade_flores
ORDER BY tb.codigo;

-- 4. Conferir produtos sem subtipo ou com subtipo divergente.
SELECT p.id, p.codigo, p.tipo_produto
FROM produto p
LEFT JOIN planta pl ON pl.produto_id = p.id
LEFT JOIN vaso v ON v.produto_id = p.id
LEFT JOIN buque b ON b.produto_id = p.id
LEFT JOIN arranjo a ON a.produto_id = p.id
WHERE
    (pl.produto_id IS NOT NULL)::INTEGER +
    (v.produto_id IS NOT NULL)::INTEGER +
    (b.produto_id IS NOT NULL)::INTEGER +
    (a.produto_id IS NOT NULL)::INTEGER <> 1
    OR (p.tipo_produto = 'PLANTA' AND pl.produto_id IS NULL)
    OR (p.tipo_produto = 'VASO' AND v.produto_id IS NULL)
    OR (p.tipo_produto = 'BUQUE' AND b.produto_id IS NULL)
    OR (p.tipo_produto = 'ARRANJO' AND a.produto_id IS NULL);

-- 5. Conferir imagens principais duplicadas (o índice já impede isso).
SELECT 'produto' AS proprietario, produto_id AS id, COUNT(*)
FROM imagem
WHERE principal = TRUE AND produto_id IS NOT NULL
GROUP BY produto_id HAVING COUNT(*) > 1
UNION ALL
SELECT 'papel', papel_id, COUNT(*)
FROM imagem
WHERE principal = TRUE AND papel_id IS NOT NULL
GROUP BY papel_id HAVING COUNT(*) > 1
UNION ALL
SELECT 'laco', laco_id, COUNT(*)
FROM imagem
WHERE principal = TRUE AND laco_id IS NOT NULL
GROUP BY laco_id HAVING COUNT(*) > 1;

-- 6. Conferir personalizações de buquê com posições ainda não preenchidas.
SELECT
    per.id AS personalizacao_id,
    tb.codigo AS tamanho,
    tb.quantidade_flores,
    COUNT(ip.id) AS flores_selecionadas
FROM personalizacao per
JOIN tamanho_buque tb ON tb.id = per.tamanho_buque_id
LEFT JOIN item_personalizacao ip ON ip.personalizacao_id = per.id
GROUP BY per.id, tb.codigo, tb.quantidade_flores
HAVING COUNT(ip.id) <> tb.quantidade_flores;

-- 7. Conferir FKs inválidas: deve retornar zero linhas.
SELECT conrelid::regclass AS tabela, conname AS restricao
FROM pg_constraint
WHERE contype = 'f'
  AND NOT convalidated;
