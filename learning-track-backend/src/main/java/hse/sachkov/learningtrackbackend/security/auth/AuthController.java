package hse.sachkov.learningtrackbackend.security.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.AbstractMap;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public void register(@RequestBody AuthRequest registerRequest) {
        log.info("Start processing POST /auth/register request . . .");

        authService.register(registerRequest);

        log.info("POST /auth/register request processed!");
    }

    @PostMapping("/login")
    public void login(@RequestBody AuthRequest loginRequest, HttpServletResponse response) {
        log.info("Start processing POST /auth/login request . . .");

        AbstractMap.SimpleEntry<String, String> tokens = authService.login(loginRequest);
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getKey());
        response.addHeader("Refresh-Token", "Bearer " + tokens.getValue());
        response.addHeader("Access-Control-Expose-Headers", "*");

        log.info("POST /auth/login request processed!");
    }

    @PostMapping()
    public void auth(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        log.info("Start processing POST /auth request . . .");

        if (authRequest.getPassword() == null || authRequest.getPassword().isEmpty()) {
            authService.auth(authRequest);
        } else {
            AbstractMap.SimpleEntry<String, String> tokens = authService.loginWithCode(authRequest);
            response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getKey());
            response.addHeader("Refresh-Token", "Bearer " + tokens.getValue());
            response.addHeader("Access-Control-Expose-Headers", "*");
        }

        log.info("POST /auth request processed!");
    }
}
