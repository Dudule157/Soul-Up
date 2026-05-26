# Arquitetura do MVP

## Fluxo principal

```txt
Usuario seleciona video no frontend
        |
        v
POST /treinos/analisar multipart
        |
        v
TreinoController
        |
        v
TreinoService
        |
        +--> valida usuario
        +--> valida extensao do video
        +--> salva arquivo em uploads
        +--> chama PythonAnalysisService
                    |
                    v
              python main.py video
                    |
                    v
              JSON da analise
        |
        v
salva Treino no MySQL
        |
        v
retorna TreinoResponse
```

## Por que essa divisao existe

Controller deve ser fino. Ele conhece HTTP, parametros e status de resposta. Ele nao deve saber como salvar video, chamar Python ou persistir resultado.

Service concentra regra de negocio. No MVP, a regra e: um usuario existente envia um video valido, o video e analisado, o resultado vira um treino persistido.

Repository isola acesso ao banco. Isso reduz acoplamento com JPA e deixa as consultas localizadas.

Python fica isolado porque visao computacional tem dependencias, runtime e erros diferentes do backend Java. Se no futuro o processamento virar fila, servico HTTP ou worker, o impacto no backend fica menor.

## Pontos de atencao profissional

1. Senha em texto puro nao e aceitavel em producao.
2. `ProcessBuilder` bloqueia a requisicao enquanto o video e processado.
3. Arquivo local funciona para MVP, mas nao escala para multiplas instancias do backend.
4. Threshold fixo de agachamento e simples, mas sensivel a camera, biotipo e qualidade do video.
5. `ddl-auto=update` e pratico no estudo, mas migrations com Flyway ou Liquibase sao melhores em projeto profissional.
