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

package cz.neumimto.rpg.inventory;

import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.configuration.Localization;
import cz.neumimto.rpg.damage.DamageService;
import cz.neumimto.rpg.effects.EffectService;
import cz.neumimto.rpg.effects.IGlobalEffect;
import cz.neumimto.rpg.gui.Gui;
import cz.neumimto.rpg.inventory.runewords.RWService;
import cz.neumimto.rpg.inventory.runewords.Rune;
import cz.neumimto.rpg.inventory.runewords.RuneWord;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.skills.ISkill;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.utils.ItemStackUtils;
import org.spongepowered.api.Game;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.TargetContainerEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
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


    public static ItemType ITEM_SKILL_BIND = ItemTypes.BLAZE_POWDER;
    public static TextColor LORE_FIRSTLINE = TextColors.AQUA;
    public static TextColor SOCKET_COLOR = TextColors.GRAY;
    public static TextColor ENCHANTMENT_COLOR = TextColors.BLUE;
    public static TextColor LEVEL_COLOR = TextColors.YELLOW;
    public static TextColor RESTRICTIONS = TextColors.LIGHT_PURPLE;



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
        if (character.isStub())
            return;
        int size = character.getPlayer().getInventory().query(Hotbar.class).size();
        for (int i = 0; i<size; i++) {
            initializeHotbar(character,i);
        }
    }

    public void initializeHotbar(IActiveCharacter character, int slot) {
        initializeHotbar(character, slot, null);
    }

    public void initializeHotbar(IActiveCharacter character, int slot, ItemStack toPickup) {
        Player player = character.getPlayer();
        Hotbar query = player.getInventory().query(Hotbar.class);
        int selectedSlotIndex = query.getSelectedSlotIndex();
        Optional<Slot> slot1 = query.getSlot(new SlotIndex(slot));
        if (slot1.isPresent()) {
            Slot s = slot1.get();
            Optional<ItemStack> peek = s.peek();
            if (!peek.isPresent()) {
                //picking up an item
                if (character.getHotbar()[slot] != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                    HotbarObject hotbarObject = character.getHotbar()[slot];
                    hotbarObject.onUnEquip(character);
                    character.getHotbar()[slot] = HotbarObject.EMPTYHAND_OR_CONSUMABLE;
                }
                if (toPickup != null) {
                    HotbarObject o = getHotbarObject(character, toPickup);
                    if (o != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                        o.onEquip(toPickup, character);
                        character.getHotbar()[slot] = o;
                    }
                }
            } else {
                if (character.getHotbar()[slot]!=HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                    character.getHotbar()[slot].onUnEquip(character);
                }
                ItemStack i = peek.get();
                HotbarObject hotbarObject = getHotbarObject(character, i);

                if (hotbarObject != HotbarObject.EMPTYHAND_OR_CONSUMABLE) {
                    hotbarObject.setSlot(slot);
                    character.getHotbar()[slot] = hotbarObject;
                    if (hotbarObject.getType() == HotbarObjectTypes.CHARM) {
                        hotbarObject.onEquip(i,character);
                    } else if (hotbarObject.getType() == HotbarObjectTypes.WEAPON && slot == selectedSlotIndex) {
                        hotbarObject.onEquip(i,character);
                    }

                } else {
                    character.getHotbar()[slot] = HotbarObject.EMPTYHAND_OR_CONSUMABLE;
                }
            }
        }
    }

    protected HotbarObject getHotbarObject(IActiveCharacter character, ItemStack is) {
        if (is == null)
            return HotbarObject.EMPTYHAND_OR_CONSUMABLE;
        if (ItemStackUtils.isItemSkillBind(is)) {
            return buildHotbarSkill(character, is);
        }
        if (ItemStackUtils.isCharm(is)) {
            return buildCharm(character, is);
        }
        if (ItemStackUtils.isItemRune(is)) {
            return new HotbarRune();
        }
        if (ItemStackUtils.isWeapon(is.getItem())) {
            return buildHotbarWeapon(character, is);
        }
        return HotbarObject.EMPTYHAND_OR_CONSUMABLE;
    }

    private Charm buildCharm(IActiveCharacter character, ItemStack is) {
        Charm charm = new Charm();
        charm.setEffects(ItemStackUtils.getItemEffects(is));

        return charm;
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

    public Weapon buildHotbarWeapon(IActiveCharacter character, ItemStack is) {
        Weapon w = new Weapon(is);
        Optional<List<Text>> a = is.get(Keys.ITEM_LORE);
        if (!a.isPresent()) {
            return w;
        }
        w.setItemStack(is);
        List<Text> texts = a.get();
        Map<IGlobalEffect, Integer> map = new HashMap<>();
        for (Text text : texts) {
            if (text.getColor() == ENCHANTMENT_COLOR) {
                ItemStackUtils.findItemEffect(text, map);
            } else if (text.getColor() == LEVEL_COLOR) {
                w.setLevel(ItemStackUtils.getItemLevel(text));
            } else if (text.getColor() == RESTRICTIONS) {
                //todo
            }
        }
        w.setEffects(map);
        Map<IGlobalEffect, Integer> itemEffects = ItemStackUtils.getItemEffects(is);
        w.setEffects(itemEffects);
        return w;
    }

    public HotbarSkill buildHotbarSkill(IActiveCharacter character, ItemStack is) {
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

    //todo event
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

    protected void changeEquipedWeapon(IActiveCharacter character, Weapon weapon) {
        changeEquipedWeapon(character, weapon.getItemStack());
    }


    protected void changeEquipedWeapon(IActiveCharacter character, ItemStack weapon) {
        //old
        Weapon mainHand = character.getMainHand();
        mainHand.current = false;
        effectService.removeGlobalEffectsAsEnchantments(mainHand.getEffects(), character);

        //new
        Weapon weapon1 = buildHotbarWeapon(character, weapon);
        effectService.applyGlobalEffectsAsEnchantments(weapon1.getEffects(), character);

        int slot = ((Hotbar) character.getPlayer().getInventory().query(Hotbar.class)).getSelectedSlotIndex();
        character.setHotbarSlot(slot, weapon1);
        weapon1.current = true;
        character.setMainHand(weapon1);
        damageService.recalculateCharacterWeaponDamage(character);
    }

    public void startSocketing(IActiveCharacter character) {
        Optional<ItemStack> itemInHand = character.getPlayer().getItemInHand(HandTypes.MAIN_HAND);
        if (itemInHand.isPresent()) {
            Hotbar h = character.getPlayer().getInventory().query(Hotbar.class);
            int selectedSlotIndex = h.getSelectedSlotIndex();
            HotbarObject o = character.getHotbar()[selectedSlotIndex];
            if (o.getType() == HotbarObjectTypes.RUNE) {
                character.setCurrentRune(selectedSlotIndex);
                Gui.sendMessage(character,Localization.SOCKET_HELP);
            }
        }
    }

    public void insertRune(IActiveCharacter character) {
        if (!character.isSocketing())
            return;
        Optional<ItemStack> itemInHand = character.getPlayer().getItemInHand(HandTypes.MAIN_HAND);
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
                    String name = null;
                    Inventory slot = character.getPlayer().getInventory().query(Hotbar.class).query(new SlotIndex(character.getCurrentRune()));
                    ItemStack runeitem = null;
                    if (!slot.peek().isPresent()) {
                        return;
                    }
                    runeitem = slot.peek().get();
                    if (runeitem.get(Keys.DISPLAY_NAME).isPresent()) {
                        name = runeitem.get(Keys.DISPLAY_NAME).get().toPlain();
                    }
                    r.r = rwService.getRune(name);
                    if (r.r == null) {
                        Gui.sendMessage(character, Localization.UNKNOWN_RUNE_NAME);
                        character.setCurrentRune(-1);
                        return;
                    }
                    ItemStack i = rwService.insertRune(itemStack, r.getRune().getName());
                    CarriedInventory<? extends Carrier> inventory = character.getPlayer().getInventory();
                    Inventory query = inventory.query(Hotbar.class).query(new SlotIndex(character.getCurrentRune()));
                    query.clear();
                    character.getPlayer().setItemInHand(HandTypes.MAIN_HAND,i);
                    if (!rwService.hasEmptySocket(i.get(Keys.ITEM_LORE).get())) {
                        i = rwService.findRuneword(i);
                        RuneWord rw = rwService.getRuneword(i.get(Keys.ITEM_LORE).get());
                        if (rwService.canUse(rw, character)) {
                            character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, i);
                        } else {
                            character.getPlayer().setItemInHand(HandTypes.MAIN_HAND, null);
                            Entity entity = character.getPlayer().getLocation().getExtent().createEntity(EntityTypes.ITEM, character.getPlayer().getLocation().getPosition());
                            entity.offer(Keys.REPRESENTED_ITEM, i.createSnapshot());
                            character.getPlayer().getWorld().spawnEntity(entity, Cause.of(NamedCause.of("CANNOTHOLDRW", character.getPlayer())));
                        }
                    }
                }
            }
        }
    }

    public void cancelSocketing(IActiveCharacter character) {
        if (character.isSocketing()) {
            Gui.sendMessage(character, Localization.SOCKET_CANCELLED);
        }
        character.setCurrentRune(-1);

    }
}
