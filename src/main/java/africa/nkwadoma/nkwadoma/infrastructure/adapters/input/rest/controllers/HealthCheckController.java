package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers;

import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actuator/health")
public class HealthCheckController {

    @GetMapping
    public Health healthCheck() {

        return Health.up().build();
    }
}
