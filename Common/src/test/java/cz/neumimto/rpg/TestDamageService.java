package cz.neumimto.rpg;

import cz.neumimto.rpg.api.entity.IEntity;
import cz.neumimto.rpg.common.damage.DamageServiceImpl;

import javax.inject.Singleton;

@Singleton
public class TestDamageService extends DamageServiceImpl {
    @Override
    public void damageEntity(IEntity character, double maxValue) {

    }
}
