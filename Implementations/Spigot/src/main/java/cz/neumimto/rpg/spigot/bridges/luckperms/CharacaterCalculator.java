package cz.neumimto.rpg.spigot.bridges.luckperms;

import cz.neumimto.rpg.common.classes.ClassService;
import cz.neumimto.rpg.common.entity.players.classes.ClassDefinition;
import cz.neumimto.rpg.spigot.entities.players.ISpigotCharacter;
import cz.neumimto.rpg.spigot.entities.players.SpigotCharacterService;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class CharacaterCalculator implements ContextCalculator<Player> {
    private LuckPerms luckPerms;
    private final Set<Calculator> calculators = new HashSet<>();

    @Inject
    private SpigotCharacterService characterService;

    @Inject
    private ClassService classService;

    @Override
    public void calculate(@NonNull Player target, @NonNull ContextConsumer consumer) {
        var character = characterService.getCharacter(target);
        for (Calculator calculator : calculators) {
            calculator.function.apply(character).forEach(v -> consumer.accept(calculator.context, v));
        }
    }

    @Override
    public ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();

        for (Calculator calculator : this.calculators) {
            calculator.suggestions.get().forEach(value -> builder.add(calculator.context, value));
        }

        return builder.build();
    }

    public void registerContexts() {

        registerContext("ntrpg:has-class",
                c -> c.getClasses().values().stream().map(a->a.getClassDefinition().getName()).collect(Collectors.toSet()),
                () -> classService.getClasses().values().stream().map(ClassDefinition::getName).collect(Collectors.toSet()));


        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        luckPerms = provider.getProvider();
        luckPerms.getContextManager().registerCalculator(this);
    }

    public void unregisterContexts() {
        if (this.luckPerms != null) {
            this.luckPerms.getContextManager().unregisterCalculator(this);
        }
    }

    private void registerContext(String context, Function<ISpigotCharacter, Iterable<String>> calculator, Supplier<Iterable<String>> suggestions) {
        calculators.add(new Calculator(context, calculator, suggestions));
    }

    record Calculator(String context, Function<ISpigotCharacter, Iterable<String>> function, Supplier<Iterable<String>> suggestions){}

}
