# Matriz de testes

Os resultados obtidos abaixo correspondem aos testes básicos executados localmente. Os demais cenários estão descritos para reprodução e devem ter suas evidências registradas antes da entrega.

| Cenário | Resultado esperado | Resultado obtido | Evidência |
|---|---|---|---|
| GET /produtos/1 | 200 | 200 | Teste HTTP local |
| POST /pedidos com corpo 1 | 201 | 201 | Teste HTTP local |
| GET /pedidos/1 | 200 | 200 | Teste HTTP local |
| POST /entregas com corpo 1 | 201 | 201 | Teste HTTP local |
| GET /entregas/1 | 200 | 200 | Teste HTTP local |
| POST /entregas com abc | 400 | A verificar | Executar testes.http |
| POST /entregas com ID inexistente | 422 | A verificar | Executar testes.http |
| POST /entregas com Pedidos desligado | 503 | A verificar | Executar testes.http |
| GET de entrega existente com Pedidos desligado | 200 | A verificar | Executar testes.http |
| Criar entrega com Produtos desligado, pedido existente | 201 | A verificar | Executar testes.http |
| Reiniciar Entregas e consultar entrega anterior | 404 | A verificar | Reiniciar processo |

**Atenção:** registre capturas de tela ou logs dos testes adicionais. Não considere os resultados marcados “A verificar” como evidências já realizadas.
