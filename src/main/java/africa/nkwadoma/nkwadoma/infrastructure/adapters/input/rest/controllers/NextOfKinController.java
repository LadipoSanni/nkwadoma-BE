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
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;


@RequestMapping(BASE_URL)
@RequiredArgsConstructor
@RestController
public class NextOfKinController {
    private final CreateNextOfKinUseCase createNextOfKinUseCase;
    private final NextOfKinRestMapper nextOfKinRestMapper;

    @PostMapping("next-of-kin-details")
    public ResponseEntity<ApiResponse<NextOfKinResponse>> createNextOfKin(@RequestBody NextOfKinRequest request) throws MeedlException {
        NextOfKin nextOfKin = nextOfKinRestMapper.toNextOfKin(request);
        NextOfKin createdNextOfKin = createNextOfKinUseCase.createNextOfKin(nextOfKin);
        return ResponseEntity.ok(ApiResponse.<NextOfKinResponse>builder()
               .data(nextOfKinRestMapper.toNextOfKinResponse(createdNextOfKin))
               .message(ControllerConstant.RESPONSE_IS_SUCCESSFUL.getMessage())
               .statusCode(HttpStatus.OK.name())
               .build());
    }
}
