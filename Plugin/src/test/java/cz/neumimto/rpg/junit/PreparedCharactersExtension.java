package cz.neumimto.rpg.junit;

import cz.neumimto.rpg.players.ActiveCharacter;
import cz.neumimto.rpg.players.CharacterBase;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.UUID;

public class PreparedCharactersExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == IActiveCharacter.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return generateCharacter();
    }

    private IActiveCharacter generateCharacter() {
        CharacterBase characterBase = new CharacterBase();

        return new ActiveCharacter(UUID.randomUUID(), characterBase);
    }
}
