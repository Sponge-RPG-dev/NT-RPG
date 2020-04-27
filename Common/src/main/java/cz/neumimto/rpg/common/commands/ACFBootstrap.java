package cz.neumimto.rpg.common.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.api.configuration.AttributeConfig;
import cz.neumimto.rpg.api.effects.IGlobalEffect;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.api.skills.ISkill;
import cz.neumimto.rpg.api.skills.PlayerSkillContext;
import cz.neumimto.rpg.api.skills.tree.SkillTree;
import cz.neumimto.rpg.common.inventory.runewords.RuneWord;

import java.util.*;
import java.util.stream.Collectors;

public class ACFBootstrap {


    public static void initializeACF(CommandManager manager, List<BaseCommand> commandClasses) {

        manager.getCommandCompletions().registerAsyncCompletion("effect", c ->
                Rpg.get().getEffectService().getGlobalEffects().keySet()
        );

        manager.getCommandContexts().registerContext(IGlobalEffect.class, c -> {
            String s = c.getFirstArg();
            return Rpg.get().getEffectService().getGlobalEffect(s.toLowerCase());
        });

        manager.getCommandCompletions().registerAsyncCompletion("skilltree", c ->
                Rpg.get().getSkillService().getSkillTrees().keySet()
        );

        manager.getCommandContexts().registerContext(SkillTree.class, c -> {
            String s = c.getFirstArg();
            return Rpg.get().getSkillService().getSkillTrees().get(s);
        });

        //may be async as only way to add skills now is to reload ntrpg
        manager.getCommandCompletions().registerAsyncCompletion("skill", c ->
                Rpg.get().getSkillService().getSkillNames()
        );

        //may not be async as playercontext changes at any time
        manager.getCommandCompletions().registerCompletion("learnedskill", c -> {
            UUID uuid = c.getIssuer().getUniqueId();
            IActiveCharacter character = Rpg.get().getCharacterService().getCharacter(uuid);
            Map<String, PlayerSkillContext> skills = character.getSkillsByName();
            return skills.keySet();
        });

        manager.getCommandContexts().registerContext(ISkill.class, c -> {
            String s = c.joinArgs();

            return Rpg.get().getSkillService().getSkillByLocalizedName(s.toLowerCase());
        });


        manager.getCommandCompletions().registerAsyncCompletion("class-any", c ->
                Rpg.get().getClassService().getClasses().keySet()
        );

        manager.getCommandCompletions().registerAsyncCompletion("class", c ->
                Rpg.get().getClassService().getClassDefinitions().stream()
                .filter(a -> c.getIssuer().hasPermission("ntrpg.class." + a.getName().toLowerCase()))
                .map(ClassDefinition::getName)
                .collect(Collectors.toList())
        );

        manager.getCommandContexts().registerContext(ClassDefinition.class, c -> {
            String firstArg = "";
            while (c.getIndex() <= c.getArgs().size()) {
                firstArg += c.popFirstArg();
                ClassDefinition cw = Rpg.get().getClassService().getClassDefinitionByName(firstArg);
                if (cw != null) {
                    return cw;
                }
                firstArg += " ";
            }
            throw new InvalidCommandArgument("Unknown class " + firstArg);
        });

        manager.getCommandCompletions().registerAsyncCompletion("attribute", c ->
                Rpg.get().getPropertyService().getAttributes().keySet()
        );

        manager.getCommandContexts().registerContext(AttributeConfig.class, c -> {
            String firstArg = c.popFirstArg();
            return Rpg.get().getPropertyService().getAttributeById(firstArg).get();
        });

        manager.getCommandCompletions().registerAsyncCompletion("classtype", c ->
                Rpg.get().getPluginConfig().CLASS_TYPES.keySet()
        );

        manager.getCommandCompletions().registerCompletion("party-current", c-> {
            UUID uniqueId = c.getIssuer().getUniqueId();
            Set<IActiveCharacter> players = Rpg.get().getCharacterService().getCharacter(uniqueId)
                    .getParty()
                    .getPlayers();
            return players.stream().map(IActiveCharacter::getPlayerAccountName).collect(Collectors.toList());
        });

        manager.getCommandCompletions().registerAsyncCompletion("learned-skill", c->
                Rpg.get().getCharacterService().getCharacter(c.getIssuer().getUniqueId()).getSkills().keySet()
        );

        manager.getCommandCompletions().registerAsyncCompletion("socket-type", c->
                Rpg.get().getItemService().getSocketTypes().keySet()
        );

        manager.getCommandCompletions().registerAsyncCompletion("runeword", c-> {
            return Collections.emptyList();
            //todo
        });

        manager.getCommandContexts().registerContext(RuneWord.class, c -> {
            String firstArg = c.getFirstArg();
            c.popFirstArg();
            //todo
            return new RuneWord();
        });

        manager.getCommandContexts().registerIssuerOnlyContext(IActiveCharacter.class, c -> {
            UUID uniqueId = c.getIssuer().getUniqueId();
            return Rpg.get().getCharacterService().getCharacter(uniqueId);
        });

        ContextResolver resolver = manager.getCommandContexts().getResolver(OnlineOtherPlayer.class);
        if (resolver == null) {
            throw new IllegalStateException("Required to register OnlineOtherPlayer acf context resolver!!");
        }


        for (BaseCommand o : commandClasses) {
            manager.registerCommand(o);
        }
    }
}
