package ma.ensa.log_auth_section.models;

public class AuthResponse {
    private String token;
    private User user;
    private String message;

    public AuthResponse(String token, User user, String message) {
        this.token = token;
        this.user = user;
        this.message = message;
    }

    // Getters
    public String getToken() { return token; }
    public User getUser() { return user; }
    public String getMessage() { return message; }
}

