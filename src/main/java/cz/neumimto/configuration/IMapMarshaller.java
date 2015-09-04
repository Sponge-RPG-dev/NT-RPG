package cz.neumimto.configuration;

import com.typesafe.config.Config;

import java.util.Map;

/**
 * Created by NeumimTo.
 */
public interface IMapMarshaller<K, V> extends IMarshaller<Map.Entry<K, V>> {
    public static final String KVSeparator = ":";

    @Override
    public String marshall(Map.Entry<K, V> kvEntry);

    @Override
    public Map.Entry<K, V> unmarshall(Config string);
}