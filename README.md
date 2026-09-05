🔧 Maintenance Management System

Sistema de gerenciamento de manutenções desenvolvido em Java 17, com persistência em MySQL via JDBC puro, arquitetura em camadas, regras de negócio, tratamento de exceções, transações e testes automatizados.

A aplicação permite administrar empresas, equipamentos, usuários e manutenções por meio de uma interface de console. Esta primeira versão foi construída sem Spring e sem ORM de forma intencional, com o objetivo de compreender os mecanismos que frameworks como Spring Boot e Hibernate abstraem.

Status: V1 JDBC funcional, com aplicação de console e suíte automatizada cobrindo domínio, serviços e persistência. A próxima evolução planejada é a migração para Spring Boot e API REST.

🎯 Objetivo do projeto

Mais do que implementar um CRUD, o objetivo deste projeto é praticar fundamentos importantes de desenvolvimento Back-End Java em um sistema com regras de negócio e persistência real.

Durante o desenvolvimento foram trabalhados conceitos como:

modelagem de domínio e orientação a objetos;

arquitetura em camadas;

padrão DAO;

JDBC e SQL parametrizado;

mapeamento manual entre banco e objetos Java;

relacionamentos e integridade referencial;

transações com commit e rollback;

tradução de exceções entre camadas;

prevenção de operações incompatíveis com o estado das entidades;

identificação e redução de consultas N+1;

injeção de dependências por construtor;

composição manual das dependências na classe Main;

testes unitários com JUnit 5 e Mockito;

testes de integração dos DAOs com MySQL;

análise de cobertura com JaCoCo.

✨ Funcionalidades

Empresas

cadastrar, buscar, listar, atualizar e excluir empresas;

controlar status ATIVADA e DESATIVADA;

garantir unicidade de CNPJ;

impedir exclusões que violem relacionamentos existentes.

Equipamentos

cadastrar equipamentos vinculados a uma empresa;

buscar, listar, atualizar e excluir equipamentos;

garantir unicidade do código patrimonial;

validar a empresa responsável;

impedir exclusão quando houver manutenção associada incompatível com a operação.

Usuários

O domínio utiliza herança e polimorfismo para representar três tipos de usuário:

Administrador — possui departamento;

Gestor — possui área responsável;

Tecnico — possui especialidade e pode ser responsável por manutenções.

Operações disponíveis:

cadastro;

busca por ID;

listagem;

atualização;

exclusão;

persistência dos dados comuns e específicos em tabelas relacionadas;

transações JDBC nas operações que envolvem mais de uma tabela;

proteção contra alteração indevida do tipo de usuário;

unicidade de e-mail.

Manutenções

cadastro de manutenção PREVENTIVA ou CORRETIVA;

associação com equipamento e técnico responsável;

listagem geral e por status;

busca por ID;

atualização enquanto estiver em andamento;

finalização com data final e custo;

cancelamento;

exclusão de manutenções já finalizadas ou canceladas;

tratamento de data_fim opcional;

bloqueio de operações incompatíveis com o estado atual.

Estados possíveis:

ANDAMENTO → CONCLUIDA
     └────→ CANCELADA

🖥️ Aplicação de console

A V1 possui uma interface de linha de comando organizada em menus específicos para cada módulo.

========================================================================
        SISTEMA DE GERENCIAMENTO DE MANUTENÇÕES - V1 JDBC
========================================================================

MENU PRINCIPAL
1 - Usuários
2 - Empresas
3 - Equipamentos
4 - Manutenções
0 - Sair

A classe Main funciona como composition root da aplicação: nela são criados os DAOs, Services, componentes de entrada e menus, e as dependências são conectadas por construtor.

Main
 ├── DAOs
 │    ├── UsuarioDAO
 │    ├── EmpresaDAO
 │    ├── EquipamentoDAO
 │    └── ManutencaoDAO
 │
 ├── Services
 │    ├── GestaoUsuario
 │    ├── GestaoEmpresa
 │    ├── GestaoEquipamento
 │    └── GestaoManutencao
 │
 └── ConsoleApplication
      ├── UsuarioMenu
      ├── EmpresaMenu
      ├── EquipamentoMenu
      └── ManutencaoMenu

Isso mantém as classes desacopladas de implementações criadas internamente e facilita principalmente a substituição das dependências por mocks durante os testes.

🏗️ Arquitetura

src/main/java/com/joao/empresa
├── app
│   ├── ConsoleApplication.java
│   └── console
│       ├── ConsoleInput.java
│       ├── UsuarioMenu.java
│       ├── EmpresaMenu.java
│       ├── EquipamentoMenu.java
│       └── ManutencaoMenu.java
├── dao
│   ├── UsuarioDAO.java
│   ├── EmpresaDAO.java
│   ├── EquipamentoDAO.java
│   └── ManutencaoDAO.java
├── database
│   ├── ConnectionFactory.java
│   └── TradutorSQLException.java
├── exceptions
├── model
├── services
└── Main.java

Fluxo simplificado:

Console / Main
      ↓
   Services
      ↓
     DAOs
      ↓
     JDBC
      ↓
    MySQL

Responsabilidades

Camada

Responsabilidade

model

Entidades, estado e invariantes do domínio

services

Casos de uso e coordenação das regras da aplicação

dao

SQL, persistência e reconstrução dos objetos

database

Conexões e tradução de erros do JDBC

app

Interação com o usuário e fluxo da aplicação de console

exceptions

Exceções específicas de persistência e negócio

🗄️ Modelo de dados

O banco utiliza MySQL/InnoDB e relacionamentos protegidos por chaves estrangeiras.

Empresa
   └── Equipamento
          └── Manutencao
                 └── Tecnico

Usuario
   ├── Administrador
   ├── Gestor
   └── Tecnico

Principais restrições de integridade:

PRIMARY KEY;

FOREIGN KEY;

UNIQUE;

NOT NULL;

ON DELETE RESTRICT;

ON DELETE CASCADE;

ON UPDATE CASCADE.

Campos protegidos por unicidade incluem:

e-mail do usuário;

CNPJ da empresa;

código patrimonial do equipamento.

O schema principal está em:

src/main/resources/schema.sql

🔄 Transações JDBC

Algumas operações precisam modificar mais de uma tabela. O cadastro de um usuário, por exemplo, envolve a tabela geral usuario e uma tabela específica conforme seu tipo.

INSERT usuario
      ↓
INSERT administrador / gestor / tecnico
      ↓
COMMIT

Se qualquer etapa falhar:

INSERT usuario
      ↓
falha na segunda operação
      ↓
ROLLBACK

Dessa forma, a operação é tratada como uma única unidade e o banco não fica em um estado parcialmente atualizado.

⚠️ Tratamento e tradução de exceções

Erros técnicos de banco não são propagados diretamente até as camadas superiores. A aplicação traduz exceções conforme o nível de abstração.

Exemplo de duplicidade de CNPJ:

MySQL
  ↓
SQLException
  ↓
TradutorSQLException
  ↓
RegistroDuplicadoException
  ↓
GestaoEmpresa
  ↓
EmpresaJaCadastradaException

Entre as exceções próprias do projeto estão:

PersistenciaException
RegistroDuplicadoException
IntegridadeReferencialException

UsuarioNaoEncontradoException
EmpresaNaoEncontradaException
EquipamentoNaoEncontradoException
ManutencaoNaoEncontradaException

UsuarioJaCadastradoException
EmpresaJaCadastradaException
EquipamentoJaCadastradoException

EntidadeEmUsoException

Também são utilizadas exceções padrão quando representam melhor o problema:

IllegalArgumentException para argumentos inválidos;

IllegalStateException para operações incompatíveis com o estado atual da entidade.

⚡ Persistência e performance

A camada DAO utiliza diretamente recursos do JDBC, incluindo:

Connection;

PreparedStatement;

ResultSet;

RETURN_GENERATED_KEYS;

JOIN;

parâmetros SQL;

tratamento explícito de NULL;

controle manual de transações.

Durante a evolução do projeto também foram identificados cenários de N+1 queries. Algumas consultas foram reorganizadas utilizando JOIN, reduzindo acessos desnecessários ao banco e tornando explícita a preocupação com o custo das operações de persistência.

🧪 Testes automatizados

O projeto possui atualmente 178 métodos de teste distribuídos entre domínio, Services e DAOs.

Testes de domínio

Cobrem, entre outros pontos:

validações de entidades;

equals() e hashCode();

atualização de dados;

associações entre objetos;

regras e transições de estado de manutenção.

Testes de Services

Utilizam JUnit 5 + Mockito para isolar a camada de aplicação dos DAOs e validar:

cenários de sucesso;

entidades inexistentes;

duplicidades;

argumentos inválidos;

estados inválidos;

tradução de exceções;

interações esperadas com as dependências.

Testes dos DAOs

São testes de integração com um banco MySQL separado:

manutencao_test_db

O schema utilizado pelos testes está em:

src/test/resources/schema-test.sql

Para executar a suíte completa:

mvn test

Para gerar também o relatório do JaCoCo:

mvn verify

O relatório é gerado em:

target/site/jacoco/index.html

Os testes dos DAOs precisam de uma instância local do MySQL e do banco manutencao_test_db criado previamente. Na configuração atual, esses testes utilizam root / root como credenciais locais.

🛠️ Tecnologias

Tecnologia

Uso

Java 17

Linguagem principal

Maven

Build e dependências

JDBC

Persistência sem ORM

MySQL

Banco de dados relacional

MySQL Connector/J

Driver JDBC

JUnit 5

Testes automatizados

Mockito

Mocks e isolamento de dependências

JaCoCo

Cobertura de testes

Git

Controle de versão

GitHub

Hospedagem e documentação do projeto

▶️ Como executar

Pré-requisitos

Java 17 ou superior;

Maven;

MySQL 8.x;

Git, caso queira clonar o repositório.

1. Criar o banco

Execute o arquivo:

src/main/resources/schema.sql

Ele cria automaticamente o banco:

manutencao_db

2. Configurar a conexão

Por padrão, a aplicação utiliza:

URL:      jdbc:mysql://localhost:3306/manutencao_db
Usuário:  root
Senha:    root

Esses valores podem ser sobrescritos por propriedades da JVM:

-Ddb.url=jdbc:mysql://localhost:3306/manutencao_db
-Ddb.user=seu_usuario
-Ddb.password=sua_senha

No IntelliJ IDEA, essas propriedades podem ser adicionadas em Run/Debug Configurations → VM options.

3. Compilar

mvn clean compile

4. Executar

Pela IDE, execute:

com.joao.empresa.Main

Ou via Maven:

mvn compile exec:java -Dexec.mainClass="com.joao.empresa.Main"

📂 Estrutura dos testes

src/test
├── java/com/joao/empresa
│   ├── builders
│   ├── dao
│   ├── model
│   └── services
└── resources
    └── schema-test.sql

Os builders presentes em src/test auxiliam na criação de objetos de teste, reduzindo repetição e deixando os cenários mais legíveis.

📈 Roadmap

Java 17 + JDBC                    ✅
Modelagem do domínio              ✅
Banco relacional                  ✅
DAOs e mapeamento manual          ✅
Services e regras de negócio      ✅
Transações JDBC                   ✅
Tradução de exceções              ✅
Testes automatizados              ✅
Aplicação de console              ✅
        ↓
Spring Boot                       ⏳
API REST                          ⏳
Spring Data JPA / Hibernate       ⏳
DTOs e Bean Validation            ⏳
Flyway                            ⏳
Tratamento global de exceções     ⏳
Spring Security                   ⏳
Docker                            ⏳

A próxima versão deverá reaproveitar o conhecimento adquirido nesta implementação para substituir gradualmente a infraestrutura manual por abstrações do ecossistema Spring, sem perder a compreensão do que acontece por baixo do framework.

💡 Principais aprendizados

A construção manual desta versão permitiu compreender na prática:

como o Java abre e gerencia conexões com o banco;

como SQL é enviado com parâmetros seguros;

como registros são transformados em objetos;

como IDs gerados pelo banco retornam para as entidades;

como representar relacionamentos sem ORM;

como proteger consistência com transações;

como commit e rollback funcionam;

como constraints complementam as regras da aplicação;

como erros técnicos podem ser traduzidos entre camadas;

como separar domínio, aplicação, persistência e interface;

como injeção por construtor facilita testes e reduz acoplamento;

como identificar consultas desnecessárias e problemas como N+1;

por que frameworks como Spring Boot, Spring Data JPA e Hibernate são úteis — e quais problemas eles abstraem.

👨‍💻 Autor

João Emanuel Pereira do Nascimento
Estudante de Ciência da Computação com foco em desenvolvimento Back-End Java.

📧 pnjoaoemanuel@gmail.com
💼 LinkedIn
🐙 GitHub
