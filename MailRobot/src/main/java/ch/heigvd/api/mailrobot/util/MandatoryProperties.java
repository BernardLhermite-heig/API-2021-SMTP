package ch.heigvd.api.mailrobot.util;

import java.util.MissingFormatArgumentException;
import java.util.Properties;

/**
 * Étends la classe Properties pour rajouter une méthode lançant une exception
 * si une propriété est manquante.
 *
 * @author Stéphane Marengo
 * @author Loris Marzullo
 */
public class MandatoryProperties extends Properties {
    /**
     * Retourne la valeur liée à la clé passée en paramètre
     *
     * @param key la clé à chercher dans le fichier
     * @return la valeur liée à la clé
     * @throws MissingFormatArgumentException si la clé est introuvable
     */
    public String getMandatoryProperty(String key) {
        String prop = super.getProperty(key);
        if (prop == null)
            throw new MissingFormatArgumentException("Missing property " + key + " in properties file.");
        return prop;
    }
}
