package com.orderflow.ecommerce.controllers.docs;

import com.orderflow.ecommerce.dtos.ErrorResponse;
import com.orderflow.ecommerce.entities.Category;
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

@Tag(name = "Category", description = "Endpoints para gerenciar Category - CRUD de categorias de produtos")
public interface CategoryControllerDocs {

    @Operation(
        summary = "Lista todas as categorias",
        description = "Retorna uma lista de todas as categorias existentes no sistema.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista obtida com sucesso",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = Category.class)))
            )
        }
    )
    ResponseEntity<List<Category>> findAll();

    @Operation(
        summary = "Obtém categoria por id",
        description = "Retorna uma categoria com base no seu ID fornecido.",
        parameters = {
            @Parameter(
                name = "id",
                description = "Identificador numérico de categoria",
                required = true,
                example = "1"
            )
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Categoria encontrada",
                content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(
                responseCode = "404",
                description = "Categoria inexistente",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    ResponseEntity<Category> findById(Long id);

    @Operation(
        summary = "Cria uma categoria",
        description = "Insere uma nova categoria no sistema. O campo 'id' deve ser omitido no envio.",
        requestBody = @RequestBody(
            description = "Dados da categoria a ser criada",
            required = true,
            content = @Content(schema = @Schema(implementation = Category.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Categoria criada e persistida com sucesso",
                content = @Content(schema = @Schema(implementation = Category.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Corpo inválido ou falha de validação",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    ResponseEntity<Category> insert(Category obj);

    @Operation(
        summary = "Remove categoria por id",
        description = "Remove uma categoria existente do sistema de forma permanente.",
        parameters = {
            @Parameter(
                name = "id",
                description = "Identificador número da categoria a remover",
                required = true,
                example = "1"
            )
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
        summary = "Atualiza o nome de uma categoria",
        description = "Atualiza os dados de uma categoria existente com base no ID fornecido",
        parameters = {
            @Parameter(
                name = "id",
                description = "Identificador numérico da categoria",
                required = true,
                example = "1"
            )
        },
        requestBody = @RequestBody(
            description = "Novos dados para atualização da categoria",
            required = true,
            content = @Content(schema = @Schema(implementation = Category.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Categoria atualizada com sucesso",
                content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(
                responseCode = "400",
                description = "Corpo inválido ou falha de validação nos dados enviados",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                responseCode = "404",
                description = "Categoria inexistente ou não encontrada para o ID informado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    ResponseEntity<Category> update (Long id, Category obj);
}
