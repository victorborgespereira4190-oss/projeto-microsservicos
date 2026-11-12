# Como executar e testar

## Requisitos
- JDK 17 ou superior
- IntelliJ IDEA
- Portas 8081, 8082 e 8083 livres

## Inicialização
Abra cada pasta `*-service` em uma janela do IntelliJ, selecione JDK 17+ e execute a classe principal em `src/`.

1. Execute `Produtos.java` — porta 8081.
2. Execute `Pedidos.java` — porta 8082.
3. Execute `Entregas.java` — porta 8083.

Os serviços usam a API HTTP incluída no JDK, sem Maven, Gradle ou Spring Boot. Também é possível executar no terminal, a partir da pasta de cada serviço: `javac -d out src/Produtos.java && java -cp out Produtos` (substitua o nome da classe para os outros serviços).

## Teste rápido
1. `GET http://localhost:8081/produtos/1` deve retornar `200`.
2. `POST http://localhost:8082/pedidos` com corpo `1` deve retornar `201`.
3. `GET http://localhost:8082/pedidos/1` deve retornar `200`.
4. `POST http://localhost:8083/entregas` com corpo `1` deve retornar `201`.
5. `GET http://localhost:8083/entregas/1` deve retornar `200`.

Use os arquivos `testes.http` no IntelliJ para repetir esses testes. Como os dados são mantidos em memória, os IDs reiniciam quando os processos são reiniciados.

## Testes de falha
- `POST /entregas` com corpo `abc` -> `400`.
- `POST /entregas` com ID de pedido inexistente -> `422`.
- Desligar Pedidos e tentar criar uma entrega -> `503`.
- Com Pedidos desligado, consultar uma entrega já criada -> `200`.
- Com Produtos desligado, mas Pedidos ligado, criar entrega para pedido já existente -> `201`.

Confira `MATRIZ_TESTES.md` e `ARQUITETURA.md` para a documentação da entrega.
