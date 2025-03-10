package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlResponse.MeedlNotificationReponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.meedlNotification.MeedlNotificationRestMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.BASE_URL;

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
        MeedlNotificationReponse meedlNotificationReponse =
                meedlNotificationRestMapper.toMeedlNotificationResponse(meedlNotification);
        ApiResponse<MeedlNotificationReponse> apiResponse = ApiResponse.<MeedlNotificationReponse>builder()
                .data(meedlNotificationReponse)
                .message(NOTIFICATION_VIEW_SUCCESSFULLY)
                .statusCode(HttpStatus.OK.toString())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("view-all-notification")
    private ResponseEntity<ApiResponse<?>> viewAllNotification(@AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        List<MeedlNotification> meedlNotifications =
                meedlNotificationUsecase.viewAllNotification(meedlUser.getClaimAsString("sub"));
        List<MeedlNotificationReponse> meedlNotificationReponse =
                meedlNotificationRestMapper.toMeedlNotificationResponses(meedlNotifications);
        ApiResponse<List<MeedlNotificationReponse>> apiResponse =
                ApiResponse.<List<MeedlNotificationReponse>>builder()
                        .data(meedlNotificationReponse)
                        .message(ALL_NOTIFICATION_VIEW_SUCCESSFULLY)
                        .statusCode(HttpStatus.OK.toString())
                        .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("unread-notification-count")
    private ResponseEntity<ApiResponse<?>> unreadNotificationCount(@AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        int numberOfUnReadNotification =
                meedlNotificationUsecase.getNumberOfUnReadNotification(meedlUser.getClaimAsString("sub"));
        ApiResponse<Integer> apiResponse =
                ApiResponse.<Integer>builder()
                        .data(numberOfUnReadNotification)
                        .message(COUNT_OF_UNREAD_NOTIFICATION)
                        .statusCode(HttpStatus.OK.toString())
                        .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
