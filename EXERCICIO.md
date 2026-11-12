# Exercício — Serviço de Entregas

Criar um terceiro serviço Java, com classe principal `Entregas`, porta 8083 e armazenamento próprio em memória.

## Regras
- `POST /entregas`: recebe ID positivo de pedido no corpo `text/plain`.
- Consulta `GET http://localhost:8082/pedidos/{id}` com timeout.
- Pedido existente: cria entrega, responde `201` e cabeçalho `Location`.
- Pedido inexistente: `422`, sem salvar.
- Pedidos indisponível ou resposta inesperada: `503`, sem salvar.
- Entrada inválida: `400`; método incorreto: `405` com `Allow`; rota desconhecida: `404`.
- `GET /entregas/{id}`: consulta apenas a memória de Entregas, com `200`, `400` ou `404`.

Exemplo de resposta: `{"id":1,"pedidoId":1,"status":"AGUARDANDO_ENVIO"}`.

## Demonstração
Criar pedido e entrega; consultar entrega; testar IDs inválidos e inexistentes; parar Pedidos e observar `503` ao criar e `200` ao consultar entrega existente; parar apenas Produtos e verificar que Entregas ainda pode criar a partir de um pedido existente; reiniciar Entregas e observar a perda dos registros em memória.
