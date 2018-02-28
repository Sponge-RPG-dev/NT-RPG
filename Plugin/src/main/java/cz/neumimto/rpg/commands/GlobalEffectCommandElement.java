package cz.neumimto.rpg.commands;

import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.effects.IGlobalEffect;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by NeumimTo on 5.11.2017.
 */
public class GlobalEffectCommandElement extends CommandElement {

    public GlobalEffectCommandElement(@Nullable Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        String skilllc = args.next().replaceAll("_", " ");;
        IGlobalEffect effect= NtRpgPlugin.GlobalScope.effectService.getGlobalEffect(skilllc);
        if (effect == null) {
            throw args.createError(TextSerializers.FORMATTING_CODE.deserialize("&CUnknown effect &C\"" + skilllc + "\""));
        }
        return effect;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return NtRpgPlugin.GlobalScope.effectService.getGlobalEffects()
                .keySet()
                .stream()
                .map(a -> a.replaceAll(" ", "_"))
                .filter(a -> {
                    try {
                        if (a.toLowerCase().startsWith(args.next())) {
                            return true;
                        } else {
                            return false;
                        }
                    } catch (ArgumentParseException e) {}
                    return true;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of("<effect>");
    }

}

