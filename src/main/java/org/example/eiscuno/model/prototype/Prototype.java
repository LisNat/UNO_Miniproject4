package org.example.eiscuno.model.prototype;

/**
 * Interfaz para el patrón Prototype
 */
public interface Prototype<T> {
    /**
     * Crea una copia del objeto actual
     * @return Una nueva instancia copiada del objeto
     */
    T clone();
}
