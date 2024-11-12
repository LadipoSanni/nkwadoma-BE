package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;


@RequestMapping(BASE_URL)
@RequiredArgsConstructor
@RestController
public class NextOfKinController {
    private final CreateNextOfKinUseCase createNextOfKinUseCase;
    private final NextOfKinRestMapper nextOfKinMapper;

    @PostMapping("/next-of-kin")
    public NextOfKinResponse createNextOfKin(@RequestBody NextOfKinRequest request) {
        nextOfKinMapper
        return createNextOfKinUseCase.createNextOfKin(request);
    }
}
