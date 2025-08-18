package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;


import africa.nkwadoma.nkwadoma.application.ports.input.meedlnotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.ApiResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.appResponse.PaginatedResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlResponse.MeedlNotificationCountResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.meedlResponse.MeedlNotificationReponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.mapper.meedlnotification.MeedlNotificationRestMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.constants.ControllerConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.SuccessMessages.*;

@RestController
@RequiredArgsConstructor
@Slf4j
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
                meedlNotifications.getTotalPages(),meedlNotifications.getTotalElements(),pageNumber,pageSize
        );
        ApiResponse<PaginatedResponse<MeedlNotificationReponse>> apiResponse =
                ApiResponse.<PaginatedResponse<MeedlNotificationReponse>>builder()
                        .data(response)
                        .message(ALL_NOTIFICATION_VIEW_SUCCESSFULLY)
                        .statusCode(HttpStatus.OK.toString())
                        .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("search-notification")
    private ResponseEntity<ApiResponse<?>> searchNotification(@AuthenticationPrincipal Jwt meedlUser,
                                                              @RequestParam(name = "title") String title,
                                                              @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                                              @RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber) throws MeedlException {
        Page<MeedlNotification> meedlNotifications =
                meedlNotificationUsecase.searchNotification(meedlUser.getClaimAsString("sub"),title,pageSize,pageNumber);
        List<MeedlNotificationReponse> meedlNotificationReponse =
                meedlNotifications.stream().map(meedlNotificationRestMapper::toMeedlNotificationResponse)
                        .collect(Collectors.toList());
        PaginatedResponse<MeedlNotificationReponse> response = new PaginatedResponse<>(
                meedlNotificationReponse, meedlNotifications.hasNext(),
                meedlNotifications.getTotalPages(),meedlNotifications.getTotalElements(),pageNumber,pageSize
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
    private ResponseEntity<ApiResponse<?>> notificationCount(@AuthenticationPrincipal Jwt meedlUser) throws MeedlException {
        MeedlNotification numberOfUnReadNotification =
                meedlNotificationUsecase.fetchNotificationCount(meedlUser.getClaimAsString("sub"));
        MeedlNotificationCountResponse meedlNotificationCountResponse =
                meedlNotificationRestMapper.toMeedlNotificationCountResponse(numberOfUnReadNotification);
        ApiResponse<MeedlNotificationCountResponse> apiResponse =
                ApiResponse.<MeedlNotificationCountResponse>builder()
                        .data(meedlNotificationCountResponse)
                        .message(NOTIFICATION_COUNT)
                        .statusCode(HttpStatus.OK.toString())
                        .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
    
    @DeleteMapping("notification/delete")
    private ResponseEntity<ApiResponse<?>> deleteNotifications(@AuthenticationPrincipal Jwt meedlUser, @RequestParam List<String> notificationIds) throws MeedlException {

        meedlNotificationUsecase.deleteMultipleNotification(meedlUser.getClaimAsString("sub"), notificationIds);

        return new ResponseEntity<>(ApiResponse.builder()
                .statusCode(HttpStatus.OK.toString())
                .message("Notification " + ControllerConstant.DELETED_SUCCESSFULLY.getMessage()).build(), HttpStatus.OK);
    }



}
