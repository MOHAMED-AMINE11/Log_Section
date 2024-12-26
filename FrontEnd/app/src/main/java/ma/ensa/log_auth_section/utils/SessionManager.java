package ma.ensa.log_auth_section.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import ma.ensa.log_auth_section.models.User;

public class SessionManager {
    private static final String PREF_NAME = "FoodRescueSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER = "user"; // Stocker l'utilisateur entier en JSON

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private static SessionManager instance;

    private SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    // Sauvegarde du token d'authentification
    public void saveAuthToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    // Récupère le token d'authentification
    public String getAuthToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    // Sauvegarde l'utilisateur complet en tant que JSON
    public void saveUser(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user); // Convertit l'objet User en JSON
        editor.putString(KEY_USER, userJson);
        editor.apply();
    }

    // Récupère l'utilisateur complet depuis SharedPreferences
    public User getUser() {
        String userJson = prefs.getString(KEY_USER, null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class); // Convertit le JSON en objet User
        }
        return null; // Si aucun utilisateur n'est trouvé, retourne null
    }

    // Efface la session (y compris l'utilisateur et le token)
    public void clearSession() {
        editor.clear();
        editor.apply();
    }

    // Vérifie si l'utilisateur est connecté en vérifiant si le token et l'utilisateur sont présents
    public boolean isLoggedIn() {
        return getAuthToken() != null && getUser() != null;
    }
}
