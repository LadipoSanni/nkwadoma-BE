package africa.nkwadoma.nkwadoma.infrastructure.utilities;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        responses = {
                @ApiResponse(responseCode = "200", description = "Success"),
                @ApiResponse(responseCode = "400", description = "Bad Request")
        }
)
public @interface CustomSwaggerDoc {
    String summary();
    String description();
    Class<?> responseClass();
}