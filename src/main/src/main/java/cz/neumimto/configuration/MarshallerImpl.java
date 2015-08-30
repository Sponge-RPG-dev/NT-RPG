package cz.neumimto.configuration;

import com.typesafe.config.Config;

/**
 *
 */
public class MarshallerImpl implements IMarshaller {
    @Override
    public String marshall(Object o) {
        throw new UnsupportedOperationException("Config loader is missing logic for converting some config nodes to string");
    }

    @Override
    public Object unmarshall(Config string) {
        throw new UnsupportedOperationException("Config loader is missing logic for reading some config nodes from string");
    }
}