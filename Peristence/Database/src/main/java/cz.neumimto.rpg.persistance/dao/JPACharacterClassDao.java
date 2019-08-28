package cz.neumimto.rpg.persistance.dao;

import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.common.persistance.dao.ICharacterClassDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by NeumimTo on 24.2.2019.
 */
@Singleton
public class JPACharacterClassDao implements ICharacterClassDao {

    private final DataSource dataSource;

    public JPACharacterClassDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public void update(CharacterClass characterClass) {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement("update rpg_character_class " +
                    "experiences = ? " +
                    "level = ? " +
                    "name = ? " +
                    "usedSkillPoints = ? " +
                    "skillPoints = ? where class_id = ?")) {
                pst.setDouble(0, characterClass.getExperiences());
                pst.setInt(1, characterClass.getLevel());
                pst.setString(2, characterClass.getName());
                pst.setInt(3, characterClass.getUsedSkillPoints());
                pst.setInt(4, characterClass.getSkillPoints());

                pst.setLong(5, characterClass.getId());

                pst.executeUpdate();
            }
        } catch (SQLException e) {

        }
    }

}
