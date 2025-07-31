package org.example.eiscuno.model.prototype;

/**
 * Interface to the Prototype patron.
 */
public interface IPrototype<T> {
    /**
     * Creates a new copy of the original Object.
     * @return a new instance copied from the Object.
     */
    T clone();
}
