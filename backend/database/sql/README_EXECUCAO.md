# Banco de dados — Flor de Mandacaru

Scripts SQL para PostgreSQL, derivados do DER atualizado da Flor de Mandacaru.

## Pré-requisitos

- PostgreSQL instalado e em execução.
- Banco `flor_de_mandacaru` já criado.
- Acesso ao banco pelo pgAdmin 4 ou `psql`.

## Ordem de execução

Execute os arquivos no banco `flor_de_mandacaru`:

1. `01_schema.sql` — cria as tabelas, PKs, UNIQUEs e CHECKs.
2. `02_relacionamentos.sql` — cria as chaves estrangeiras.
3. `03_integridade.sql` — cria índices e triggers de integridade.
4. `04_dados_iniciais.sql` — cadastra os tamanhos e o layout P.
5. `05_validacao.sql` — consultas de conferência.

## Atenção sobre os layouts

O DER define as quantidades PP=1, P=5, M=7, G=15 e GG=20, mas só informa as coordenadas do layout P. Os layouts dos demais tamanhos ainda dependem da definição visual da interface.

O banco impede que um layout ultrapasse a quantidade de flores definida. A exigência de que o layout esteja completo deve ser validada no fluxo de personalização/fechamento do pedido. Assim, é possível cadastrar os tamanhos e avançar no desenvolvimento sem inventar coordenadas para PP, M, G e GG.

O script 04 cadastra os cinco tamanhos e somente as cinco posições do tamanho P, cujas coordenadas já foram definidas no DER. Os demais layouts ficam pendentes de definição visual.

## Observações de implementação

- `imagem.formato` é uma coluna gerada a partir da extensão de `nome_arquivo`.
- As FKs de catálogo nos snapshots de pedido são opcionais para preservar o histórico mesmo se um registro de catálogo for removido.
- `produto` e suas tabelas de subtipo são validados por constraint triggers diferidos. Insira o registro em `produto` e o respectivo subtipo dentro da mesma transação.
- `item_composicao` aceita como proprietário somente produtos do tipo BUQUE ou ARRANJO.
- A personalização no carrinho aceita somente produtos BUQUE ou ARRANJO. Buquês exigem tamanho; arranjos não utilizam tamanho de buquê.
- A validação de que todas as posições de um buquê foram preenchidas antes de fechar o pedido também deve ser chamada no fluxo transacional da aplicação.
- A tabela `flor_pedido` referencia `item_pedido`, conforme o DER atualizado, e guarda os dados de snapshot necessários para reconstruir a composição.
- As exclusões em cascata/restritivas foram escolhidas para preservar dependências e histórico; revise-as caso a política de exclusão da aplicação mude.

## Como executar no pgAdmin 4

1. Abra o pgAdmin.
2. Selecione `Servers > PostgreSQL > Databases > flor_de_mandacaru`.
3. Clique com o botão direito no banco e abra **Query Tool**.
4. Abra um arquivo `.sql` pelo botão de abrir arquivo.
5. Execute com F5 ou pelo botão **Execute**.
6. Confira a aba **Messages** para identificar `COMMIT` ou mensagens de erro.

Execute um arquivo por vez e só avance quando o anterior terminar sem erro.

## Limpeza para recomeçar

Como o banco está vazio, os scripts podem ser executados na ordem acima. Se você precisar recomeçar depois de criar tabelas, não execute `DROP SCHEMA public CASCADE` sem confirmar que não há dados que deseja preservar.
