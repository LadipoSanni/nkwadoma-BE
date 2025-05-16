package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.swaggerAnnotationDoc;

import africa.nkwadoma.nkwadoma.domain.enums.constants.InvestmentVehicleMessages;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.swagger.SwaggerDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = SwaggerDocumentation.VIEW_INVESTMENT_DETAILS_OF_FINANCIER,
        description = SwaggerDocumentation.VIEW_INVESTMENT_DETAILS_OF_FINANCIER_DESCRIPTION,
        responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Financier detail retrieved successfully",
                        content = @Content(schema = @Schema(implementation = InvestmentVehicleMessages.class))
                ),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid financier ID provided",
                        content = @Content(schema = @Schema(implementation = InvestmentVehicleMessages.class))
                ),
                @ApiResponse(
                        responseCode = "404",
                        description = "Financier not found"
                )
        }
)
public @interface FinancierDetail {}