package eu.builderscoffee.commons.common.utils;

import lombok.val;

import java.util.HashMap;
import java.util.Map;

public class Cache<K, V> {
    
    private final Map<K, V> cache = new HashMap<>();

    /**
     * @param key Clé
     * @return La valeur contenue par le cache ou null si elle n'existe pas.
     */
    public V get(K key) {
        synchronized (cache) {
            return cache.get(key);
        }
    }

    /**
     * Ajoute la clé ainsi que sa valeur au cache
     *
     * @param key Clé
     * @param value Valeur
     */
    public void put(K key, V value) {
        synchronized (cache) {
            cache.put(key, value);
        }
    }

    /**
     * @param key Clé
     * @param defaultValue Valeur par défaut
     * @return La valeur associé à la clé si elle existe, sinon ajoute la valeur par défaut à
     * la clé et la retourne.
     */
    public V getOrPut(K key, V defaultValue) {
        synchronized (cache) {
            val value = cache.get(key);
            if(value == null) {
                cache.put(key, defaultValue);
                return defaultValue;
            }
            return cache.get(key);
        }
    }

    /**
     * Supprime la clé du cache ainsi que sa valeur.
     *
     * @param key Clé
     */
    public void remove(K key) {
        synchronized (cache) {
            cache.remove(key);
        }
    }
    
}
