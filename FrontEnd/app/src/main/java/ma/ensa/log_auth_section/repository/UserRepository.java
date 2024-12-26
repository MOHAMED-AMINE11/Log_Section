package ma.ensa.log_auth_section.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.IOException;

import ma.ensa.log_auth_section.api.RetrofitClient;
import ma.ensa.log_auth_section.api.UserApiService;
import ma.ensa.log_auth_section.models.AuthRequest;
import ma.ensa.log_auth_section.models.AuthResponse;
import ma.ensa.log_auth_section.models.User;
import ma.ensa.log_auth_section.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private static UserRepository instance;
    private final UserApiService apiService;
    private final SessionManager sessionManager;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private UserRepository(Context context) {
        apiService = RetrofitClient.getInstance().getUserApiService();
        sessionManager = SessionManager.getInstance(context);
    }

    public static synchronized UserRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository(context.getApplicationContext());
        }
        return instance;
    }

    public void login(AuthRequest authRequest, RepositoryCallback<AuthResponse> callback) {
        apiService.login(authRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    sessionManager.saveAuthToken(authResponse.getToken());
                    sessionManager.saveUser(authResponse.getUser());
                    userLiveData.postValue(authResponse.getUser());
                    callback.onSuccess(authResponse);
                } else {
                    String errorMessage = response.message();  // Message de l'API en cas d'erreur
                    errorLiveData.postValue(errorMessage);  // Envoie l'erreur dans LiveData
                    callback.onError("Échec de la connexion: " + errorMessage);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                errorLiveData.postValue("Erreur réseau: " + t.getMessage());
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }

    public void register(User user, RepositoryCallback<AuthResponse> callback) {
        apiService.register(user).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("UserRepository", "Inscription réussie !");
                    AuthResponse authResponse = response.body();
                    sessionManager.saveAuthToken(authResponse.getToken());
                    sessionManager.saveUser(authResponse.getUser());
                    userLiveData.postValue(authResponse.getUser());
                    callback.onSuccess(authResponse);
                } else {
                    try {
                        // Loggez les détails de l'erreur
                        Log.d("UserRepository", "Erreur lors de l'inscription : " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    errorLiveData.postValue(response.message());
                    callback.onError("Échec de l'inscription: " + response.message());
                }
            }



            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Log.e("UserRepository", "Erreur réseau : " + t.getMessage());
                errorLiveData.postValue("Erreur réseau: " + t.getMessage());
                callback.onError("Erreur réseau: " + t.getMessage());
            }
        });
    }


    public void logout(RepositoryCallback<Void> callback) {
        String token = sessionManager.getAuthToken();
        if (token != null) {
            apiService.logout("Bearer " + token).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        sessionManager.clearSession();
                        userLiveData.postValue(null);  // Déconnexion de l'utilisateur
                        callback.onSuccess(null);
                    } else {
                        errorLiveData.postValue("Erreur lors de la déconnexion.");
                        callback.onError("Erreur lors de la déconnexion.");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    errorLiveData.postValue("Erreur réseau: " + t.getMessage());
                    callback.onError("Erreur réseau: " + t.getMessage());
                }
            });
        } else {
            errorLiveData.postValue("Aucun utilisateur connecté.");
            callback.onError("Aucun utilisateur connecté.");
        }
    }

    public MutableLiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // Méthodes existantes...
}
