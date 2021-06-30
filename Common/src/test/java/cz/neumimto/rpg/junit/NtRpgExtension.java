package cz.neumimto.rpg.junit;

import com.google.inject.Injector;
import cz.neumimto.rpg.RpgTest;
import cz.neumimto.rpg.TestApiImpl;
import cz.neumimto.rpg.api.RpgApi;
import cz.neumimto.rpg.api.logging.Log;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.engine.execution.NamespaceAwareStore;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class NtRpgExtension implements BeforeAllCallback, TestInstancePostProcessor {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        new TestDictionary().reset();
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        Field injector_cache = GuiceExtension.class.getDeclaredField("NAMESPACE");
        injector_cache.setAccessible(true);
        ExtensionContext.Namespace namespace = (ExtensionContext.Namespace) injector_cache.get(null);

        AnnotatedElement element = context.getElement().get();
        var store = context.getStore(namespace);
        var i = (Injector) store.get(testInstance.getClass());
        if (i != null) {
            TestApiImpl instance = i.getInstance(TestApiImpl.class);
            new RpgTest(instance);
        }
    }
}
