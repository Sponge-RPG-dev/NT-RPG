package cz.neumimto.rpg.common.events;

import cz.neumimto.nts.annotations.ScriptMeta;

import java.util.function.Supplier;

public interface EventFactoryService {

    <T> T createEventInstance(Class<? extends T> clazz);

    <T> void registerProvider(Class<T> clazz, Supplier<? extends T> provider);

    void registerEventProviders();

    default Class listenerSubclass() {
        return Object.class;
    }

    default Class listenerAnnotation() {
        return ScriptMeta.ScriptTarget.class; //just a dummy
    }

    Supplier<?> findBySimpleName(String name);
}
