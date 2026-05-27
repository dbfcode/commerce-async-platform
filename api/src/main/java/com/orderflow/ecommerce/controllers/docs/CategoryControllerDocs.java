package com.orderflow.ecommerce.controllers.docs;

import com.orderflow.ecommerce.dtos.CategoryRequest;
import com.orderflow.ecommerce.dtos.CategoryResponse;
import com.orderflow.ecommerce.dtos.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Category", description = "Endpoints para gerenciar Category - CRUD de categorias de produtos")
public interface CategoryControllerDocs {

    @Operation(
            summary = "Lista todas as categorias",
            description = "Retorna uma lista paginada de todas as categorias. Filtra por nome se o parâmetro 'name' for informado.",
            parameters = {
                    @Parameter(name = "name", description = "Filtro opcional por nome da categoria", required = false, example = "Eletrônicos"),
                    @Parameter(name = "page", description = "Número da página (começa em 0)", example = "0"),
                    @Parameter(name = "size", description = "Quantidade de itens por página", example = "10"),
                    @Parameter(name = "sort", description = "Campo e direção de ordenação", example = "name,asc")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista obtida com sucesso",
                            content = @Content(schema = @Schema(implementation = CategoryResponse.class))
                    )
            }
    )
    ResponseEntity<Page<CategoryResponse>> findAll(
            @Parameter(hidden = true) @RequestParam(required = false) String name,
            @Parameter(hidden = true) Pageable pageable
    );

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
                content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(
                responseCode = "404",
                description = "Categoria inexistente",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    ResponseEntity<CategoryResponse> findById(Long id);

    @Operation(
        summary = "Cria uma categoria",
        description = "Insere uma nova categoria no sistema.",
        requestBody = @RequestBody(
            description = "Dados da categoria a ser criada",
            required = true,
            content = @Content(schema = @Schema(implementation = CategoryRequest.class))
        ),
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Categoria criada com sucesso",
                content = @Content(schema = @Schema(implementation = CategoryResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Corpo inválido ou falha de validação",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    ResponseEntity<CategoryResponse> create(CategoryRequest request);

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
                    content = @Content(schema = @Schema(implementation = CategoryRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Categoria atualizada com sucesso",
                            content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
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
    ResponseEntity<CategoryResponse> update(Long id, CategoryRequest request);

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
                description = "Categoria removida com sucesso",
                content = @Content
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Categoria não encontrada",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    ResponseEntity<Void> delete(Long id);
}
