package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education.CohortResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlResponse.MeedlReponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.meedlNotification.MeedlNotificationRestMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.NOTIFICATION_VIEW_SUCCESSFULLY;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.cohort.SuccessMessages.COHORT_EDITED_SUCCESSFULLY;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class MeedlNotificationController {


    private final MeedlNotificationUsecase meedlNotificationUsecase;
    private final MeedlNotificationRestMapper meedlNotificationRestMapper;


    @GetMapping("view-notification")
    public ResponseEntity<ApiResponse<?>> viewNotification(@AuthenticationPrincipal Jwt meedlUser,
                                                           @RequestParam
                                                           @NotBlank(message = "notification id is required")
                                                           String notificationId) throws MeedlException {
        MeedlNotification meedlNotification = meedlNotificationUsecase.viewNotification(meedlUser.getClaimAsString("sub"),
                notificationId);
        MeedlReponse meedlReponse =
                meedlNotificationRestMapper.toMeedlResponse(meedlNotification);
        ApiResponse<MeedlReponse> apiResponse = ApiResponse.<MeedlReponse>builder()
                .data(meedlReponse)
                .message(NOTIFICATION_VIEW_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
