package ch.heigvd.api.mailrobot.util;

import java.util.Properties;

/**
 * Étends la classe Properties pour faciliter son utilisation.
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public class CustomProperties extends Properties {

    /**
     * Cherche la valeur entière associée à la clé passée en paramètre.
     * Si cette dernière est introuvable ou invalide, la valeur par défaut fournie est retournée.
     *
     * @param key          la clé à chercher
     * @param defaultValue la valeur par défaut à utiliser en cas d'erreur
     * @return la valeur trouvée ou la valeur par défaut en cas d'erreur
     */
    public int getInteger(String key, int defaultValue) {
        int value;
        try {
            value = Integer.parseInt(getProperty(key));
        } catch (NumberFormatException e) {
            value = defaultValue;
        }
        return value;
    }
}
