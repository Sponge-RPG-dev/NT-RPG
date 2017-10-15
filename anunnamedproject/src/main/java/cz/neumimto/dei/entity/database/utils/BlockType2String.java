package cz.neumimto.dei.entity.database.utils;

import cz.neumimto.dei.exceptions.NotExistingItemTypeException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;

import javax.persistence.AttributeConverter;
import java.util.Optional;

/**
 * Created by NeumimTo on 6.7.2016.
 */
public class BlockType2String implements AttributeConverter<BlockType, String> {

	@Override
	public String convertToDatabaseColumn(BlockType attribute) {
		return attribute.getId();
	}

	@Override
	public BlockType convertToEntityAttribute(String s) {
		Optional<BlockType> type = Sponge.getGame().getRegistry().getType(BlockType.class, s);
		if (type.isPresent()) {
			return type.get();
		}
		throw new NotExistingItemTypeException(s);
	}
}
