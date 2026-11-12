import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Entregas {
    private static final int PORTA = 8083;
    private static final String PEDIDOS_URL =
            System.getenv().getOrDefault("PEDIDOS_URL", "http://localhost:8082");
    private static final Map<Integer, Entrega> entregas = new ConcurrentHashMap<>();
    private static final AtomicInteger proximoId = new AtomicInteger(1);
    private static final HttpClient cliente = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2)).build();

    public static void main(String[] args) throws IOException {
        HttpServer servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", PORTA), 0);
        servidor.createContext("/entregas", Entregas::atender);
        servidor.setExecutor(Executors.newCachedThreadPool());
        servidor.start();
        System.out.println("Entregas em http://localhost:" + PORTA);
    }

    private static void atender(HttpExchange exchange) throws IOException {
        String metodo = exchange.getRequestMethod();
        String caminho = exchange.getRequestURI().getPath();

        if ("/entregas".equals(caminho)) {
            if (!"POST".equals(metodo)) {
                exchange.getResponseHeaders().set("Allow", "POST");
                responder(exchange, 405, "{\"erro\":\"Método não permitido\"}");
                return;
            }
            criar(exchange);
            return;
        }

        if (caminho.matches("/entregas/[^/]+")) {
            if (!"GET".equals(metodo)) {
                exchange.getResponseHeaders().set("Allow", "GET");
                responder(exchange, 405, "{\"erro\":\"Método não permitido\"}");
                return;
            }
            consultar(exchange, caminho.substring("/entregas/".length()));
            return;
        }

        responder(exchange, 404, "{\"erro\":\"Rota não encontrada\"}");
    }

    private static void criar(HttpExchange exchange) throws IOException {
        String corpo = new String(exchange.getRequestBody().readNBytes(65), StandardCharsets.UTF_8).trim();
        if (!corpo.matches("[1-9][0-9]*")) {
            responder(exchange, 400, "{\"erro\":\"Informe um ID de pedido inteiro positivo\"}");
            return;
        }

        final int pedidoId;
        try {
            pedidoId = Integer.parseInt(corpo);
        } catch (NumberFormatException e) {
            responder(exchange, 400, "{\"erro\":\"ID de pedido inválido\"}");
            return;
        }

        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(PEDIDOS_URL + "/pedidos/" + pedidoId))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();

        try {
            HttpResponse<String> resposta = cliente.send(
                    requisicao, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (resposta.statusCode() == 404) {
                responder(exchange, 422, "{\"erro\":\"O pedido informado não existe\"}");
                return;
            }
            if (resposta.statusCode() != 200) {
                responder(exchange, 503, "{\"erro\":\"Não foi possível consultar o serviço de Pedidos\"}");
                return;
            }

            int id = proximoId.getAndIncrement();
            Entrega entrega = new Entrega(id, pedidoId, "AGUARDANDO_ENVIO");
            entregas.put(id, entrega);
            exchange.getResponseHeaders().set("Location", "/entregas/" + id);
            responder(exchange, 201, entrega.toJson());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            responder(exchange, 503, "{\"erro\":\"Consulta ao serviço de Pedidos interrompida\"}");
        } catch (Exception e) {
            responder(exchange, 503, "{\"erro\":\"Serviço de Pedidos indisponível\"}");
        }
    }

    private static void consultar(HttpExchange exchange, String idTexto) throws IOException {
        if (!idTexto.matches("[1-9][0-9]*")) {
            responder(exchange, 400, "{\"erro\":\"ID de entrega inválido\"}");
            return;
        }

        final int id;
        try {
            id = Integer.parseInt(idTexto);
        } catch (NumberFormatException e) {
            responder(exchange, 400, "{\"erro\":\"ID de entrega inválido\"}");
            return;
        }

        Entrega entrega = entregas.get(id);
        if (entrega == null) {
            responder(exchange, 404, "{\"erro\":\"Entrega não encontrada\"}");
            return;
        }
        responder(exchange, 200, entrega.toJson());
    }

    private static void responder(HttpExchange exchange, int status, String corpo) throws IOException {
        byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var saida = exchange.getResponseBody()) { saida.write(bytes); }
    }

    private static class Entrega {
        private final int id;
        private final int pedidoId;
        private final String status;

        private Entrega(int id, int pedidoId, String status) {
            this.id = id;
            this.pedidoId = pedidoId;
            this.status = status;
        }

        private String toJson() {
            return "{\"id\":" + id + ",\"pedidoId\":" + pedidoId
                    + ",\"status\":\"" + status + "\"}";
        }
    }
}
