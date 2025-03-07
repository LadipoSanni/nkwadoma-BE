package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.OrganizationRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.UserIdentityResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/users")
    public class UserController {

        @Operation(
                summary = "Create a new user",
                description = "Accepts a UserRequest object and returns a UserResponse object"
        )
        @ApiResponse(
                responseCode = "201",
                description = "User created successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UserIdentityResponse.class),
                        examples = @ExampleObject(value = """
                {
                    "id": 1,
                    "name": "John Doe",
                    "email": "john.doe@example.com"
                }
                """
                        )
                )
        )
        @PostMapping
        public UserIdentityResponse createUser(
                @RequestBody(
                        description = "User details for creating a new user",
                        required = true,
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = OrganizationRequest.class),
                                examples = @ExampleObject(value = """
                    {
                        "name": "John Doe",
                        "email": "john.doe@example.com"
                    }
                    """
                                )
                        )
        ) {
            return null;
        }
        }


}
