package interview.guide.modules.auth.model;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserInfo user;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
    }
}