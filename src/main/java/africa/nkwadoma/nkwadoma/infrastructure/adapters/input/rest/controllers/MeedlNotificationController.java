package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.investmentVehicle.FinancierResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlResponse.MeedlNotificationCountResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlResponse.MeedlNotificationReponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.meedlNotification.MeedlNotificationRestMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    private ResponseEntity<ApiResponse<?>> viewAllNotification(@AuthenticationPrincipal Jwt meedlUser,
                                                               @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                               @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        Page<MeedlNotification> meedlNotifications =
                meedlNotificationUsecase.viewAllNotification(meedlUser.getClaimAsString("sub"),pageSize,pageNumber);
        List<MeedlNotificationReponse> meedlNotificationReponse =
                meedlNotifications.stream().map(meedlNotificationRestMapper::toMeedlNotificationResponse)
                        .collect(Collectors.toList());
        PaginatedResponse<MeedlNotificationReponse> response = new PaginatedResponse<>(
                meedlNotificationReponse, meedlNotifications.hasNext(),
                meedlNotifications.getTotalPages(),pageNumber,pageSize
        );
        ApiResponse<PaginatedResponse<MeedlNotificationReponse>> apiResponse =
                ApiResponse.<PaginatedResponse<MeedlNotificationReponse>>builder()
                        .data(response)
                        .message(ALL_NOTIFICATION_VIEW_SUCCESSFULLY)
                        .statusCode(HttpStatus.OK.toString())
                        .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("notification-count")
    private ResponseEntity<ApiResponse<?>> unreadNotificationCount(@AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        MeedlNotification numberOfUnReadNotification =
                meedlNotificationUsecase.getNumberOfUnReadNotification(meedlUser.getClaimAsString("sub"));
        MeedlNotificationCountResponse meedlNotificationCountResponse =
                meedlNotificationRestMapper.toMeedlNotificationCountResponse(numberOfUnReadNotification);
        ApiResponse<MeedlNotificationCountResponse> apiResponse =
                ApiResponse.<MeedlNotificationCountResponse>builder()
                        .data(meedlNotificationCountResponse)
                        .message(COUNT_OF_UNREAD_NOTIFICATION)
                        .statusCode(HttpStatus.OK.toString())
                        .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
