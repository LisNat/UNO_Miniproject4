package org.example.eiscuno.model.serializable;

import java.io.Serializable;
/**
 * Interface for handling serialization and deserialization of objects to and from files.
 */
public interface ISerializableFileHandler {
    void serialize(String filename, Object element);
    Object deserialize(String filename);
}
