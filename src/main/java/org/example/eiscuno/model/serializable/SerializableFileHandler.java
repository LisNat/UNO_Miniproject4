package org.example.eiscuno.model.serializable;

import java.io.*;
import org.example.eiscuno.model.serializable.ISerializableFileHandler;
/**
 * Implementation of {@link ISerializableFileHandler} for serializing and deserializing objects
 * to and from files using Java's built-in serialization mechanism.
 * <p>
 * Provides methods to save an object to a file and read an object from a file,
 * handling IO and class-not-found exceptions internally.
 */
public class SerializableFileHandler implements ISerializableFileHandler {
    @Override
    public void serialize(String filename, Object element) {
        try (ObjectOutputStream obs = new ObjectOutputStream(new FileOutputStream(filename))) {
            obs.writeObject(element);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Deserializes and returns an object from the specified file.
     *
     * @param filename the name of the file to read the object from
     * @return the deserialized object, or null if an error occurs
     */
    @Override
    public Object deserialize(String filename) {
        try (ObjectInputStream inp = new ObjectInputStream(new FileInputStream(filename))) {
            return (Object) inp.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
