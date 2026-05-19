package com.orderflow.ecommerce.controllers.docs;

import com.orderflow.ecommerce.dtos.ErrorResponse;
import com.orderflow.ecommerce.dtos.PingResponse;
import com.orderflow.ecommerce.dtos.PublishSampleOrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Test and integration", description = "Endpoints auxiliares para health check e publicação de eventos de exemplo no RabbitMQ")
public interface TestControllerDocs {

    @Operation(
        summary = "Verifica se a API está respondendo",
        description = "Endpoint de health check para checar a disponibilidade da aplicação.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "API ativa",
                content = @Content(schema = @Schema(implementation = PingResponse.class)))
        }
    )
    PingResponse ping();

    @Operation(
        summary = "Publica um evento OrderCreated de exemplo no RabbitMQ",
        description = "Útil para validar filas e consumidores (EmailConsumer, InventoryConsumer) sem fluxo real de pedido.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Evento enfileirado",
                content = @Content(schema = @Schema(implementation = PublishSampleOrderResponse.class))),
            @ApiResponse(
                responseCode = "500",
                description = "Erro interno ao publicar",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    PublishSampleOrderResponse publishSampleOrder();
}
