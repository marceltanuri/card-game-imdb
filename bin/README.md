# Card Game IMDB

REST API baseada que simula um jogo de cartas. Cada carta representa um filme e o objetivo do jogo é responder o máximo de rodadas possível (2 cartas por rodada) qual a carta de maior avaliação no IMDB.

Frameworks
- SptingBoot
- SpringData
- SpringSecurity
- H2 DataBase
- JSoup - HTML Parser 

- SpringBootTest
- JUnit

Foram implementados testes unitários e testes de integração.

## Para experimentar a aplicação

1. Faça checkout do projeto
2. Inicie a aplicação SpringBoot. Java 11+ requirido
3. Os endpoints da aplicação são:
	- `/iniciar` {POST} inicia uma nova partida
	- `/proxima/{imbdEscolhido}` {POST} responde a rodada atual e chama a póxima rodada. Deve ser enviado por parâmetro o imdbId da carta escolhida
	- `/status` {GET} informa a qualquer momento o status da partida em andamento, se não houver partida em andamento e houver alguma partida já encerrada, então informa o status da partida encerrada. Se nenhum dos casos retorna um status não encontrado
	- `/desistir` {POST} abandona a partida atual
	- `/top10` {GET} informa o ranking dos 10 jogadores com maior pontuação
	- `/meuRanking` {GET} informa a posição no ranking do jogador atual
	
4. Todas as requisições requerem autenticação. Para efeitos de testes os usuários disponíveis são:
	- user, user0, user1, user2.... user13
	

A documentação OpenAPI está disponível no arquivo: `prova3OpenAPIDoc.yaml`.
A collection do postman utilizada como origem da documentação OpenAPI está no aquivo: `AdaProva3.postman_collection.json`

** A documentação OpenAPI ainda está incompleta, requer mais detalhes, misc, e schemas.
  