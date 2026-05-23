# Parking Lot System

Este é um sistema de gerenciamento de estacionamento feito em Java, rodando interativamente via terminal (CLI). O banco de dados utilizado é o **PostgreSQL**, que é orquestrado e isolado de forma automática usando **Docker**.

## 🛠️ Tecnologias Utilizadas
- **Java 17** (JDBC para conexão com banco)
- **Maven** (Gerenciador de dependências)
- **PostgreSQL 15** (Banco de dados relacional)
- **Docker & Docker Compose** (Isolamento da infraestrutura)

---

## 🚀 Como Executar o Projeto

Para rodar este projeto, você precisará ter o **Java (JDK)** e o **Docker Desktop** instalados na sua máquina. Siga os passos abaixo:

### Passo 1: Iniciar o Banco de Dados
Antes de rodar a aplicação Java, o banco de dados PostgreSQL precisa estar rodando. Nós usamos o Docker Compose para fazer isso em apenas um comando.

1. Abra o **Docker Desktop** no seu computador.
2. Abra um terminal dentro da pasta `projeto estacionamento` e rode o seguinte comando para baixar e ligar o banco de dados em segundo plano:
   ```bash
   docker-compose up -d
   ```
> *Nota: O banco está configurado para rodar na porta `5433` da sua máquina e vai criar as tabelas automaticamente quando o Java for executado pela primeira vez.*

### Passo 2: Executar a Aplicação Java

Como a infraestrutura (banco) já está online, basta executar o código Java na sua máquina. O projeto está configurado para acessar o `localhost:5433` por padrão.

#### Opção A: Usando a sua IDE (VSCode, IntelliJ, Eclipse) - Recomendado
1. Abra o arquivo `Main.java`.
2. Clique no botão de **Run** ou **Play** da sua IDE.
3. O console/terminal integrado da IDE irá abrir com o menu interativo, pronto para você digitar.

#### Opção B: Pelo Terminal usando Maven
Caso prefira não usar os botões da IDE, você pode compilar e rodar via terminal:
1. No terminal da pasta `projeto estacionamento`, digite:
   ```bash
   mvn compile exec:java -Dexec.mainClass="Main"
   ```
*(Caso não tenha o plugin exec do maven configurado, você pode compilar e rodar manualmente)*:
```bash
mvn clean package
java -jar target/projeto-estacionamento-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## 🧹 Limpando os Dados (Zerar o Estacionamento)
Se você quiser apagar todo o histórico de movimentações e recomeçar do zero, você precisará apagar o volume salvo pelo Docker. Para isso, execute:
```bash
docker-compose down -v
```
Depois, basta rodar o comando do Passo 1 novamente.
