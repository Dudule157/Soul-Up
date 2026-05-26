# Soul Up MVP

Soul Up e um MVP para upload de videos de agachamento, processamento com Python/OpenCV/MediaPipe, contagem de repeticoes e persistencia do historico em MySQL via Spring Boot.

## Arquitetura

```txt
Frontend HTML/CSS/JS
        |
        | HTTP multipart
        v
Backend Spring Boot
        |
        | ProcessBuilder
        v
Python OpenCV + MediaPipe
        |
        | JSON
        v
MySQL
```

## Decisoes tecnicas

| Decisao | Motivo |
| --- | --- |
| Upload de video em vez de webcam | Reduz complexidade, melhora debugging e deixa o pipeline mais previsivel. |
| Python separado do backend | Mantem visao computacional isolada da regra de negocio da API. |
| Spring Boot em camadas | Evita controller com regra de negocio e deixa o codigo escalavel para novas features. |
| HTML/CSS/JS puro no MVP | Reduz escopo e foca backend, integracao e processamento. |
| Apenas agachamento | Movimento claro, facil de modelar por angulo de joelho e adequado para um primeiro MVP. |

## Requisitos locais

- Java 21
- Maven
- Python 3.12
- MySQL

Observacao: nesta maquina, durante a criacao do projeto, `java` e `mvn` nao estavam disponiveis no PATH da sessao. Instale ou configure antes de compilar o backend.

## Banco de dados

Crie o banco:

```sql
CREATE DATABASE soul_up CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

O backend esta configurado com `spring.jpa.hibernate.ddl-auto=update`, entao as tabelas sao criadas pelo Hibernate em ambiente local. Para estudo, veja tambem [docs/database.sql](docs/database.sql).

## Executar Python

```bash
cd ia-python
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
python main.py caminho/do/video.mp4
```

O script deve imprimir apenas o JSON de resultado em `stdout`. Erros saem em `stderr`, porque o backend depende dessa separacao.

## Executar backend

```bash
cd backend
mvn spring-boot:run
```

Variaveis uteis:

```bash
DB_URL=jdbc:mysql://localhost:3306/soul_up?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=sua_senha
PYTHON_EXECUTABLE=python
PYTHON_SCRIPT_PATH=../ia-python/main.py
VIDEO_UPLOAD_DIR=uploads
```

## Executar frontend

Abra [frontend/index.html](frontend/index.html) no navegador. A tela espera a API em `http://localhost:8080`, mas esse valor pode ser alterado no campo API.

## Endpoints

| Metodo | Rota | Responsabilidade |
| --- | --- | --- |
| POST | `/usuarios` | Criar usuario |
| GET | `/usuarios` | Listar usuarios |
| GET | `/usuarios/{id}` | Buscar usuario por ID |
| POST | `/treinos/analisar` | Receber video, analisar e salvar treino |
| GET | `/treinos` | Listar treinos |
| GET | `/treinos/usuario/{id}` | Listar historico do usuario |

## Limitacoes conscientes do MVP

- Senha ainda nao usa hash. Antes de qualquer uso real, adicione Spring Security e BCrypt.
- Processamento e sincrono. Para videos longos ou muitos usuarios, evolua para fila ou job assíncrono.
- A regra de agachamento usa thresholds fixos. Isso e simples para aprendizado, mas precisa calibracao com videos reais.
- Arquivos enviados ficam em `backend/uploads`. Em producao, use armazenamento externo e politica de retencao.
