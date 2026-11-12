# Serviço de Entregas

Implementação didática em Java puro, sem bibliotecas externas, usando `HttpServer` e `HttpClient`.

## Execução
Abra a pasta `entregas-service` no IntelliJ com JDK 17+ e execute `src/Entregas.java`. O servidor inicia em `http://localhost:8083`.

Para criar entregas, mantenha Pedidos ligado na porta 8082. O endereço pode ser alterado pela variável de ambiente `PEDIDOS_URL`.

## Endpoints
- `POST /entregas` com corpo `text/plain` contendo o ID positivo de um pedido: retorna `201` e `Location` se o pedido existir.
- `GET /entregas/{id}`: retorna `200` com os dados ou `404` se não existir.

Erros: `400` para ID inválido, `422` para pedido inexistente, `503` para indisponibilidade de Pedidos e `405` para método não permitido.

Os dados ficam somente na memória de Entregas e desaparecem ao reiniciar. Veja `testes.http` para exemplos.
