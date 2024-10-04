package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class IdentityManagerController {
    private final IdentityManagerOutPutPort identityManagerOutPutPort;
    private final IdentityMapper identityMapper;

    @PostMapping("auth/login")
    public ResponseEntity<?> login(@RequestBody UserIdentityRequest userIdentityRequest) throws MiddlException {
        UserIdentity userIdentity = identityMapper.toIdentity(userIdentityRequest);
        return ResponseEntity.ok(identityManagerOutPutPort.login(userIdentity));
    }
}
