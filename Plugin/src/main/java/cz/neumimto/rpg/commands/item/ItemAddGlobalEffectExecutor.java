package cz.neumimto.rpg.commands.item;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import cz.neumimto.core.localization.TextHelper;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.configuration.Localizations;
import cz.neumimto.rpg.effects.EffectParams;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.effects.model.EffectModelFactory;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemAddGlobalEffectExecutor implements CommandExecutor {
	private static Gson gson = new Gson();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		IGlobalEffect effect = args.<IGlobalEffect>getOne("effect").get();
		Player player = (Player) src;
		if (player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
			ItemStack itemStack = player.getItemInHand(HandTypes.MAIN_HAND).get();

			Optional<String> params = args.getOne("params");
			String s = params.get();
			try {
				if (s.equals("?")) {
					Class<?> modelType = EffectModelFactory.getModelType(effect.asEffectClass());
					if (Number.class.isAssignableFrom(modelType) || modelType.isPrimitive()) {
						player.sendMessage(Text.of("Expected: " + modelType.getTypeName()));
					} else {
						Map<String, String> q = new HashMap<>();
						for (Field field : modelType.getDeclaredFields()) {
							q.put(field.getName(), field.getType().getName());
						}
						player.sendMessage(Text.of("Expected: " + gson.toJson(q)));
					}
				} else {
					EffectParams map = null;
					Class<?> modelType = EffectModelFactory.getModelType(effect.asEffectClass());
					if (Number.class.isAssignableFrom(modelType) || modelType.isPrimitive()) {
						map = new EffectParams();
						map.put(effect.asEffectClass().getName(), s);
					} else {
						map = gson.fromJson(s, EffectParams.class);
					}
					itemStack = NtRpgPlugin.GlobalScope.inventorySerivce.addEffectsToItemStack(itemStack, effect.getName(), map);
					itemStack = NtRpgPlugin.GlobalScope.inventorySerivce.updateLore(itemStack);
					player.setItemInHand(HandTypes.MAIN_HAND, itemStack);
					player.sendMessage(TextHelper.parse("Enchantment " + effect.getName() + " added"));
				}
			} catch (JsonSyntaxException e) {
				Class<?> modelType = EffectModelFactory.getModelType(effect.asEffectClass());
				Map<String, String> q = new HashMap<>();
				for (Field field : modelType.getDeclaredFields()) {
					q.put(field.getName(), field.getType().getName());
				}
				throw new RuntimeException("Expected: " + gson.toJson(q));
			}
		} else {
			player.sendMessage(Localizations.NO_ITEM_IN_HAND.toText());
		}
		return CommandResult.success();
	}
}
