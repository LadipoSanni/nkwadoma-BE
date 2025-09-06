package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.constants.ControllerConstant;
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
public class AdditionalDetailController {
    private final AdditionalDetail additionalDetail;
    private final AdditionalDetailMapper additionalDetailMapper;

    @PostMapping("additional-details")
    public ResponseEntity<ApiResponse<NextOfKinResponse>> additionalDetails(@RequestBody africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.AdditionalDetail request,
                                                                            @AuthenticationPrincipal Jwt meedlUserId) throws MeedlException {
        String id = meedlUserId.getClaim("sub");
        log.info("User ID =====> {}", id);
        NextOfKin nextOfKin = additionalDetailMapper.map(request, meedlUserId.getClaimAsString("sub") );
        NextOfKin createdNextOfKin = additionalDetail.saveAdditionalDetails(nextOfKin);
        return ResponseEntity.ok(ApiResponse.<NextOfKinResponse>builder()
               .data(additionalDetailMapper.toNextOfKinResponse(createdNextOfKin))
               .message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage())
               .statusCode(HttpStatus.OK.name())
               .build());
    }
}
