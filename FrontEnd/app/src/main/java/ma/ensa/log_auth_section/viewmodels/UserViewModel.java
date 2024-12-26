package ma.ensa.log_auth_section.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import ma.ensa.log_auth_section.models.AuthRequest;
import ma.ensa.log_auth_section.models.AuthResponse;
import ma.ensa.log_auth_section.models.User;
import ma.ensa.log_auth_section.repository.RepositoryCallback;
import ma.ensa.log_auth_section.repository.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final LiveData<Boolean> authState;

    public UserViewModel(Application application) {
        super(application);
        repository = UserRepository.getInstance(application);

        // Déterminer l'état d'authentification en fonction de la présence d'un utilisateur
        authState = Transformations.map(repository.getUserLiveData(), user -> user != null);
    }

    public LiveData<User> getUser() {
        return repository.getUserLiveData();
    }

    public LiveData<String> getError() {
        return repository.getErrorLiveData();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getAuthState() {
        return authState;
    }

    public void login(String email, String password) {
        isLoading.setValue(true);
        AuthRequest authRequest = new AuthRequest(email, password); // Création de l'objet AuthRequest
        repository.login(authRequest, new RepositoryCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse response) {
                isLoading.postValue(false);
                // Vous pouvez aussi récupérer l'utilisateur ici si nécessaire
                // userLiveData.setValue(response.getUser()); // Exemple de mise à jour
            }

            @Override
            public void onError(String error) {
                isLoading.postValue(false);
                // Notifie l'UI de l'erreur
            }
        });
    }


    public void register(User user) {
        Log.d("UserViewModel", "Démarrage de l'inscription");
        isLoading.setValue(true);
        repository.register(user, new RepositoryCallback<AuthResponse>() {
            @Override
            public void onSuccess(AuthResponse response) {
                Log.d("UserViewModel", "Inscription réussie");
                isLoading.postValue(false);
            }

            @Override
            public void onError(String error) {
                Log.e("UserViewModel", "Erreur d'inscription : " + error);
                isLoading.postValue(false);
            }
        });
    }


    public void logout() {
        repository.logout(new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void response) {}

            @Override
            public void onError(String error) {}
        });
    }
}
