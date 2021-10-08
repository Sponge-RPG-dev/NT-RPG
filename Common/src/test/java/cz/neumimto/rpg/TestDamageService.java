package cz.neumimto.rpg;

import cz.neumimto.rpg.common.entity.IEntity;
import cz.neumimto.rpg.common.damage.DamageService;

import javax.inject.Singleton;

@Singleton
public class TestDamageService extends DamageService {
    @Override
    public void damageEntity(IEntity character, double value) {

    }

    @Override
    public void init() {

    }
}
