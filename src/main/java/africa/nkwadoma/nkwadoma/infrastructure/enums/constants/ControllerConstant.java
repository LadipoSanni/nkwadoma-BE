package africa.nkwadoma.nkwadoma.infrastructure.enums.constants;

import lombok.*;

@Getter
public enum ControllerConstant {
    RESPONSE_IS_SUCCESSFUL("Response is successful");
    private final String message;

    ControllerConstant(String message) {
        this.message = message;
    }
}
