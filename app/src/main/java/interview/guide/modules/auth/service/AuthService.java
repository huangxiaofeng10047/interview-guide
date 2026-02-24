package interview.guide.modules.auth.service;

import interview.guide.modules.auth.model.LoginRequest;
import interview.guide.modules.auth.model.LoginResponse;
import interview.guide.modules.auth.model.UserEntity;
import interview.guide.modules.auth.repository.UserRepository;
import interview.guide.modules.auth.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       UserDetailsServiceImpl userDetailsService,
                       JwtUtil jwtUtil,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // 设置认证上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成JWT令牌
        var userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        var token = jwtUtil.generateToken(userDetails);

        // 构建响应
        var user = (UserEntity) userDetails;
        var userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());

        var response = new LoginResponse();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtUtil.getExpirationMs() / 1000);
        response.setUser(userInfo);

        return response;
    }

    public void register(UserEntity userEntity) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(userEntity.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(userEntity.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 加密密码
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        // 保存用户
        userRepository.save(userEntity);
    }
}