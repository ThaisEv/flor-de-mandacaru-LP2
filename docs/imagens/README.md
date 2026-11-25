# Organização e gerenciamento de imagens

Este documento define como o catálogo da Flor de Mandacaru identifica, organiza e disponibiliza imagens de produtos e de opções de personalização.

## Objetivo e arquitetura

Os arquivos de imagem ficam no armazenamento de arquivos, não como BLOB no banco. O banco guarda os metadados e referências necessários para localizar cada arquivo; a aplicação valida os metadados e associa a imagem a um produto, papel ou laço. Essa separação mantém o banco dedicado a dados relacionais e permite servir, fazer backup e substituir arquivos por meio da infraestrutura de armazenamento.

- **Banco de dados:** ID interno da imagem, código de nomenclatura, proprietário, nome do arquivo, caminho relativo, finalidade, texto alternativo e ordem. O formato é derivado da extensão do arquivo.
- **Armazenamento de arquivos:** bytes das imagens em diretórios organizados por categoria, classificação opcional e código do item.
- **Aplicação:** cadastro e validação de metadados, associação ao proprietário e seleção da imagem adequada ao contexto. A entidade `Imagem` não faz upload nem acessa o disco.

A tabela `IMAGEM` tem FKs opcionais para `PRODUTO`, `PAPEL` e `LACO`; uma restrição exige que exatamente uma delas esteja preenchida. Assim, cada referência tem integridade referencial, sem uma FK polimórfica. O ID da imagem no banco é independente do código legível, como `PLT-001-EDI`.

## Estrutura de pastas

O caminho é relativo à raiz de armazenamento configurada pela aplicação. A classificação só aparece quando fizer sentido para a categoria; não é necessário criar uma subpasta artificial:

```text
uploads/
├── plantas/
│   └── com-flores/                 # classificação opcional
│       ├── PLT-001/
│       │   ├── PLT-001-ICO.webp
│       │   ├── PLT-001-CMP.webp
│       │   ├── PLT-001-EDI.webp
│       │   ├── PLT-001-EDI-02.webp
│       │   └── PLT-001-GAL-01.webp
│       └── PLT-002/
│           ├── PLT-002-ICO.webp
│           └── PLT-002-CMP.webp
├── vasos/
│   └── VAS-001/
├── buques/
│   └── BUQ-001/
├── arranjos/
│   └── ARR-001/
├── papeis/
│   └── PAP-001/
└── lacos/
    └── LAC-001/
```

Os prefixos são próprios de cada categoria: `PLT` (planta), `VAS` (vaso), `BUQ` (buquê), `ARR` (arranjo), `PAP` (papel) e `LAC` (laço). A categoria é determinada pelo tipo do item e seu código, nunca inferida apenas pelo nome da pasta. Cada proprietário tem um código estável e único; imagens de um mesmo item ficam na mesma pasta.

## Nomenclatura e finalidades

O nome segue `CODIGO_PRODUTO-FINALIDADE[-SEQUENCIAL].extensao`. O código identifica o proprietário, a finalidade identifica o contexto de uso e o sequencial diferencia várias imagens da mesma finalidade. O sequencial é omitido quando há uma única imagem daquela finalidade e obrigatório quando há mais de uma; começa em `01`.

| Finalidade | Código | Uso |
|---|---|---|
| `ICONE` | `ICO` | Ícones e menus de seleção |
| `COMPOSICAO` | `CMP` | Montagem de buquês e arranjos |
| `EDITORIAL` | `EDI` | Catálogo e página de apresentação |
| `GALERIA` | `GAL` | Fotografias complementares |

Exemplos: `PLT-001-ICO.webp`, `PLT-001-CMP.webp`, `PLT-001-EDI.webp`, `PLT-001-EDI-02.webp`, `PLT-001-GAL-01.webp`, `VAS-001-ICO.webp`, `BUQ-001-EDI.webp` e `ARR-001-EDI.webp`. Uma imagem de papel ou laço usa, por exemplo, `PAP-001-ICO.webp` ou `LAC-001-ICO.webp`.

O código do arquivo é distinto do `id` técnico da tabela `IMAGEM`. A extensão também não substitui a finalidade: uma fotografia `GAL` não deve ser usada como imagem `ICO` só por compartilhar o mesmo arquivo.

## Metadados e recuperação

`caminho` guarda o caminho **relativo da pasta**, por exemplo `uploads/plantas/com-flores/PLT-001`; `nome_arquivo` guarda `PLT-001-EDI.webp`. A aplicação combina esses valores com a raiz de armazenamento configurada. Não se registram caminhos absolutos da máquina, nem se armazenam bytes no banco.

O domínio permite, para cada item:

- adicionar e remover imagens, sem permitir a associação do mesmo código duas vezes ao mesmo item;
- consultar a lista e filtrar por finalidade;
- recuperar a imagem principal, o ícone, a imagem de composição, a editorial ou a galeria;
- manter no máximo uma imagem principal por proprietário;
- cadastrar o item sem imagens ou sem todas as finalidades.

`FinalidadeImagem` mantém os códigos `ICO`, `CMP`, `EDI` e `GAL` explicitamente; nomes de arquivos não dependem do nome Java do enum. Para cada combinação de item e finalidade, use um nome sem sequencial quando houver só uma imagem ou números únicos (`-01`, `-02`, ...) quando houver várias. Os metadados mantêm também a ordem de exibição.

## Fluxo de cadastro, atualização e exclusão

1. O administrador cadastra o item no catálogo.
2. A aplicação identifica seu tipo e, quando aplicável, sua classificação.
3. O item recebe um código estável e único, como `PLT-001` ou `PAP-001`.
4. A aplicação cria ou identifica a pasta da categoria/classificação/código.
5. Os arquivos validados são gravados nessa pasta com nomes gerados conforme o código e a finalidade.
6. Os metadados são persistidos com o proprietário correto, caminho relativo, nome, finalidade, texto alternativo, indicador principal e ordem.
7. O frontend solicita os metadados adequados ao contexto: ícone no seletor, composição durante a montagem, editorial no catálogo e galeria na página do item.

Na substituição, grave e valide o novo arquivo antes de atualizar a referência; remova o arquivo anterior somente depois que a nova referência estiver confirmada. Na exclusão, remova os metadados e o arquivo de forma coordenada, registrando falhas de armazenamento para reconciliação em vez de deixar uma operação parcialmente concluída em silêncio. Uma mudança de finalidade ou nome deve atualizar arquivo e metadados juntos, preservando a unicidade.

## Boas práticas e segurança

- Aceitar somente `webp`, `png`, `jpg` e `jpeg`, verificando extensão **e** conteúdo/MIME; impor limites de tamanho e dimensões e decodificar a imagem para validar que é íntegra.
- Gerar o nome no servidor a partir dos códigos validados; nunca usar diretamente nomes de arquivo recebidos do cliente. Recusar nomes duplicados em vez de sobrescrever silenciosamente.
- Validar que caminhos são relativos, ficam sob a raiz configurada e não contêm segmentos `.`/`..`, separadores inesperados ou componentes absolutos.
- Armazenar um texto alternativo (`alt`) descritivo para acessibilidade; não usar o nome do arquivo como substituto automático.
- Escolher uma única imagem principal por proprietário, sem exigir que ela exista durante o cadastro inicial.
- Tratar arquivos ausentes como erro de integridade: registrar e sinalizar a falha, permitir correção/reenvio e não retornar uma referência inexistente como se fosse válida.
- Restringir uploads a administradores autenticados, limitar taxa e tamanho, validar conteúdo real, armazenar fora da raiz executável da aplicação e servir conteúdo com tipo seguro. Não aceitar caminhos, extensões ou metadados de confiança do cliente.
- Fazer backup dos arquivos e dos metadados; a recuperação precisa preservar as referências entre ambos.

## Escopo atual

O modelo Java e os diagramas definem metadados e associações; a aplicação ainda não implementa upload, exclusão física, serviço de arquivos, endpoint ou integração com armazenamento local/nuvem. A raiz `uploads/` é uma convenção documentada, não um diretório de desenvolvimento obrigatório nem um caminho absoluto embutido no código.
