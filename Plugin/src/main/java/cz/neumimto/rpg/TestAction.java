package cz.neumimto.rpg;

import com.flowpowered.math.vector.Vector3d;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.effects.*;
import cz.neumimto.rpg.effects.common.stacking.IntegerEffectStackingStrategy;
import cz.neumimto.rpg.gui.ParticleDecorator;
import cz.neumimto.rpg.inventory.InventoryService;
import cz.neumimto.rpg.inventory.data.CustomItemData;
import cz.neumimto.rpg.players.IActiveCharacter;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;

/**
 * Created by NeumimTo on 29.10.2016.
 * When unit tests are not enough.
 *
 * @see cz.neumimto.rpg.commands.CommandAdmin - test.action
 */
@Singleton
public class TestAction implements IEffectSourceProvider {

	@Inject
	private EffectService effectService;

	@Inject
	private InventoryService inventoryService;

	public void data(IActiveCharacter character) {
		ItemStack itemStack = character.getPlayer().getItemInHand(HandTypes.MAIN_HAND).get();
		IGlobalEffect globalEffect = effectService.getGlobalEffect("Elemental Resistance");
		IGlobalEffect globalEffect1 = effectService.getGlobalEffect("All skills");

		CustomItemData itemData = inventoryService.getItemData(itemStack);
		itemData.getEnchantements().put(globalEffect.getName(), "+10%");
		itemData.getEnchantements().put(globalEffect1.getName(), "+5");
		itemStack.offer(itemData);
		inventoryService.setItemLevel(itemStack, 5);
		itemData.setSocketCount(4);
		inventoryService.updateLore(itemStack);
		character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, itemStack);
	}

	public void charm(IActiveCharacter character) {
		ItemStack itemStack = character.getPlayer().getItemInHand(HandTypes.MAIN_HAND).get();
		IGlobalEffect globalEffect = effectService.getGlobalEffect("Damage to mana");


		CustomItemData itemData = inventoryService.getItemData(itemStack);
		itemData.getEnchantements().put(globalEffect.getName(), "+3%");
		itemStack.offer(itemData);
		inventoryService.setItemLevel(itemStack, 10);
		inventoryService.setItemRarity(itemStack, Text.builder(Localization.CHARM).color(TextColors.GOLD).style(TextStyles.BOLD).build());
		inventoryService.updateLore(itemStack);
		character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, itemStack);
	}

	public void getData(IActiveCharacter character) {
		ItemStack itemStack = character.getPlayer().getItemInHand(HandTypes.MAIN_HAND).get();
		CustomItemData itemData = inventoryService.getItemData(itemStack);
	}

	public void testAddEffect(IActiveCharacter character) {
		effectService.addEffect(new Test(character), character, this);
		effectService.addEffect(new Test(character), character, this);
		effectService.addEffect(new Test(character), character, this);
		IEffectContainer<Integer, Test> k = character.getEffect("test");
		if (k.getStackedValue() != 30) {
			throw new AssertionError();
		}
	}

	public void spiral(IActiveCharacter character) {
		ParticleEffect build = ParticleEffect.builder().type(ParticleTypes.SMOKE)
				.quantity(1).build();

		double rot = 0.125;
		Vector3d position = character.getLocation().getPosition();
		World w = character.getLocation().getExtent();
		while (rot < 1) {
			new ParticleDecorator()
					.spiral(
							4,
							32,
							7,
							rot,
							vector3d -> {
								Vector3d vector3d1 = VectorUtils.rotateAroundAxisY(character.getLocation().add(vector3d).getPosition(), 30);
								w.spawnParticles(build, vector3d1);
							});
			rot += 0.125;
		}

	}

	@Override
	public IEffectSource getType() {
		return EffectSourceType.COMMAND;
	}

	private static class Test extends EffectBase<Integer> {

		public Test(IEffectConsumer consumer) {
			super("test", consumer);
			setStackable(true, new IntegerEffectStackingStrategy());
			setDuration(10000L);
		}

		@Override
		public void onApply() {
			getConsumer().sendMessage("added");
		}

		@Override
		public void onRemove() {
			getConsumer().sendMessage("removed");
		}

		@Override
		public Integer getValue() {
			return 10;
		}
	}
}
