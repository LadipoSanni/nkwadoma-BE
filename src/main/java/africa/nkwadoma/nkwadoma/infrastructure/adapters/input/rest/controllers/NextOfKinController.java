package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.constants.ControllerConstant;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;



@Slf4j
@RequiredArgsConstructor
@RestController
public class NextOfKinController {
    private final NextOfKinUseCase nextOfKinUseCase;
    private final NextOfKinRestMapper nextOfKinRestMapper;

    @PostMapping("additional-details")
    public ResponseEntity<ApiResponse<NextOfKinResponse>> createNextOfKin(@RequestBody NextOfKinRequest request,
                                                                          @AuthenticationPrincipal Jwt meedlUserId) throws MeedlException {
        log.info("User ID =====> " + meedlUserId.getClaim("sub"));
        NextOfKin nextOfKin = nextOfKinRestMapper.toNextOfKin(request, meedlUserId.getClaimAsString("sub") );
        NextOfKin createdNextOfKin = nextOfKinUseCase.saveAdditionalDetails(nextOfKin);
        return ResponseEntity.ok(ApiResponse.<NextOfKinResponse>builder()
               .data(nextOfKinRestMapper.toNextOfKinResponse(createdNextOfKin))
               .message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage())
               .statusCode(HttpStatus.OK.name())
               .build());
    }
}
