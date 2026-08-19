# 🔧 Maintenance Management System

Sistema de gerenciamento de manutenções desenvolvido em **Java 17**, utilizando **JDBC puro e MySQL** para persistência de dados.

O projeto faz parte do processo de aprofundamento em **desenvolvimento Back-End Java**, com foco em compreender os fundamentos que frameworks como Spring abstraem: acesso ao banco de dados, transações, mapeamento entre objetos e tabelas, tratamento de exceções, regras de negócio e organização em camadas.

> **Status atual:** versão JDBC em fase final de estabilização e atualização da suíte de testes.

---

## 📌 Sobre o projeto

O sistema representa o domínio de manutenção de equipamentos pertencentes a empresas.

A aplicação permite gerenciar:

* empresas;
* equipamentos vinculados às empresas;
* usuários com diferentes papéis;
* técnicos responsáveis pelas manutenções;
* manutenções preventivas e corretivas;
* histórico e estados das manutenções.

Os tipos de usuário atualmente modelados são:

* `Administrador`, com departamento;
* `Gestor`, com área responsável;
* `Tecnico`, com especialidade.

As manutenções podem assumir os estados:

* `ANDAMENTO`;
* `CONCLUIDA`;
* `CANCELADA`.

A proposta desta primeira versão é implementar a persistência **sem ORM ou framework**, permitindo compreender manualmente o fluxo completo entre aplicação Java e banco de dados antes da futura evolução para Spring Boot.

---

## ✅ Funcionalidades implementadas

### Empresas

* cadastro;
* busca por ID;
* listagem;
* atualização;
* exclusão;
* status de empresa ativada ou desativada;
* vínculo com equipamentos;
* proteção contra exclusão de empresas que ainda possuem registros associados;
* validação de unicidade de CNPJ pelo banco de dados.

### Equipamentos

* cadastro vinculado a uma empresa;
* busca por ID;
* listagem;
* atualização;
* exclusão;
* código patrimonial único;
* validação da empresa responsável;
* bloqueio da exclusão quando existem manutenções associadas.

### Usuários

* cadastro;
* busca por ID;
* listagem;
* atualização;
* exclusão;
* herança e polimorfismo entre os diferentes tipos de usuário;
* persistência dos dados gerais e específicos em tabelas relacionadas;
* transações JDBC para operações que envolvem múltiplas tabelas;
* proteção contra alteração indevida do tipo do usuário;
* unicidade de e-mail.

### Manutenções

* cadastro de manutenção preventiva ou corretiva;
* associação com equipamento e técnico responsável;
* busca por ID;
* listagem geral;
* listagem por status;
* busca por equipamento;
* busca por técnico;
* atualização enquanto a manutenção está em andamento;
* conclusão com registro de data final e custo;
* cancelamento;
* tratamento de `data_fim` opcional;
* bloqueio de alterações incompatíveis com o estado atual;
* bloqueio da exclusão de manutenção em andamento.

---

## 🏗️ Arquitetura

O projeto utiliza uma organização em camadas:

```text
src/main/java/com/joao/empresa
├── model        # Entidades, comportamento e regras do domínio
├── services     # Casos de uso e regras da aplicação
├── dao          # Persistência e consultas SQL utilizando JDBC
├── database     # Conexão e tratamento de erros do banco
└── exceptions   # Exceções de persistência e de negócio
```

O fluxo principal da aplicação é:

```text
Entidades
    ↑
Services
    ↓
DAOs
    ↓
JDBC
    ↓
MySQL
```

Cada camada possui uma responsabilidade específica:

* **Entidades:** protegem o estado dos objetos e as invariantes do domínio;
* **Services:** coordenam operações e interpretam regras de negócio;
* **DAOs:** isolam consultas SQL e detalhes da persistência;
* **Banco de dados:** garante integridade por meio de constraints e relacionamentos.

A versão atual ainda não possui `Controller`, endpoints HTTP ou interface gráfica.

---

## 🧠 Conceitos aplicados

O projeto foi utilizado para praticar e aplicar conceitos importantes de desenvolvimento Back-End e arquitetura de software:

### Java e Orientação a Objetos

* encapsulamento;
* abstração;
* herança;
* polimorfismo;
* classes abstratas;
* enums;
* coleções;
* `equals()` e `hashCode()`;
* identidade de entidades;
* validações e invariantes de domínio.

### Arquitetura

* arquitetura em camadas;
* Separation of Concerns;
* baixo acoplamento;
* alta coesão;
* padrão DAO;
* Fail Fast;
* regras de negócio dentro do domínio;
* separação entre regra de negócio e infraestrutura.

### JDBC e persistência

* `Connection`;
* `PreparedStatement`;
* `ResultSet`;
* consultas parametrizadas;
* `INSERT`, `SELECT`, `UPDATE` e `DELETE`;
* `JOIN`;
* mapeamento manual entre tabelas e objetos;
* relacionamentos por chaves estrangeiras;
* `AUTO_INCREMENT`;
* `RETURN_GENERATED_KEYS`;
* tratamento de valores `NULL`;
* transações;
* `commit`;
* `rollback`;
* atomicidade;
* integridade referencial.

### Performance

Durante a evolução do projeto também foram identificados e corrigidos cenários de **N+1 queries**, nos quais uma listagem executava uma consulta inicial e depois novas consultas para cada registro encontrado.

Parte dessas operações passou a utilizar `JOIN`, reduzindo o número de acessos ao banco e reforçando a importância de analisar não apenas se uma consulta funciona, mas também **quantas consultas estão sendo executadas**.

---

## ⚠️ Tratamento de exceções

O tratamento de erros foi estruturado considerando o nível de abstração de cada camada.

Erros técnicos do JDBC são tratados na camada de persistência e traduzidos para exceções próprias da aplicação.

Exemplo:

```text
MySQL
  ↓
SQLException
  ↓
TradutorSQLException
  ↓
RegistroDuplicadoException
  ↓
Service
  ↓
EmpresaJaCadastradaException
```

A ideia central é que:

> **O erro evolui conforme atravessa as camadas da aplicação, sendo traduzido para a linguagem e o nível de abstração de cada camada.**

Entre as exceções utilizadas estão:

```text
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
```

Também são utilizadas exceções padrão do Java quando representam melhor o problema:

* `IllegalArgumentException` → entrada ou argumento inválido;
* `IllegalStateException` → operação incompatível com o estado atual da entidade.

O banco permanece como última linha de defesa por meio de constraints como:

```text
PRIMARY KEY
FOREIGN KEY
UNIQUE
NOT NULL
ON DELETE RESTRICT
ON DELETE CASCADE
```

---

## 🔄 Transações

Operações que alteram múltiplas tabelas utilizam transações JDBC.

Um exemplo é o cadastro de usuários, que pode envolver:

```text
INSERT usuario
        ↓
INSERT tecnico / gestor / administrador
        ↓
COMMIT
```

Caso alguma etapa falhe:

```text
INSERT usuario
        ↓
falha ao inserir dados específicos
        ↓
ROLLBACK
```

Isso garante **atomicidade**, evitando que uma operação fique parcialmente persistida no banco.

---

## 🗄️ Banco de dados

O projeto utiliza **MySQL** e possui um `schema.sql` responsável pela definição da estrutura do banco.

Entre as principais relações estão:

```text
Empresa
   └── Equipamentos

Usuario
   ├── Administrador
   ├── Gestor
   └── Tecnico
          └── Manutenções

Equipamento
   └── Manutenções
```

Restrições de unicidade são utilizadas para proteger campos como:

* e-mail de usuário;
* CNPJ de empresa;
* código patrimonial de equipamento.

---

## 🛠️ Tecnologias utilizadas

| Tecnologia        | Uso no projeto                          |
| ----------------- | --------------------------------------- |
| Java 17           | Linguagem principal                     |
| Maven             | Build e gerenciamento de dependências   |
| JDBC              | Comunicação manual com o banco          |
| MySQL             | Banco de dados relacional               |
| MySQL Connector/J | Driver JDBC                             |
| JUnit 5           | Testes automatizados                    |
| Mockito           | Mocks e isolamento de dependências      |
| JaCoCo            | Relatórios de cobertura                 |
| Git               | Controle de versão                      |
| GitHub            | Versionamento e documentação do projeto |

---

## 🧪 Testes

O projeto utiliza **JUnit 5**, **Mockito** e builders para construção dos objetos utilizados nos cenários de teste.

A suíte de testes foi criada durante versões anteriores da aplicação e atualmente está sendo **reestruturada para refletir o domínio e a arquitetura atualizados**.

A nova suíte deverá cobrir:

* entidades e invariantes do domínio;
* transições de estado;
* regras das Services;
* cenários de sucesso;
* entidades não encontradas;
* duplicidades;
* argumentos inválidos;
* estados inválidos;
* entidades em uso;
* tradução de exceções;
* DAOs e operações JDBC;
* geração de IDs;
* valores opcionais;
* integridade referencial;
* transações, `commit` e `rollback`.

A meta é permitir que a versão JDBC seja considerada estabilizada somente após toda a suíte executar corretamente com:

```bash
mvn test
```

---

## 🚧 Status atual

A camada JDBC passou por uma etapa de estabilização envolvendo:

* correção do gerenciamento de IDs;
* melhoria de `equals()` e `hashCode()`;
* fortalecimento das invariantes do domínio;
* correção do tratamento de valores opcionais;
* melhoria do mapeamento entre banco e objetos Java;
* redução de consultas N+1;
* utilização de transações;
* implementação adequada de `commit` e `rollback`;
* padronização do tratamento de exceções;
* tradução de erros entre as camadas;
* proteção da integridade dos relacionamentos;
* revisão das operações de atualização e exclusão.

A principal etapa em andamento é a **reestruturação da suíte de testes automatizados**.

---

## 📈 Próximas etapas

### Finalização da versão JDBC

* [ ] atualizar builders de teste;
* [ ] reconstruir os testes das entidades;
* [ ] reconstruir os testes das Services;
* [ ] criar/revisar testes de integração dos DAOs;
* [ ] testar fluxos transacionais;
* [ ] revisar cobertura com JaCoCo;
* [ ] garantir execução completa com `mvn test`;
* [ ] revisar documentação de execução;
* [ ] preparar a release da versão JDBC.

### Evolução futura

Após a conclusão e estabilização da versão JDBC, o projeto será utilizado como base para uma nova implementação utilizando:

* Spring Boot;
* Spring Web;
* Spring Data JPA;
* Hibernate;
* DTOs;
* Bean Validation;
* `@Transactional`;
* tratamento global de exceções;
* Flyway;
* Spring Security;
* autenticação e autorização;
* API REST;
* documentação da API;
* Docker;
* testes unitários e de integração.

---

## 🎯 Objetivo de aprendizado

O objetivo do projeto não é apenas construir um CRUD, mas compreender os fundamentos utilizados no desenvolvimento de aplicações Back-End Java.

A implementação manual com JDBC permite compreender na prática:

* como uma aplicação estabelece conexões com o banco;
* como SQL é executado pelo Java;
* como parâmetros são enviados de forma segura;
* como registros são convertidos em objetos;
* como objetos são persistidos novamente;
* como IDs são gerados e recuperados;
* como relacionamentos são representados;
* como funcionam transações;
* como `commit` e `rollback` protegem a consistência;
* como identificar problemas de performance como N+1;
* como o banco protege a integridade dos dados;
* onde devem ficar as regras de negócio;
* como erros técnicos são traduzidos entre camadas;
* como organizar uma aplicação reduzindo acoplamento entre domínio e infraestrutura.

A futura migração para Spring terá como objetivo abstrair parte desse código manual sem perder a compreensão dos mecanismos existentes por trás do framework.

---

## 🗺️ Roadmap

```text
Java + JDBC
    ↓
Estabilização da persistência ✅
    ↓
Regras e invariantes do domínio ✅
    ↓
Tratamento de exceções ✅
    ↓
Testes automatizados 🚧
    ↓
Release JDBC
    ↓
Spring Boot
    ↓
Spring Data JPA
    ↓
API REST
    ↓
Spring Security
```

---

## 👨‍💻 Autor

**João Emanuel Pereira do Nascimento**

Estudante de Ciência da Computação com foco em desenvolvimento **Back-End Java**.

📧 [pnjoaoemanuel@gmail.com](mailto:pnjoaoemanuel@gmail.com)
💼 [LinkedIn](https://www.linkedin.com/in/jo%C3%A3o-emanuel-5b268b22b)
🐙 [GitHub](https://github.com/joaoemanuel-dev)
