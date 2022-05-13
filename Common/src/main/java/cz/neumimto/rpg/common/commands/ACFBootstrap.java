package cz.neumimto.rpg.common.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandManager;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import co.aikar.locales.MessageKey;
import cz.neumimto.rpg.common.Rpg;
import cz.neumimto.rpg.common.configuration.AttributeConfig;
import cz.neumimto.rpg.common.effects.IGlobalEffect;
import cz.neumimto.rpg.common.entity.players.IActiveCharacter;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.common.entity.players.classes.PlayerClassData;
import cz.neumimto.rpg.common.skills.ISkill;
import cz.neumimto.rpg.common.skills.PlayerSkillContext;
import cz.neumimto.rpg.common.skills.SkillData;
import cz.neumimto.rpg.common.skills.tree.SkillTree;

import java.util.*;
import java.util.stream.Collectors;

public class ACFBootstrap {


    public static void initializeACF(CommandManager manager, List<BaseCommand> commandClasses) {

        manager.getCommandCompletions().registerAsyncCompletion("geffect", c ->
                Rpg.get().getEffectService().getGlobalEffects().keySet()
        );

        manager.getCommandContexts().registerContext(IGlobalEffect.class, c -> {
            String s = c.popFirstArg();
            return Rpg.get().getEffectService().getGlobalEffect(s.toLowerCase());
        });

        manager.getCommandCompletions().registerAsyncCompletion("skilltree", c ->
                Rpg.get().getSkillService().getSkillTrees().keySet()
        );

        manager.getCommandContexts().registerContext(SkillTree.class, c -> {
            String s = c.popFirstArg();
            SkillTree skillTree = Rpg.get().getSkillService().getSkillTrees().get(s);
            return skillTree;
        });

        //may be async as only way to add skills now is to reload ntrpg
        manager.getCommandCompletions().registerAsyncCompletion("skill", c ->
                Rpg.get().getSkillService().getSkillNames()
        );

        manager.getCommandCompletions().registerAsyncCompletion("skillskctx", c -> {
            SkillTree tree = (SkillTree) c.getContextValue(SkillTree.class);
            if (tree != null) {
                return tree.getSkills().values().stream()
                        .map(SkillData::getSkillName)
                        .collect(Collectors.toSet());
            } else {
                return Collections.emptySet();
            }
        });

        manager.getCommandCompletions().registerAsyncCompletion("issuerclasses", c -> {
            UUID uniqueId = c.getIssuer().getUniqueId();
            List<String> list = new ArrayList<>();
            Map<String, PlayerClassData> classes = Rpg.get().getCharacterService().getCharacter(uniqueId).getClasses();
            for (PlayerClassData value : classes.values()) {
                list.add(value.getClassDefinition().getName());
            }
            return list;

        });

        //may not be async as playercontext changes at any time
        manager.getCommandCompletions().registerCompletion("learnedskill", c -> {
            UUID uuid = c.getIssuer().getUniqueId();
            IActiveCharacter character = Rpg.get().getCharacterService().getCharacter(uuid);
            Map<String, PlayerSkillContext> skills = character.getSkillsByName();
            return skills.keySet();
        });

        manager.getCommandContexts().registerContext(ISkill.class, c -> {
            String s = c.popFirstArg();
            return Rpg.get().getSkillService().getById(s).orElseThrow(() -> new RuntimeException("Unknown skill " + c));
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

            String s = c.popFirstArg();
            ClassDefinition cw = Rpg.get().getClassService().getClassDefinitionByName(s);
            if (cw != null) {
                return cw;
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

        manager.getCommandCompletions().registerAsyncCompletion("classtypes", c ->
                Rpg.get().getPluginConfig().CLASS_TYPES.keySet()
        );

        manager.getCommandCompletions().registerCompletion("party-current", c -> {
            UUID uniqueId = c.getIssuer().getUniqueId();
            Set<IActiveCharacter> players = Rpg.get().getCharacterService().getCharacter(uniqueId)
                    .getParty()
                    .getPlayers();
            return players.stream().map(IActiveCharacter::getPlayerAccountName).collect(Collectors.toList());
        });

        manager.getCommandCompletions().registerAsyncCompletion("learned-skill", c ->
                Rpg.get().getCharacterService().getCharacter(c.getIssuer().getUniqueId()).getSkills().keySet()
        );


        manager.getCommandCompletions().registerAsyncCompletion("runeword", c -> {
            return Collections.emptyList();
            //todo
        });

        manager.getCommandContexts().registerIssuerOnlyContext(IActiveCharacter.class, c -> {
            UUID uniqueId = c.getIssuer().getUniqueId();
            return Rpg.get().getCharacterService().getCharacter(uniqueId);
        });

        ContextResolver resolver = manager.getCommandContexts().getResolver(OnlineOtherPlayer.class);
        if (resolver == null) {
            throw new IllegalStateException("Required to register OnlineOtherPlayer acf context resolver!!");
        }

        manager.getCommandCompletions().registerAsyncCompletion("@skillbook", c -> {
            UUID uuid = c.getIssuer().getUniqueId();
            IActiveCharacter character = Rpg.get().getCharacterService().getCharacter(uuid);
            Map<String, PlayerSkillContext> skills = character.getSkillsByName();
            Set<String> set = new HashSet<>(skills.keySet());
            set.add("-");
            return set;
        });

        manager.getCommandContexts().registerIssuerAwareContext(ISkill[].class, c -> {
            UUID uuid = c.getIssuer().getUniqueId();
            IActiveCharacter character = Rpg.get().getCharacterService().getCharacter(uuid);
            int i = 0;
            while (!c.isLastArg()) {
                String s = c.popFirstArg();
                i++;
            }
            if (i != 8) {
                c.getIssuer().sendError(MessageKey.of(""));
            }
            return null;
        });

        for (BaseCommand o : commandClasses) {
            manager.registerCommand(o);
        }
    }
}
