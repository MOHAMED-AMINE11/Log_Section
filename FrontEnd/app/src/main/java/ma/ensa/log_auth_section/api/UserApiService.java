package ma.ensa.log_auth_section.api;

import ma.ensa.log_auth_section.models.AuthRequest;
import ma.ensa.log_auth_section.models.AuthResponse;
import ma.ensa.log_auth_section.models.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApiService {
    @GET("api/users/{id}")
    Call<User> getUserById(@Path("id") int id);

    @GET("api/users/email/{email}")
    Call<User> getUserByEmail(@Path("email") String email);

    @PUT("api/users/{id}")
    Call<User> updateUser(@Path("id") int id, @Body User user);

    // Nouvelles méthodes pour l'authentification
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @POST("api/auth/signup")
    Call<AuthResponse> register(@Body User user);

    @POST("api/auth/logout")
    Call<Void> logout(@Header("Authorization") String token);
}
