package cz.neumimto.effects.positive;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import cz.neumimto.Decorator;
import cz.neumimto.SkillLocalization;
import cz.neumimto.Utils;
import cz.neumimto.rpg.api.Rpg;
import cz.neumimto.rpg.sponge.entities.ISpongeEntity;
import cz.neumimto.rpg.sponge.entities.players.ISpongeCharacter;
import cz.neumimto.rpg.sponge.utils.math.VectorUtils;
import cz.neumimto.rpg.api.effects.Generate;
import cz.neumimto.rpg.api.effects.IEffect;
import cz.neumimto.rpg.api.entity.IEffectConsumer;
import cz.neumimto.rpg.sponge.effects.ShapedEffectDecorator;
import cz.neumimto.rpg.api.gui.Gui;
import cz.neumimto.rpg.api.entity.players.IActiveCharacter;
import cz.neumimto.rpg.api.skills.scripting.JsBinding;
import cz.neumimto.rpg.api.utils.rng.XORShiftRnd;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by ja on 1.8.2017.
 */
@JsBinding(JsBinding.Type.CLASS)
@Generate(id = "name", description = "Creates a portal. The portal has to be targetted via teleportation scroll")
public class PortalEffect extends ShapedEffectDecorator {

	public static final String name = "Portal";
	private static ParticleEffect uninicialized = ParticleEffect.builder()
			.type(ParticleTypes.ENCHANTING_GLYPHS)
			.quantity(3)
			.build();
	private static ParticleEffect inLava = ParticleEffect.builder()
			.type(ParticleTypes.DRIP_WATER)
			.quantity(3)
			.build();
	private static ParticleEffect inWater = ParticleEffect.builder()
			.type(ParticleTypes.DRIP_LAVA)
			.quantity(6)
			.build();
	private static ParticleEffect initialized = ParticleEffect.builder()
			.type(ParticleTypes.REDSTONE_DUST)
			.option(ParticleOptions.COLOR, Color.YELLOW)
			.quantity(5)
			.build();
	private Location<World> targetLocation;
	private Location<World> castLocation;
	private double manaPerLookup;
	private double manaPerEntity;
	private long entityLookupInterval;
	private long lastTimeRun;
	private double chanceToFail;
	private boolean safe;
	private IActiveCharacter character;
	private Vector3d[] vertices;
	private PortalState portalState;

	@Generate.Constructor
	public PortalEffect(IEffectConsumer consumer, long duration, String location) {
		this(consumer, duration, Utils.locationFromString(location));
	}

	public PortalEffect(IEffectConsumer consumer, long duration, Location<World> targetLocation) {
		this(consumer, duration, null, 0, 0, 2500, 0, true);
	}

	public PortalEffect(IEffectConsumer consumer, long duration, Location<World> targetLocation,
			double manaPerLookup, double manaPerEntity, long entityLookupInterval,
			double chanceToFail, boolean safe) {
		super(name, consumer);
		this.castLocation = ((ISpongeEntity)getConsumer()).getLocation().add(0, 1, 0);
		this.targetLocation = targetLocation;
		this.manaPerLookup = manaPerLookup;
		this.manaPerEntity = manaPerEntity;
		this.entityLookupInterval = entityLookupInterval;
		setDuration(duration);
		setPrinterCount(10);
		this.safe = safe;
		lastTimeRun = System.currentTimeMillis() + 2500;
		this.chanceToFail = chanceToFail;
		if (getConsumer() instanceof IActiveCharacter) {
			character = (IActiveCharacter) getConsumer();
		}
		portalState = PortalState.UNINITIALIZED;
	}

	@Override
	public void onTick(IEffect self) {
		super.onTick(self);
		if (lastTimeRun <= System.currentTimeMillis() - entityLookupInterval) {
			if (!initialized()) {
				Entity entity = getLocationImprint();
				if (entity != null) {
					entity.remove();
				}
			} else {
				Set<Entity> teleportCandidates = getTeleportCandidates(castLocation);
				if (safe) {
					Optional<Location<World>> safeLocation = Sponge.getTeleportHelper().getSafeLocation(targetLocation, 10, 10);
					for (Entity candidate : teleportCandidates) {
						if (safeLocation.isPresent()) {
							candidate.setLocation(processChanceToFail(safeLocation.get()));
							drainMana(manaPerEntity);
						} else {
							if (getConsumer() instanceof ISpongeCharacter) {
								String translate = Rpg.get().getLocalizationService().translate(SkillLocalization.TELEPORT_LOCATION_OBSTRUCTED);
								((ISpongeCharacter) getConsumer()).sendMessage(translate);
							}
							setDuration(0);
						}
					}
				} else {
					for (Entity candidate : teleportCandidates) {
						candidate.setLocation(processChanceToFail(targetLocation));
						drainMana(manaPerEntity);
						if (getDuration() == 0) {
							break;
						}
					}
				}
			}
			drainMana(manaPerLookup);
			Gui.displayMana(character);

			lastTimeRun = System.currentTimeMillis();
		}
	}

	private Entity getLocationImprint() {
		Chunk c = getChunk(castLocation);
		for (Entity entity : c.getEntities()) {
			if (entity.getLocation().getPosition().distanceSquared(castLocation.getPosition()) <= 4) {
				if (entity.getType() == EntityTypes.ITEM) {
					Item i = (Item) entity;
					Location location = Utils.extractLocationFromItem(i);
					if (location == null) {
						continue;
					}
					setTargetLocation(location);
					setPrinterCount(4);
					return entity;
				}
			}
		}
		return null;
	}

	private void drainMana(double manacost) {
		if (character != null) {
			double value = character.getMana().getValue();
			if (value - manaPerLookup <= 0) {
				character.getMana().setValue(0f);
				setDuration(0);
			} else {
				character.getMana().setValue(character.getMana().getValue() - manacost);
			}
		}
	}

	public Set<Entity> getTeleportCandidates(Location<World> relevantLocation) {

		Chunk c = getChunk(relevantLocation);
		Set<Entity> entities = new HashSet<>();
		int max = 5;
		for (Entity entity : c.getEntities()) {
			if (entity.getLocation().getPosition().distanceSquared(castLocation.getPosition()) <= 4) {
				if (max <= 0) {
					break;
				}
				entities.add(entity);
				max--;
			}
		}
		return entities;

	}

	private Chunk getChunk(Location<World> relevantLocation) {
		Chunk lookupArea;
		Vector3i chunkPosition = relevantLocation.getChunkPosition();
		Optional<Chunk> chunk = relevantLocation.getExtent().getChunk(chunkPosition);
		if (chunk.isPresent()) {
			lookupArea = chunk.get();
		} else {
			lookupArea = relevantLocation.getExtent().loadChunk(chunkPosition, true).get();
		}
		return lookupArea;
	}

	public boolean initialized() {
		return targetLocation != null;
	}


	@Override
	public Vector3d[] getVertices() {
		if (vertices == null) {
			vertices = new Vector3d[30];
			Decorator.ellipse(vertices, 1, 3, 1, ((ISpongeEntity)getConsumer()).getRotation());
		}
		return vertices;
	}

	@Override
	public void draw(Vector3d vec) {
		Location<World> add = castLocation.add(vec);
		add.getExtent().spawnParticles(portalState.particleEffect, add.getPosition());
	}

	public void setTargetLocation(Location<World> location) {
		targetLocation = location;
		updateState();
	}

	private void updateState() {
		if (targetLocation == null) {
			portalState = PortalState.UNINITIALIZED;
			return;
		}
		World extent = targetLocation.getExtent();
		Vector3d position = targetLocation.getPosition();
		BlockState block = extent.getBlock(position.getFloorX(), position.getFloorY(), position.getFloorZ());
		portalState = PortalState.getByBlock(block);
	}

	private Location<World> processChanceToFail(Location<World> desiredLocation) {
		XORShiftRnd rnd = new XORShiftRnd();
		if (rnd.nextDouble(100) < chanceToFail) {
			Vector3d random = VectorUtils.getRandomPointBetween(desiredLocation.getPosition(), targetLocation.getPosition());
			Location<World> add = castLocation.add(random);
			if (safe) {
				Optional<Location<World>> safeLocation = Sponge.getTeleportHelper().getSafeLocation(add, 10, 20);
				if (!safeLocation.isPresent()) {
					return desiredLocation;
				}
				return safeLocation.get();
			} else {
				return add;
			}
		}
		return desiredLocation;
	}

	public enum PortalState {
		WATER(inWater),
		LAVA(inLava),
		UNINITIALIZED(uninicialized),
		INITIALIZED(initialized);

		private final ParticleEffect particleEffect;

		PortalState(ParticleEffect effect) {
			this.particleEffect = effect;
		}

		static PortalState getByBlock(BlockState state) {
			BlockType type = state.getType();
			if (type == BlockTypes.LAVA || type == BlockTypes.FLOWING_LAVA) {
				return LAVA;
			}
			if (type == BlockTypes.WATER || type == BlockTypes.FLOWING_WATER) {
				return WATER;
			}
			return INITIALIZED;
		}

		public ParticleEffect getParticleEffect() {
			return particleEffect;
		}
	}
}
