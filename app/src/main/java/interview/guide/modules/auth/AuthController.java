package interview.guide.modules.auth;

import interview.guide.common.result.Result;
import interview.guide.modules.auth.model.LoginRequest;
import interview.guide.modules.auth.model.LoginResponse;
import interview.guide.modules.auth.model.UserEntity;
import interview.guide.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        var response = authService.login(loginRequest);
        return Result.success(response);
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserEntity userEntity) {
        authService.register(userEntity);
        return Result.success();
    }
}