package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.enums.constants.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;


@Slf4j
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
@RestController
public class NextOfKinController {
    private final CreateNextOfKinUseCase createNextOfKinUseCase;
    private final NextOfKinRestMapper nextOfKinRestMapper;

    @PostMapping("additional-details")
    public ResponseEntity<APIResponse<NextOfKinResponse>> createNextOfKin(@RequestBody NextOfKinRequest request,
                                                                          @AuthenticationPrincipal Jwt meedlUserId) throws MeedlException {
        log.info("User ID =====> " + meedlUserId.getClaim("sub"));
        NextOfKin nextOfKin = nextOfKinRestMapper.toNextOfKin(request);
        nextOfKin.getLoanee().getUserIdentity().setId(meedlUserId.getClaim("sub"));
        NextOfKin createdNextOfKin = createNextOfKinUseCase.saveAdditionalDetails(nextOfKin);
        return ResponseEntity.ok(APIResponse.<NextOfKinResponse>builder()
               .data(nextOfKinRestMapper.toNextOfKinResponse(createdNextOfKin))
               .message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage())
               .statusCode(HttpStatus.OK.name())
               .build());
    }
}
