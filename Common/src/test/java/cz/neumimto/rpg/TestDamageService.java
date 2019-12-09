package cz.neumimto.rpg;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.common.damage.AbstractDamageService;

import javax.inject.Singleton;

@Singleton
public class TestDamageService extends AbstractDamageService {
    @Override
    public void damageEntity(IEntity character, double value) {

    }

    @Override
    public void init() {

    }
}
