# Arquitetura

```text
Cliente HTTP (IntelliJ)
       |
       +--> Produtos :8081
       |       GET /produtos/{id}
       |
       +--> Pedidos :8082 ----HTTP GET----> Produtos :8081
       |       POST /pedidos
       |       GET /pedidos/{id}
       |
       +--> Entregas :8083 ---HTTP GET----> Pedidos :8082
               POST /entregas
               GET /entregas/{id}
```

Os serviços são executados separadamente e possuem dados próprios em memória. Pedidos consulta Produtos apenas na criação de um pedido. Entregas consulta Pedidos apenas na criação de uma entrega. A consulta de uma entrega já existente não depende de Pedidos nem de Produtos.

Cada serviço tem IDs próprios e não importa classes de outro serviço. A comunicação acontece exclusivamente por HTTP. Ao reiniciar um processo, os registros mantidos em memória são perdidos.
