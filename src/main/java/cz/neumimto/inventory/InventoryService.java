/*    
 *     Copyright (c) 2015, NeumimTo https://github.com/NeumimTo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 */

package cz.neumimto.inventory;

import cz.neumimto.configuration.Localization;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.damage.DamageService;
import cz.neumimto.effects.EffectService;
import cz.neumimto.effects.IGlobalEffect;
import cz.neumimto.gui.Gui;
import cz.neumimto.inventory.runewords.RWService;
import cz.neumimto.inventory.runewords.Rune;
import cz.neumimto.players.CharacterService;
import cz.neumimto.players.IActiveCharacter;
import cz.neumimto.skills.ISkill;
import cz.neumimto.skills.SkillService;
import cz.neumimto.utils.ItemStackUtils;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.*;

/**
 * Created by NeumimTo on 22.7.2015.
 */
@Singleton
public class InventoryService {


    @Inject
    private SkillService skillService;

    @Inject
    private Game game;

    @Inject
    private CharacterService characterService;

    @Inject
    private EffectService effectService;

    @Inject
    private DamageService damageService;

    @Inject
    private RWService rwService;

    private Map<UUID, InventoryMenu> inventoryMenus = new HashMap<>();

    public static ItemType ITEM_SKILL_BIND = ItemTypes.BLAZE_POWDER;
    public static TextColor LORE_FIRSTLINE = TextColors.BLUE;
    public static TextColor SOCKET_COLOR = TextColors.GRAY;

    public ItemStack getHelpItem(List<String> lore, ItemType type) {
        ItemStack.Builder builder = ItemStack.builder();
        builder.quantity(1).itemType(type);
        return builder.build();
    }

    public Map<UUID, InventoryMenu> getInventoryMenus() {
        return inventoryMenus;
    }

    public void addInventoryMenu(UUID uuid, InventoryMenu menu) {
        if (!inventoryMenus.containsKey(uuid))
            inventoryMenus.put(uuid, menu);
    }


    public void initializeHotbar(IActiveCharacter character) {
        int i = 0;
        for (Inventory inventory : character.getPlayer().getInventory().query(Hotbar.class)) {
            Slot s = (Slot) inventory;
            Optional<ItemStack> stack = s.peek();
            if (stack.isPresent()) {
                HotbarObject hotbarObject = getHotbarObject(character, stack.get());
                if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE)
                    hotbarObject.setSlot(i);
                character.setHotbarSlot(i, hotbarObject);
            }
            i++;
        }
    }

    public void initializeHotbar(IActiveCharacter character, int slot) {
        Player player = character.getPlayer();
    }


    protected HotbarObject getHotbarObject(IActiveCharacter character, ItemStack is) {
        if (is == null)
            return HotbarObject.EMPTYHAND_OR_CONSUMABLE;
        if (ItemStackUtils.isItemSkillBind(is)) {
            return buildHotbarSkill(character, is);
        }
        if (ItemStackUtils.isWeapon(is.getItem())) {
            return buildHotbarWeapon(character, is);
        }
        if (ItemStackUtils.isItemRune(is)) {
            return new HotbarRune();
        }
        return HotbarObject.EMPTYHAND_OR_CONSUMABLE;
    }

    private HotbarRune buildHotbarRune(ItemStack is) {
        HotbarRune rune = new HotbarRune();
        Optional<Text> text = is.get(Keys.DISPLAY_NAME);
        if (text.isPresent()) {
            String s = text.get().toPlain();
            Rune rune1 = rwService.getRune(s);
            rune.r = rune1;
        }
        return rune;
    }

    private Weapon buildHotbarWeapon(IActiveCharacter character, ItemStack is) {
        Map<IGlobalEffect, Integer> itemEffects = ItemStackUtils.getItemEffects(is);
        Weapon w = new Weapon(is.getItem());
        w.setItemStack(is);
        w.setEffects(itemEffects);

        return w;
    }

    protected HotbarSkill buildHotbarSkill(IActiveCharacter character, ItemStack is) {
        HotbarSkill skill = new HotbarSkill();
        Optional<Text> text = is.get(Keys.DISPLAY_NAME);
        if (text.isPresent()) {
            String s = text.get().toPlain();
            String[] split = s.split("-");
            for (String s1 : split) {
                if (s1.isEmpty())
                    continue;
                if (s1.endsWith("«")) {
                    String substring = s1.substring(0, s1.length() - 2);
                    ISkill skill1 = skillService.getSkill(substring);
                    skill.left_skill = skill1;
                } else if (s1.startsWith("»")) {
                    String substring = s1.substring(2);
                    ISkill skill1 = skillService.getSkill(substring);
                    skill.right_skill = skill1;
                }
            }
        }
    return skill;
    }

    public void createHotbarSkill(ItemStack is, ISkill right, ISkill left) {
        Optional<List<Text>> texts = is.get(Keys.ITEM_LORE);
        List<Text> lore;
        if (texts.isPresent()) {
            lore = texts.get();
            lore.clear();
        } else {
            lore = new ArrayList<>();
        }
        lore.add(Text.of(LORE_FIRSTLINE, Localization.SKILLBIND));
        is.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.ITALIC, left != null ? left.getName() + " «-" : "", right != null ? "-» " + right.getName() : ""));
        if (right != null) {
            lore.add(Text.of(TextColors.RED, Localization.CAST_SKILL_ON_RIGHTLICK.replaceAll("%1", right.getName())));
            lore = makeDesc(right, lore);
        }
        if (left != null) {
            lore.add(Text.of(TextColors.RED, Localization.CAST_SKILl_ON_LEFTCLICK.replaceAll("%1", left.getName())));
            lore = makeDesc(left, lore);
        }
        for (String a : Localization.ITEM_SKILLBIND_FOOTER.split(":n")) {
            lore.add(Text.of(TextColors.DARK_GRAY, a));
        }
        ItemStackUtils.createEnchantmentGlow(is);
        is.offer(Keys.ITEM_LORE, lore);
    }

    private List<Text> makeDesc(ISkill skill, List<Text> lore) {
        if (skill.getDescription() != null) {
            for (String s : skill.getDescription().split(":n")) {
                lore.add(Text.of(TextColors.GRAY, "- " + s));
            }
        }
        if (skill.getLore() != null) {
            for (String s : skill.getLore().split(":n")) {
                lore.add(Text.of(TextColors.GREEN, TextStyles.ITALIC, s));
            }
        }
        return lore;
    }

    public void onRightClick(IActiveCharacter character, int slot) {
        HotbarObject hotbarObject = character.getHotbar()[slot];
        if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
            hotbarObject.onRightClick(character);
        }
    }

    public void onLeftClick(IActiveCharacter character, int slot) {
        HotbarObject hotbarObject = character.getHotbar()[slot];
        if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
            hotbarObject.onLeftClick(character);
        }
    }

    public void changeEquipedWeapon(IActiveCharacter character, Weapon weapon) {
        changeEquipedWeapon(character, weapon.getItemStack());
    }

    //todo
    public void changeEquipedWeapon(IActiveCharacter character, ItemStack weapon) {
        //old
        Weapon mainHand = character.getMainHand();
        mainHand.current = false;
        effectService.removeGlobalEffectsAsEnchantments(mainHand.getEffects(), character);

        //new
        Weapon weapon1 = buildHotbarWeapon(character, weapon);
        effectService.applyGlobalEffectsAsEnchantments(weapon1.getEffects(), character);
        weapon1.setItemStack(weapon);
        int slot = mainHand.getSlot();
        character.setHotbarSlot(slot, weapon1);
        weapon1.current = true;
        damageService.recalculateCharacterWeaponDamage(character);
    }

    public void startSocketing(IActiveCharacter character) {
        Optional<ItemStack> itemInHand = character.getPlayer().getItemInHand();
        if (itemInHand.isPresent()) {
            Hotbar h = character.getPlayer().getInventory().query(Hotbar.class);
            int selectedSlotIndex = h.getSelectedSlotIndex();
            HotbarObject o = character.getHotbar()[selectedSlotIndex];
            if (o.getType() == HotbarObjectTypes.RUNE) {
                character.setCurrentRune(selectedSlotIndex);
            }
        }
    }

    public void insertRune(IActiveCharacter character) {
        if (!character.isSocketing())
            return;
        Optional<ItemStack> itemInHand = character.getPlayer().getItemInHand();
        if (itemInHand.isPresent()) {
            ItemStack itemStack = itemInHand.get();
            if (ItemStackUtils.hasSockets(itemStack)) {
                HotbarObject hotbarObject = character.getHotbar()[character.getCurrentRune()];
                if (hotbarObject == HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                    character.setCurrentRune(-1);
                    return;
                }
                if (hotbarObject.type == HotbarObjectTypes.RUNE) {
                    HotbarRune r = (HotbarRune) hotbarObject;
                    if (r.r == null) {
                        Gui.sendMessage(character, Localization.UNKNOWN_RUNE_NAME);
                        return;
                    }
                    //TODO fire events
                    ItemStack i = rwService.insertRune(itemStack, r.getRune().getName());
                    character.getPlayer().setItemInHand(i);
                }
            }
        }
    }
}
