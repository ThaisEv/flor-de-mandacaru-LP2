# 🌵🌺 Flor de Mandacaru

> *Nossa essência floresce de onde viemos.*

**Flor de Mandacaru** é uma floricultura online de inspiração nordestina. Aqui vive o código da loja virtual: um e-commerce que transforma flores em experiências de afeto, conexão e expressão, com a identidade do Nordeste no centro, sem estereótipos e sem clichês.

**Conceito central:** Nordeste que floresce.

> 📚 **Projeto acadêmico** da disciplina de Linguagem de Programação 2 (LP 2). A marca e a loja são fictícias: o foco aqui é a **implementação** (POO, Spring Boot, API REST e persistência de dados).

---

## 🌿 Sobre a marca

O mandacaru representa resistência, beleza, adaptação e florescimento. Mesmo em ambientes desafiadores, ele revela flores de enorme delicadeza. É essa a simbologia que guia o projeto: encontrar beleza nas próprias raízes.

| | |
|---|---|
| **Vibrante** | cores expressivas e composições marcantes |
| **Elegante** | equilíbrio, sofisticação e cuidado com os detalhes |
| **Afetuosa** | comunicação acolhedora, sensível e próxima |
| **Autêntica** | identidade nordestina verdadeira |
| **Contemporânea** | visual atual e navegação intuitiva |

Alegre sem ser infantil, sofisticada sem ser distante, regional sem ser folclórica.

---

## 🎨 Identidade visual (resumo para quem vai codar)

**Paleta principal**

| Cor | Uso |
|---|---|
| 🌸 Rosa-hibisco | intensidade e delicadeza |
| 🪸 Vermelho-coral | paixão e energia |
| 🟠 Laranja | alegria e calor |
| 🌼 Amarelo-solar | luz e otimismo |
| 🌿 Verde-folhagem | natureza e frescor |

**Apoio:** creme, marfim, bege-areia, tons de palha e madeira.

---

## 🛠️ Tecnologias

| Camada | Tecnologias |
|---|---|
| **Frontend** | HTML5, CSS3 (responsivo, animações), JavaScript (interatividade, validação, carrinho) |
| **Backend** | Java, Spring Boot, Spring Data JPA, Hibernate |
| **Comunicação** | APIs REST |
| **Banco de dados** | PostgreSQL |
| **Ferramentas** | Visual Studio Code, Git, GitHub |

### Arquitetura

```
┌──────────────┐   REST (JSON)   ┌─────────────────────┐   JPA/Hibernate   ┌────────────┐
│   Frontend   │ ◄─────────────► │  Backend            │ ◄───────────────► │ PostgreSQL │
│ HTML/CSS/JS  │                 │  Java + Spring Boot │                   │            │
└──────────────┘                 └─────────────────────┘                   └────────────┘
 apresentação e                   regras de negócio e                       armazenamento
 interação                        processamento                             persistente
```

### Entidades previstas

Produtos · Flores · Buquês · Arranjos · Plantas · Clientes · Usuários · Pedidos · Itens dos pedidos · Pagamentos

### Categorias da loja

Buquês, arranjos florais, flores individuais, plantas ornamentais, vasos, presentes, kits especiais e composições personalizadas.

---

## 📁 Estrutura sugerida do repositório

```
flor-de-mandacaru/
├── backend/                # Java + Spring Boot
│   └── src/main/java/...   # controller, service, repository, model
├── frontend/               # HTML, CSS e JS
│   ├── assets/             # imagens, ícones, fontes
│   ├── css/
│   └── js/
├── docs/                   # contexto da marca, modelagem, diagramas
└── README.md
```

> Ajuste a estrutura conforme o time decidir. Esta é só uma sugestão de partida.

---

## 🚀 Como rodar o projeto

### Pré-requisitos

- JDK (versão definida pelo time)
- Maven ou Gradle
- PostgreSQL
- Git

### Passo a passo

```bash
# 1. Clone o repositório
git clone https://github.com/ThaisEv/flor-de-mandacaru-LP2.git
cd flor-de-mandacaru

# 2. Crie o banco no PostgreSQL
createdb flor_de_mandacaru

# 3. Configure as credenciais em
#    backend/src/main/resources/application.properties

# 4. Suba o backend
cd backend
./mvnw spring-boot:run

# 5. Abra o frontend (ex.: Live Server no VS Code)
```

> ⚠️ Nunca suba senhas ou chaves para o GitHub. Use variáveis de ambiente.

---

## ✅ Checklist de criação do site

### 1. Planejamento e base
- [ ] Criar o documento da idealização da marca e Checkpoints (`docs/`)
- [ ] Definir o escopo do MVP, priorizando o que a disciplina exige
- [ ] Criar o repositório no GitHub e o `.gitignore`
- [x] Escrever o README inicial

### 2. Identidade visual e design
- [ ] Definir tokens de design (cores da paleta, tipografia, espaçamentos)
- [ ] Escolher tipografia com personalidade (elegante, legível e acolhedora)
- [ ] Criar logotipo e símbolo (reconhecíveis também em tamanhos pequenos)
- [ ] Prototipar as telas principais (Figma ou similar)
- [ ] Definir direção fotográfica e banco de imagens próprio
- [ ] Validar o protótipo com o time

### 3. Modelagem de dados
- [ ] Levantar requisitos funcionais e não funcionais
- [ ] Modelar entidades e relacionamentos (diagrama ER)
- [ ] Criar o banco PostgreSQL
- [ ] Definir regras de integridade (chaves, restrições, índices)
- [ ] Preparar dados de teste (sem preços reais inventados)

### 4. Backend (Java + Spring Boot)
- [ ] Criar o projeto Spring Boot (Web, Data JPA, PostgreSQL Driver)
- [ ] Configurar conexão com o banco
- [ ] Criar entidades JPA (Produto, Flor, Buquê, Arranjo, Planta, Cliente, Usuário, Pedido, ItemPedido, Pagamento)
- [ ] Criar repositories (Spring Data JPA)
- [ ] Implementar services com as regras de negócio
- [ ] Criar controllers e endpoints REST
- [ ] Tratar erros e validar dados de entrada
- [ ] Implementar autenticação e cadastro de usuários
- [ ] Implementar fluxo de pedidos e pagamentos
- [ ] Configurar CORS para o frontend
- [ ] Documentar a API (ex.: Swagger/OpenAPI)

### 5. Frontend (HTML, CSS, JS)
- [ ] Estruturar as páginas: Home, Catálogo, Produto, Carrinho, Checkout, Login/Cadastro, Meus pedidos, Sobre
- [ ] Aplicar a identidade visual em CSS
- [ ] Garantir responsividade (mobile first)
- [ ] Implementar listagem e filtro de produtos por categoria
- [ ] Implementar carrinho (adicionar, remover, alterar quantidade)
- [ ] Validar formulários no cliente
- [ ] Integrar o frontend com a API REST
- [ ] Adicionar animações e microinterações sem pesar a página
- [ ] Revisar textos com o tom de voz da marca

### 6. Conteúdo
- [ ] Fotografar ou produzir imagens dos produtos
- [ ] Escrever descrições afetuosas e sem frases genéricas
- [ ] Revisar o texto para evitar estereótipos e regionalismos forçados
- [ ] Criar a página institucional "Sobre a marca"

### 7. Qualidade
- [ ] Testes unitários das regras de negócio
- [ ] Testes dos endpoints REST
- [ ] Testes manuais do fluxo completo de compra
- [ ] Testar em diferentes navegadores e dispositivos
- [ ] Revisar acessibilidade (contraste, alt nas imagens, navegação por teclado)
- [ ] Revisar segurança (senhas criptografadas, validações, dados sensíveis)

### 8. Entrega da disciplina
- [ ] Revisar se os conceitos de POO estão claros no código (encapsulamento, herança, polimorfismo, interfaces)
- [ ] Gerar o diagrama de classes e o diagrama ER atualizados em `docs/`
- [ ] Garantir que o projeto rode do zero seguindo apenas o README
- [ ] Preparar a demonstração do fluxo de compra
- [ ] Preparar a apresentação e dividir as falas

### 9. Extras (opcional, se sobrar tempo)
- [ ] Deploy em uma hospedagem gratuita
- [ ] Documentação da API com Swagger/OpenAPI
- [ ] Testes automatizados adicionais

---

## 👥 Equipe

<!-- Thais Evelin Barbosa da Silva (ThaisEv) -->

---

<p align="center">
  <b>FLOR DE MANDACARU</b><br>
  <i>Nossa essência floresce de onde viemos.</i> 🌵
</p>
