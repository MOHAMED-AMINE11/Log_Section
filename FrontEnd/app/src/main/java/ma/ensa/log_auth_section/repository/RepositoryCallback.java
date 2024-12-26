package ma.ensa.log_auth_section.repository;

/**
 * Interface générique pour les callbacks dans le repository.
 * @param <T> Le type de l'objet retourné par le callback.
 */
public interface RepositoryCallback<T> {
    void onSuccess(T result);
    void onError(String error);
}
