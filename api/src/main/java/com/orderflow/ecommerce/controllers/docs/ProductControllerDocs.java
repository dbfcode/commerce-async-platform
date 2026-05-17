package com.orderflow.ecommerce.controllers.docs;

import com.orderflow.ecommerce.dtos.ErrorResponse;
import com.orderflow.ecommerce.entities.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Product", description = "Endpoints para gerenciar Product - CRUD de produtos do catálogo")
public interface ProductControllerDocs {

    @Operation(
        summary = "Lista todos os produtos",
        description = "Retorna uma lista de todos os produtos existentes no sistema",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista obtida com sucesso",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class)))
            )
        }
    )
    ResponseEntity<List<Product>> findAll();

    @Operation(
        summary = "Obtém produto por id",
        description = "Retorna um produto com base no seu ID fornecido.",
        parameters = {
            @Parameter(
                name = "id",
                description = "Identificador numérico do produto",
                required = true,
                example = "10")
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Produto encontrado com sucesso",
                content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(
                responseCode = "404",
                description = "Produto inexistente ou não encontrado para o ID informado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    ResponseEntity<Product> findById(Long id);

    @Operation(
        summary = "Cria um produto",
        description = "Insere um novo produto no sistema. O campo 'id' deve ser omitido no envio.",
        requestBody = @RequestBody(
            description = "Dados do produto a ser criado",
            required = true,
            content = @Content(schema = @Schema(implementation = Product.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Produto criado e persistido com sucesso",
                content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(
                responseCode = "400",
                description = "Corpo inválido ou falha de validação nos dados enviados",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    ResponseEntity<Product> insert(Product obj);

    @Operation(
        summary = "Remove produto por id",
        description = "Remove um produto existente do sistema de forma permanente.",
        parameters = {
            @Parameter(
                name = "id",
                description = "Identificador do produto a remover",
                required = true,
                example = "10")
        },
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Exclusão processada (idempotente se o registro não existir)",
                content = @Content
            )
        }
    )
    ResponseEntity<Void> delete(Long id);

    @Operation(
        summary = "Atualiza um produto",
        description = "Atualiza os dados de um produto existente com base no ID fornecido",
        parameters = {
            @Parameter(
                name = "id",
                description = "Identificador do produto",
                required = true,
                example = "10"
            )
        },
        requestBody = @RequestBody(
            description = "Novos dados para atualização de produto",
            required = true,
            content = @Content(schema = @Schema(implementation = Product.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Produto atualizado com sucesso",
                content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(
                responseCode = "400",
                description = "Corpo inválido ou falha de validação nos dados enviados",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                responseCode = "404",
                description = "Produto inexistente ou não encontrado para o ID informado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    ResponseEntity<Product> update (Long id, Product obj);
}
