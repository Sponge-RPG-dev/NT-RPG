package cz.neumimto.configuration;

import com.typesafe.config.Config;

public interface IMarshaller<T> {
    /**
     * Converts object to String
     *
     * @param t Object which is going to be serialized
     * @return String
     */
    String marshall(T t);

    /**
     * Converts String to object
     *
     * @param string
     * @return
     */
    T unmarshall(Config string);
}