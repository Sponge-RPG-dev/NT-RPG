package cz.neumimto.rpg.spigot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.ICharacterService;
import cz.neumimto.rpg.common.commands.AdminCommandFacade;
import cz.neumimto.rpg.common.commands.CommandProcessingException;

import javax.inject.Inject;

@CommandAlias("nadmin|na")
public abstract class AbstractAdminCommands<C, T> extends BaseCommand {

    @Inject
    private AdminCommandFacade adminCommandFacade;

    @Inject
    protected ICharacterService characterService;


    public void _effectAddCommand(C c, @Flags("target") T target, IGlobalEffect effect, long duration, @Default("{}") String[] args) {
        String data = String.join("", args);
        IActiveCharacter character = toCharacter(target);
        try {
            adminCommandFacade.commandAddEffectToPlayer(data, effect, duration, character);
        } catch (CommandProcessingException e) {
            sendMessageC(c, e.getMessage());
        }
    }

    protected abstract IActiveCharacter toCharacter(T t);

    protected abstract void sendMessageC(C c, String message);

    protected abstract void sendMessageT(T t, String message);
}
