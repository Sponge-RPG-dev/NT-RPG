package cz.neumimto.rpg.sponge.entities.commandblocks;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import cz.neumimto.rpg.common.entity.players.PreloadCharacter;
import cz.neumimto.rpg.api.entity.IReservable;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementProgress;
import org.spongepowered.api.advancement.AdvancementTree;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.*;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.SkinPart;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.record.RecordType;
import org.spongepowered.api.entity.*;
import org.spongepowered.api.entity.living.player.CooldownTracker;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatVisibility;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.AABB;
import org.spongepowered.api.util.RelativePositions;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.extent.Extent;
import org.spongepowered.common.entity.projectile.ProjectileLauncher;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by NeumimTo on 1.8.2017.
 */
public class CommandblockSkillExecutor extends PreloadCharacter {


    private static IReservable INFINITY_POOL = new InfinityPool();
    private Player mock;

    private CommandblockSkillExecutor(Location<Extent> location, Vector3d headRotation) {
        super(null);
        mock = new CommandblockPlayer(location, headRotation);
    }

    public static CommandblockSkillExecutor wrap(Location<Extent> location, Vector3d headRotation) {
        return new CommandblockSkillExecutor(location, headRotation);
    }

    @Override
    public IReservable getMana() {
        return INFINITY_POOL;
    }

    @Override
    public IReservable getHealth() {
        return INFINITY_POOL;
    }

    @Override
    public Object getEntity() {
        return mock;
    }

    @Override
    public boolean hasEffect(String cl) {
        return true;
    }

    @Override
    public boolean hasSkill(String name) {
        return true;
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void sendMessage(int channel, String message) {

    }

    @Override
    public void sendNotification(String message) {

    }

    @Override
    public boolean hasCooldown(String thing) {
        return false;
    }

    @Override
    public UUID getUUID() {
        return UUID.randomUUID();
    }

    @Override
    public boolean isStub() {
        return false;
    }

    private static class InfinityPool implements IReservable {

        @Override
        public double getMaxValue() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public void setMaxValue(double f) {

        }

        @Override
        public double getRegen() {
            return 0;
        }

        @Override
        public void setRegen(float f) {

        }

        @Override
        public void setReservedAmnout(float f) {

        }

        @Override
        public double getReservedAmount() {
            return 0;
        }

        @Override
        public double getValue() {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public void setValue(double f) {

        }
    }

    private static class CommandblockPlayer implements Player {

        private final Vector3d headRotation;
        private final Location<World> location;

        private CommandblockPlayer(Location location, Vector3d headRotation) {
            this.headRotation = headRotation;
            this.location = location;
        }

        @Override
        public Vector3d getHeadRotation() {
            return headRotation;
        }

        @Override
        public EntityType getType() {
            return EntityTypes.COMMANDBLOCK_MINECART;
        }

        @Override
        public String getName() {
            return "Commandblock Executor";
        }

        @Override
        public <T extends Projectile> Optional<T> launchProjectile(Class<T> projectileClass) {
            return ProjectileLauncher.launch(projectileClass, this, null);
        }

        @Override
        public <T extends Projectile> Optional<T> launchProjectile(Class<T> projectileClass, Vector3d velocity) {
            Optional<T> launch = ProjectileLauncher.launch(projectileClass, this, velocity);
            return launch;
        }

        @Override
        public Location<World> getLocation() {
            return location;
        }


        //*************************************************************************************************************
        //												NOT IMPLEMENTED
        //*************************************************************************************************************


        @Override
        public Optional<Container> getOpenInventory() {
            return Optional.empty();
        }

        @Override
        public Optional<Container> openInventory(Inventory inventory) throws IllegalArgumentException {
            return Optional.empty();
        }

        @Override
        public boolean closeInventory() throws IllegalArgumentException {
            return false;
        }

        @Override
        public int getViewDistance() {
            return 0;
        }

        @Override
        public ChatVisibility getChatVisibility() {
            return null;
        }

        @Override
        public boolean isChatColorsEnabled() {
            return false;
        }

        @Override
        public MessageChannelEvent.Chat simulateChat(Text message, Cause cause) {
            return null;
        }

        @Override
        public Set<SkinPart> getDisplayedSkinParts() {
            return Collections.emptySet();
        }

        @Override
        public PlayerConnection getConnection() {
            return null;
        }

        @Override
        public void sendResourcePack(ResourcePack pack) {

        }

        @Override
        public TabList getTabList() {
            return null;
        }

        @Override
        public void kick() {

        }

        @Override
        public void kick(Text reason) {

        }

        @Override
        public Scoreboard getScoreboard() {
            return null;
        }

        @Override
        public void setScoreboard(Scoreboard scoreboard) {

        }

        @Override
        public boolean isSleepingIgnored() {
            return false;
        }

        @Override
        public void setSleepingIgnored(boolean sleepingIgnored) {

        }

        @Override
        public Inventory getEnderChestInventory() {
            return null;
        }

        @Override
        public boolean respawnPlayer() {
            return false;
        }

        @Override
        public Optional<Entity> getSpectatorTarget() {
            return Optional.empty();
        }

        @Override
        public void setSpectatorTarget(@Nullable Entity entity) {

        }

        @Override
        public Optional<WorldBorder> getWorldBorder() {
            return Optional.empty();
        }

        @Override
        public void setWorldBorder(@Nullable WorldBorder border, Cause cause) {

        }

        @Override
        public CooldownTracker getCooldownTracker() {
            return null;
        }

        @Override
        public AdvancementProgress getProgress(Advancement advancement) {
            return null;
        }

        @Override
        public Collection<AdvancementTree> getUnlockedAdvancementTrees() {
            return null;
        }

        @Override
        public void spawnParticles(ParticleEffect particleEffect, Vector3d position) {

        }

        @Override
        public void spawnParticles(ParticleEffect particleEffect, Vector3d position, int radius) {

        }

        @Override
        public void playSound(SoundType sound, SoundCategory category, Vector3d position, double volume) {

        }

        @Override
        public void playSound(SoundType sound, SoundCategory category, Vector3d position, double volume, double pitch) {

        }

        @Override
        public void playSound(SoundType sound, SoundCategory category, Vector3d position, double volume, double pitch, double minVolume) {

        }

        @Override
        public void stopSounds() {

        }

        @Override
        public void stopSounds(SoundType sound) {

        }

        @Override
        public void stopSounds(SoundCategory category) {

        }

        @Override
        public void stopSounds(SoundType sound, SoundCategory category) {

        }

        @Override
        public void playRecord(Vector3i position, RecordType recordType) {

        }

        @Override
        public void stopRecord(Vector3i position) {

        }

        @Override
        public void sendTitle(Title title) {

        }

        @Override
        public void sendBookView(BookView bookView) {

        }

        @Override
        public void sendBlockChange(int x, int y, int z, BlockState state) {

        }

        @Override
        public void resetBlockChange(int x, int y, int z) {

        }

        @Override
        public Optional<ItemStack> getItemInHand(HandType handType) {
            return Optional.empty();
        }

        @Override
        public void setItemInHand(HandType hand, @Nullable ItemStack itemInHand) {

        }


        @Override
        public void setHeadRotation(Vector3d rotation) {

        }


        @Override
        public EntitySnapshot createSnapshot() {
            return null;
        }

        @Override
        public Random getRandom() {
            return new Random();
        }

        @Override
        public boolean setLocation(Location<World> location) {
            return false;
        }

        @Override
        public Vector3d getRotation() {
            return null;
        }

        @Override
        public void setRotation(Vector3d rotation) {

        }

        @Override
        public boolean setLocationAndRotation(Location<World> location, Vector3d rotation) {
            return false;
        }

        @Override
        public boolean setLocationAndRotation(Location<World> location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
            return false;
        }

        @Override
        public Vector3d getScale() {
            return null;
        }

        @Override
        public void setScale(Vector3d scale) {

        }

        @Override
        public Transform<World> getTransform() {
            return null;
        }

        @Override
        public boolean setTransform(Transform<World> transform) {
            return false;
        }

        @Override
        public boolean transferToWorld(World world, Vector3d position) {
            return false;
        }

        @Override
        public Optional<AABB> getBoundingBox() {
            return Optional.empty();
        }

        @Override
        public List<Entity> getPassengers() {
            return null;
        }

        @Override
        public boolean hasPassenger(Entity entity) {
            return false;
        }

        @Override
        public boolean addPassenger(Entity entity) {
            return false;
        }

        @Override
        public void removePassenger(Entity entity) {

        }

        @Override
        public void clearPassengers() {

        }

        @Override
        public Optional<Entity> getVehicle() {
            return Optional.empty();
        }

        @Override
        public boolean setVehicle(@Nullable Entity entity) {
            return false;
        }

        @Override
        public Entity getBaseVehicle() {
            return null;
        }

        @Override
        public boolean isOnGround() {
            return true;
        }

        @Override
        public boolean isRemoved() {
            return false;
        }

        @Override
        public boolean isLoaded() {
            return true;
        }

        @Override
        public void remove() {

        }

        @Override
        public boolean damage(double damage, DamageSource damageSource) {
            return false;
        }

        @Override
        public Optional<UUID> getCreator() {
            return Optional.empty();
        }

        @Override
        public Optional<UUID> getNotifier() {
            return Optional.empty();
        }

        @Override
        public void setCreator(@Nullable UUID uuid) {

        }

        @Override
        public void setNotifier(@Nullable UUID uuid) {

        }

        @Override
        public EntityArchetype createArchetype() {
            return null;
        }

        @Override
        public GameProfile getProfile() {
            return null;
        }

        @Override
        public boolean isOnline() {
            return false;
        }

        @Override
        public Optional<Player> getPlayer() {
            return Optional.empty();
        }

        @Override
        public boolean validateRawData(DataView container) {
            return false;
        }

        @Override
        public void setRawData(DataView container) throws InvalidDataException {

        }

        @Override
        public int getContentVersion() {
            return 0;
        }

        @Override
        public DataContainer toContainer() {
            return null;
        }

        @Override
        public <T extends Property<?, ?>> Optional<T> getProperty(Class<T> propertyClass) {
            return Optional.empty();
        }

        @Override
        public Collection<Property<?, ?>> getApplicableProperties() {
            return Collections.emptySet();
        }

        @Override
        public <T extends DataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
            return Optional.empty();
        }

        @Override
        public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
            return Optional.empty();
        }

        @Override
        public boolean supports(Class<? extends DataManipulator<?, ?>> holderClass) {
            return false;
        }

        @Override
        public <E> DataTransactionResult offer(Key<? extends BaseValue<E>> key, E value) {
            return null;
        }

        @Override
        public DataTransactionResult offer(DataManipulator<?, ?> valueContainer, MergeFunction function) {
            return null;
        }

        @Override
        public DataTransactionResult remove(Class<? extends DataManipulator<?, ?>> containerClass) {
            return null;
        }

        @Override
        public DataTransactionResult remove(Key<?> key) {
            return null;
        }

        @Override
        public DataTransactionResult undo(DataTransactionResult result) {
            return null;
        }

        @Override
        public DataTransactionResult copyFrom(DataHolder that, MergeFunction function) {
            return null;
        }

        @Override
        public Collection<DataManipulator<?, ?>> getContainers() {
            return null;
        }

        @Override
        public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
            return Optional.empty();
        }

        @Override
        public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
            return Optional.empty();
        }

        @Override
        public boolean supports(Key<?> key) {
            return false;
        }

        @Override
        public DataHolder copy() {
            return null;
        }

        @Override
        public Set<Key<?>> getKeys() {
            return null;
        }

        @Override
        public Set<ImmutableValue<?>> getValues() {
            return null;
        }

        @Override
        public boolean canEquip(EquipmentType type) {
            return false;
        }

        @Override
        public boolean canEquip(EquipmentType type, @Nullable ItemStack equipment) {
            return false;
        }

        @Override
        public Optional<ItemStack> getEquipped(EquipmentType type) {
            return Optional.empty();
        }

        @Override
        public boolean equip(EquipmentType type, @Nullable ItemStack equipment) {
            return false;
        }


        @Override
        public CarriedInventory<? extends Carrier> getInventory() {
            return null;
        }

        @Override
        public Text getTeamRepresentation() {
            return null;
        }

        @Override
        public Optional<CommandSource> getCommandSource() {
            return Optional.empty();
        }

        @Override
        public SubjectCollection getContainingCollection() {
            return null;
        }

        @Override
        public SubjectReference asSubjectReference() {
            return null;
        }

        @Override
        public boolean isSubjectDataPersisted() {
            return false;
        }

        @Override
        public SubjectData getSubjectData() {
            return null;
        }

        @Override
        public SubjectData getTransientSubjectData() {
            return null;
        }

        @Override
        public Tristate getPermissionValue(Set<Context> contexts, String permission) {
            return null;
        }

        @Override
        public boolean isChildOf(Set<Context> contexts, SubjectReference parent) {
            return false;
        }

        @Override
        public List<SubjectReference> getParents(Set<Context> contexts) {
            return null;
        }

        @Override
        public Optional<String> getOption(Set<Context> contexts, String key) {
            return Optional.empty();
        }

        @Override
        public String getIdentifier() {
            return null;
        }

        @Override
        public Set<Context> getActiveContexts() {
            return null;
        }

        @Override
        public void sendMessage(ChatType type, Text message) {

        }

        @Override
        public void sendMessage(Text message) {

        }

        @Override
        public MessageChannel getMessageChannel() {
            return null;
        }

        @Override
        public void setMessageChannel(MessageChannel channel) {

        }

        @Override
        public Translation getTranslation() {
            return null;
        }

        @Override
        public UUID getUniqueId() {
            return null;
        }
    }

}
