# Modelagem do banco de dados

## 1. Visão geral

Esta proposta descreve a estrutura relacional do catálogo, da personalização, do carrinho e dos pedidos da Flor de Mandacaru, uma floricultura virtual com produtos prontos e composições florais personalizadas. O objetivo é apoiar consultas do catálogo, filtros por cor, montagem de buquês e preservação do que foi comprado.

O banco previsto pelo projeto é PostgreSQL. Este documento e os diagramas são uma proposta de modelagem; não representam migrações aplicadas nem garantem que o banco ou todas as regras estejam implementados.

## 2. Organização da modelagem

Os diagramas existentes foram atualizados sem criar cópias paralelas:

- [Diagrama Entidade-Relacionamento](./diagrama-er.puml): tabelas, chaves, associações e regras de integridade.
- [Diagrama de classes](./diagrama-classes.puml): classes do domínio, responsabilidades e associações conceituais; tabelas associativas aparecem como relações muitos-para-muitos, não como classes de implementação.

Os agrupamentos do DER organizam clientes, catálogo, cores, tamanhos/layouts, composição, carrinho/personalização e pedidos. O diagrama de classes mantém os pacotes de perfil, catálogo, composição, personalização, carrinho e pedidos.

`PRODUTO` concentra os dados comuns e é especializado por `PLANTA`, `VASO`, `BUQUE` e `ARRANJO`. Cada subtipo usa `produto_id` como PK e FK para `PRODUTO`; não duplica os dados comuns. `PAPEL` e `LACO` são opções de personalização independentes, não subtipos de produto.

## 3. Catálogo de produtos

- **Produto:** código estável e único, nome, descrição, preço, estoque e estado de ativação.
- **Planta:** informações botânicas e indicação de floração. Suas cores de flor são associações a `COR`, não um único campo textual; para plantas sem flor, não se atribuem cores de flor.
- **Vaso:** material e dimensões. Suas cores usam `VASO_COR`, sem manter um segundo atributo textual de cor.
- **Buquê:** estilo e opções padrão de papel e laço. Os tamanhos de montagem são definidos separadamente em `TAMANHO_BUQUE`.
- **Arranjo:** estilo, tamanho de catálogo e vaso padrão opcional.
- **Papel e laço:** componentes independentes, com seus próprios códigos, preços adicionais e estado de ativação. Suas cores são associações, não propriedades do material.

`ITEM_COMPOSICAO` continua representando a composição de produtos do catálogo, por exemplo, plantas que formam um buquê pronto ou arranjo. `produto_composicao_id` referencia somente `PRODUTO`; a regra de que o proprietário é do subtipo `BUQUE` ou `ARRANJO` deve ser validada pela aplicação ou por trigger. `componente_id` também referencia `PRODUTO`. Não se reutiliza uma FK como se apontasse simultaneamente para duas tabelas de subtipo.

## 4. Sistema de cores

`COR` mantém um catálogo independente com identificador, nome, código hexadecimal e descrição. Assim, a mesma cor pode ser usada por produtos, vasos, papéis e laços de materiais distintos, e uma cor pode ser cadastrada antes de ser associada a um item.

As relações N:N são:

- `PRODUTO_COR(produto_id, cor_id)` para plantas, buquês e arranjos;
- `VASO_COR(vaso_id, cor_id)` para vasos;
- `PAPEL_COR(papel_id, cor_id)` para papéis;
- `LACO_COR(laco_id, cor_id)` para laços.

Cada associação tem FKs para seus dois proprietários e PK composta pelos mesmos IDs (ou unicidade equivalente), impedindo duplicatas. Como `VASO` já especializa `PRODUTO`, `PRODUTO_COR` inclui `tipo_produto` com `CHECK` limitado a `PLANTA`, `BUQUE` e `ARRANJO`, além de FK composta `(produto_id, tipo_produto)` para `PRODUTO(id, tipo_produto)`. A chave referenciada exige `UNIQUE(id, tipo_produto)` em `PRODUTO`. Assim, uma cor de vaso só pode ser registrada em `VASO_COR`; a FK de `produto_id` sozinha não seria suficiente.

O filtro por cor consulta as associações correspondentes e une os resultados das categorias. Para plantas com flor, a cor associada representa a cor da flor, não uma variação comercial do produto. Não foi introduzida entidade de variação.

No modelo Java, a coleção de cores é herdada por todos os subtipos de `Produto`, incluindo `Vaso`. A camada futura de persistência deve rotear as cores de `Vaso` para `VASO_COR` e as dos outros subtipos para `PRODUTO_COR`; `Papel` e `Laco` mantêm coleções próprias. Essa separação pertence ao mapeamento relacional, não a uma duplicação de associações no domínio.

## 5. Sistema de imagens

Os arquivos continuam fora do banco. `IMAGEM` registra somente metadados e referências: ID interno, código de nomenclatura, proprietário, nome de arquivo, caminho relativo, finalidade, descrição alternativa, indicação de principal e ordem. O formato é derivado da extensão.

A imagem referencia exatamente um proprietário: `PRODUTO`, `PAPEL` ou `LACO`. A tabela mantém uma FK opcional para cada tipo e requer que exatamente uma esteja preenchida; cada FK continua apontando a uma tabela específica e válida. Deve existir no máximo uma imagem principal por proprietário.

As finalidades são `ICO` (ícone), `CMP` (composição), `EDI` (editorial) e `GAL` (galeria). O código da imagem não substitui seu ID de banco. Os bytes ficam no armazenamento de arquivos; o banco registra caminho relativo e nome para recuperação.

Estrutura ilustrativa, com classificação opcional quando aplicável:

```text
uploads/
├── plantas/com-flores/PLT-001/PLT-001-ICO.webp
├── vasos/VAS-001/VAS-001-ICO.webp
├── buques/BUQ-001/BUQ-001-EDI.webp
├── arranjos/ARR-001/ARR-001-EDI.webp
├── papeis/PAP-001/PAP-001-ICO.webp
└── lacos/LAC-001/LAC-001-ICO.webp
```

O nome segue `CODIGO_PRODUTO-FINALIDADE[-SEQUENCIAL].extensao`; por exemplo, `PLT-001-EDI-02.webp`. O sequencial é omitido quando há apenas uma imagem da finalidade e diferencia arquivos quando há mais de uma. A lista de formatos permitidos, as recomendações de upload, atualização, exclusão e acessibilidade estão em [docs/imagens/README.md](../imagens/README.md).

## 6. Personalização de buquês

`TAMANHO_BUQUE` define o código do tamanho e a quantidade fixa de flores:

| Código | Quantidade de flores |
|---|---:|
| PP | 1 |
| P | 5 |
| M | 7 |
| G | 15 |
| GG | 20 |

`POSICAO_BUQUE` é a configuração de layout por tamanho. Cada posição possui uma `ordem_selecao` e coordenadas físicas (`linha`, `coluna`). Ordem de seleção não é posição na grade. Devem ser únicas a ordem por tamanho e cada coordenada dentro do mesmo tamanho. O número de posições configuradas deve ser igual a `quantidade_flores`.

Para o tamanho P, a grade fornecida corresponde a:

| Ordem de seleção | Linha | Coluna |
|---:|---:|---:|
| 1 | 2 | 2 |
| 2 | 2 | 1 |
| 3 | 2 | 3 |
| 4 | 1 | 2 |
| 5 | 3 | 2 |

`PERSONALIZACAO` referencia o `ITEM_CARRINHO` e guarda o tamanho, papel e laço escolhidos (e vaso opcional, conforme o modelo atual). O domínio Java mantém a regra condicional:

- Para um buquê personalizado, o tamanho é obrigatório e cada `ItemPersonalizacao` representa uma planta, quantidade unitária e posição de grade. Uma posição não pode ser repetida e deve pertencer ao tamanho escolhido. No DER, `ITEM_PERSONALIZACAO.tamanho_buque_id` funciona como coluna-ponte para duas FKs compostas: `(personalizacao_id, tamanho_buque_id)` referencia `PERSONALIZACAO(id, tamanho_buque_id)` e `(posicao_buque_id, tamanho_buque_id)` referencia `POSICAO_BUQUE(id, tamanho_buque_id)`. As duas chaves referenciadas precisam de `UNIQUE(id, tamanho_buque_id)`, e um `CHECK` exige que `tamanho_buque_id` e `posicao_buque_id` sejam ambos nulos ou ambos preenchidos. Além disso, `UNIQUE(personalizacao_id, posicao_buque_id)` impede que a mesma posição seja preenchida duas vezes. Essas constraints preservam a correspondência entre o tamanho selecionado e o layout sem depender somente da validação da aplicação.
- Para um arranjo, tamanho e posição de buquê não se aplicam. `ItemPersonalizacao` pode agrupar a mesma planta por quantidade, preservando o comportamento já existente.

O preço unitário fica registrado no item e é multiplicado pela quantidade no caso de arranjos. Para um buquê, cada item posicionado tem quantidade 1.

A aplicação pode consultar as posições do tamanho e renderizar a grade dinamicamente. O DER define os tamanhos e quantidades; os dados completos de coordenadas para PP, M, G e GG ainda precisam ser fornecidos pelo design/interface. Não se presumiu um layout para eles.

## 7. Carrinho e pedidos

O cliente adiciona um produto ao `CARRINHO` por meio de `ITEM_CARRINHO`, que guarda quantidade e preço unitário naquele momento. A personalização permanece ligada ao item do carrinho; para buquês montados, contém tamanho, escolhas de componentes e flores por posição.

No fechamento, cada linha do pedido mantém referência opcional ao produto e cópias de código, nome, quantidade, preço unitário e subtotal. `PERSONALIZACAO_PEDIDO` guarda uma cópia estruturada dos dados relevantes de tamanho, papel, laço e vaso, inclusive códigos, nomes, quantidades e preços daquele instante. `FLOR_PEDIDO.item_pedido_id` referencia `ITEM_PEDIDO`; não referencia `PERSONALIZACAO_PEDIDO`. `FLOR_PEDIDO` preserva código/nome da planta e preço. Em buquês, registra também quantidade unitária, ordem e coordenadas; em arranjos, pode manter quantidade agrupada sem posição. As FKs para catálogo/layout são referências auxiliares; os valores copiados preservam a instrução de montagem caso o catálogo seja alterado ou removido. Assim, a personalização não se reduz a um texto livre.

`ENDERECO_PEDIDO` guarda uma cópia dos campos do endereço usados na entrega e se relaciona um a um com o pedido. Pode manter uma FK opcional para o endereço original, mas o snapshot é o registro histórico da entrega. Alterações posteriores no cadastro do cliente não devem reescrever esse snapshot. As classes Java `EnderecoPedido`, `PersonalizacaoPedido` e `FlorPedido` implementam snapshots de domínio; o schema relacional e a persistência ainda não foram implementados.

## 8. Regras de integridade

- Cada entidade tem PK; FKs apontam para uma tabela única. As FKs dos subtipos de produto também são suas PKs.
- `PRODUTO.tipo_produto` identifica `PLANTA`, `VASO`, `BUQUE` ou `ARRANJO`. Cada produto deve ter exatamente uma linha no subtipo declarado. A implementação deve validar o subtipo correspondente e a especialização total/disjunta com triggers de restrição diferidos até o fim da transação (ou CHECK/FKs compostas para a correspondência do tipo, mais validação diferida da existência).
- Códigos estáveis do catálogo e da imagem são únicos. O formato de `COR.codigo_hexadecimal` deve ser validado conforme a convenção a confirmar.
- Tabelas de associação de cor usam PK composta, impedindo associações duplicadas.
- `PRODUTO_COR` usa `CHECK` de tipos não-VASO e FK composta `(produto_id, tipo_produto)` para impedir que vasos sejam associados nessa tabela; `VASO_COR` é exclusiva de vasos.
- `IMAGEM` exige exatamente uma FK de proprietário e no máximo uma imagem principal por proprietário. A unicidade de principal pode ser aplicada por índices únicos parciais por FK, além da validação de domínio.
- `ITEM_COMPOSICAO` aponta a `PRODUTO`; o subtipo proprietário precisa ser `BUQUE` ou `ARRANJO`.
- Cada tamanho de buquê tem `quantidade_flores` posições; códigos, quantidades e coordenadas devem ser positivos/válidos. `ordem_selecao` e `(linha, coluna)` são únicos por tamanho.
- `ITEM_PERSONALIZACAO` usa FKs compostas que comparam o tamanho da personalização ao tamanho da posição, além de `UNIQUE(personalizacao_id, posicao_buque_id)` para impedir duplicação de posição.
- Para buquê personalizado, cada flor ocupa uma posição distinta e tem quantidade 1; a grade precisa estar completa no fechamento. Para arranjo, quantidade agrupada e ausência de posição são aceitas.
- Snapshot de endereço, produto e personalização é criado no domínio ao converter o carrinho em pedido; sua gravação persistente depende do futuro mapeamento de banco.
- Cardinalidades e nulabilidade mostradas nos diagramas orientam FKs e validações. Regras entre tabelas que uma FK simples não consegue garantir usam os discriminadores, CHECKs, FKs compostas ou triggers diferidos descritos acima.

## 9. Diagramas

- [DER — diagrama-er.puml](./diagrama-er.puml)
- [Classes — diagrama-classes.puml](./diagrama-classes.puml)

Os arquivos usam PlantUML. Podem ser abertos com a extensão PlantUML do editor ou renderizados localmente com o CLI `plantuml docs/modelagem/diagrama-er.puml docs/modelagem/diagrama-classes.puml`. O DER mostra a estrutura relacional; o diagrama de classes mostra o domínio e não reproduz as tabelas associativas como classes.

## 10. Decisões arquiteturais

### Consolidadas nesta proposta

- PostgreSQL é o banco previsto pelo projeto; não há migração ou schema implantado nesta entrega.
- `Produto` e seus quatro subtipos mantêm a especialização existente; `tipo_produto` indica o subtipo e a implementação deve garantir correspondência total e disjunta.
- Cores são um catálogo independente, associado por relações N:N. `VASO_COR` é específico de vasos e `PRODUTO_COR` exclui `VASO` por `CHECK` e FK composta.
- Imagens mantêm a estrutura já definida: arquivos fora do banco, metadados relacionais, caminhos relativos e exatamente um proprietário entre produto, papel e laço.
- Tamanhos determinam quantidades fixas de posições; buquês personalizados usam uma flor por posição, enquanto arranjos preservam a quantidade agrupada sem posições.
- O domínio cria snapshots estruturados de valores do catálogo, personalização e endereço ao formar o pedido; a persistência desses snapshots ainda depende da implementação do schema/mapeamento.

### Pendências para validação de negócio/design

1. **Layouts:** fornecer as coordenadas de PP, M, G e GG; somente P tem disposição especificada. Aprovar também o critério de início/limite de linhas e colunas.
2. **Tamanhos de catálogo:** confirmar se buquês prontos também têm uma classificação comercial de tamanho distinta do `TamanhoBuque` usado na montagem. O Java removeu o `String tamanho` livre de `Buque`; se necessário, o atributo de catálogo deve ter significado/nome próprio.
3. **Seleção de plantas:** definir quais subtipos/estados de produto podem ocupar cada posição e se uma planta pode ser escolhida mais de uma vez no mesmo buquê. O modelo atual permite repetir a mesma planta em posições diferentes.
4. **Preço dos componentes:** confirmar se os valores de papel, laço, vaso e flores são somados ao preço do produto e se `valor_adicional` deve ser agregado ou derivado. Os snapshots propostos preservam preços individuais e total adicional no pedido.
5. **Remoção de catálogo:** decidir a política de exclusão/arquivamento das entidades referenciadas por pedidos. Os IDs no snapshot são opcionais para que o histórico não dependa de a linha do catálogo continuar existindo.
6. **Formato das cores:** definir se `codigo_hexadecimal` terá formato canônico `#RRGGBB`, se aceitará alfa (`#RRGGBBAA`) e se códigos ou nomes devem ser únicos.
7. **Foto principal e snapshots de imagem:** esta proposta preserva metadados e regra de principal no catálogo existente; ainda não define se pedidos precisam congelar também imagem ou URL exibida na compra.

Esses pontos devem ser confirmados antes de converter o DER em migrações. Os diagramas não implicam que regras ou funcionalidades pendentes já estejam implementadas.

## 11. Sincronização entre o modelo Java e os diagramas

As classes de domínio foram alinhadas ao [diagrama de classes](./diagrama-classes.puml) para cores, tamanhos/posições, personalização condicional e snapshots de pedido. A validação foi feita por compilação e testes unitários; esta mudança não implementa schema, mapeamento ORM, repositórios nem uma conexão real com PostgreSQL.

### Elementos atualmente coerentes

- **Herança de produto:** `Produto` é abstrata e `Planta`, `Vaso`, `Buque` e `Arranjo` a estendem. Os quatro subtipos definem seus prefixos de código. Essa hierarquia corresponde ao diagrama e ao DER.
- **Composição de catálogo:** `Buque` e `Arranjo` implementam `Componivel` e mantêm uma composição de `ItemComposicao`, que referencia um `Produto`. Isso é coerente com a associação de domínio; no DER, a FK do proprietário aponta somente a `PRODUTO`, com regra de subtipo `BUQUE` ou `ARRANJO`.
- **Imagens:** `Imagem` representa metadados, deriva o formato do nome do arquivo e não lê nem armazena bytes. `Produto`, `Papel` e `Laco` mantêm coleções de imagens; `ImagemAssociavel` expressa o proprietário exclusivo. O enum `FinalidadeImagem` contém as quatro finalidades e seus códigos. Esse núcleo está alinhado aos diagramas.
- **Cores:** `Cor` é associada por coleções de domínio. `Produto` centraliza as cores herdadas pelos subtipos; `Papel` e `Laco` têm coleções próprias.
- **Tamanho e layout:** `TamanhoBuque` determina as quantidades PP/P/M/G/GG; `PosicaoBuque` mantém a ordem e coordenadas, com validação de unicidade e layout completo.
- **Personalização:** `Personalizacao` referencia opcionalmente o tamanho. `ItemPersonalizacao` mantém quantidade agregada sem posição para arranjo e exige quantidade 1/posição única quando usado por buquê.
- **Pedidos e endereço:** `ItemPedido`, `PersonalizacaoPedido`, `FlorPedido` e `EnderecoPedido` copiam os valores relevantes para snapshots; o fluxo do pedido rejeita buquê personalizado com grade incompleta.
- **Pacotes:** as classes de domínio estão em `model`, o enum em `enums`, interfaces em `interfaces` e a conexão JDBC em `infrastructure.data`. O domínio consultado não depende de JDBC; essa separação deve ser mantida.

### Divergências e propostas de adaptação

| Conceito | Estado atual no Java | Observação |
|---|---|---|
| **Cores** | `Cor` guarda nome, código hexadecimal e descrição. `Produto`, `Papel` e `Laco` expõem coleções encapsuladas de cores; `Planta` remove suas cores quando passa a não possuir flor e impede novas associações nesse estado. | A coleção é de domínio e não duplica classes de junção. O mapeamento futuro deve direcionar `Vaso` para `VASO_COR`, os outros produtos para `PRODUTO_COR`, e opções para suas relações próprias. |
| **Tamanho/layout** | `TamanhoBuque` determina os cinco códigos/quantidades; `PosicaoBuque` guarda ordem, linha e coluna. A configuração verifica ordens/coordenadas duplicadas e permite validar layout completo. | Coordenadas dos tamanhos além do exemplo P ainda são dados de configuração, não existem como seed nesta implementação. |
| **Personalização** | Buquê exige tamanho, papel e laço; cada flor selecionada ocupa uma posição e quantidade 1. Arranjo mantém agrupamento por planta/quantidade e não usa tamanho ou posição de buquê. O carrinho permite grade parcial; o fechamento do pedido exige todas as posições. | Essa regra condicional preserva o comportamento de arranjo e o requisito de composição posicional do buquê. |
| **Pedido e histórico** | `ItemPedido` armazena código/nome/preço de catálogo e snapshot opcional de personalização. `PersonalizacaoPedido` e `FlorPedido` copiam as opções e flores, incluindo coordenadas/ordem no buquê e quantidade no arranjo. | Referências para entidades mutáveis continuam auxiliares; os campos `Snapshot` preservam os dados copiados. A persistência ainda precisa mapear esses objetos às tabelas do DER. |
| **Endereço do pedido** | `Pedido` cria um `EnderecoPedido` imutável com cópia dos campos e mantém referência ao endereço de origem. | O snapshot permanece estável após alterações posteriores no objeto `Endereco`; a persistência de referência opcional é futura. |

### Organização Java recomendada para a evolução

As classes de domínio `Cor`, `TamanhoBuque`, `PosicaoBuque`, `EnderecoPedido`, `PersonalizacaoPedido` e `FlorPedido` estão em `model`. `FinalidadeImagem` permanece no pacote `enums`; conexão JDBC fica em `infrastructure.data`. As tabelas associativas de cores não foram convertidas em classes Java.

O projeto mantém classes de domínio Java simples: não há mapeamentos JPA nem repositórios para os conceitos atualizados. A compilação do domínio não comprova conectividade com o banco; para isso ainda são necessários schema/migrações, mapeamento de persistência e teste com PostgreSQL acessível.

### Decisões que ainda exigem confirmação

1. Se tamanhos de `Buque` pronto são uma propriedade comercial separada dos tamanhos disponíveis na personalização.
2. Como o preço das flores e opções é congelado no carrinho e no pedido, inclusive quando a mesma planta ocupa mais de uma posição.
3. Se snapshots do pedido devem copiar apenas código, nome, posição e preços, ou também outros dados que o atendente necessite para montagem.
4. Se o vínculo persistido com o endereço original permanece quando um endereço de cliente é removido; o snapshot histórico é criado em qualquer alternativa.

Esta atualização trata somente do modelo Java de domínio. A conexão JDBC já existente não foi alterada e a compilação/testes de domínio não garantem que um PostgreSQL esteja disponível ou que haja schema implantado.
