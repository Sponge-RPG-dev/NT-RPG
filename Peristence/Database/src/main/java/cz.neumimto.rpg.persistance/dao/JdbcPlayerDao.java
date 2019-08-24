package cz.neumimto.rpg.persistance.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.api.persistance.model.TimestampEntity;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistance.converters.EquipedSlot2Json;
import cz.neumimto.rpg.persistance.model.JPACharacterBase;
import cz.neumimto.rpg.persistance.model.JPACharacterClass;
import cz.neumimto.rpg.persistance.model.JPACharacterSkill;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class JdbcPlayerDao implements IPlayerDao {

    private final DataSource dataSource;
    private static final String SQL_FIND_CHAR = "SELECT * FROM rpg_character_base WHERE uuid = ? and marked_for_removal = false order by updated desc limit 1";
    private static final String SQL_FIND_SKILLS = "SELECT sk.*, cl.class_id as class FROM rpg_character_skill as sk left join rpg_character_base as c on sk.character_id = c.character_id left join rpg_character_class as cl on sk.class_id = cl.class_id WHERE c.uuid = ?";
    private static final String SQL_FIND_CLASSES_BY_CHAR = "SELECT cl.* from rpg_character_class as cl left join rpg_character_base as cb on cb.character_id = cl.character_id WHERE cb.uuid = ?";

    public JdbcPlayerDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<CharacterBase> getPlayersCharacters(UUID uuid) {
        return null;
    }

    @Override
    public CharacterBase getLastPlayed(UUID uuid) {
        JPACharacterBase characterBase = new JPACharacterBase();

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement(SQL_FIND_CHAR)) {
                pst.setString(0, uuid.toString());
                try (ResultSet rs = pst.executeQuery();){
                    while (rs.next()) {
                        loadCharacterBase(characterBase, rs);
                    }
                }
            }
        } catch (SQLException s) {

        }

        List<JPACharacterClass> characterClasses = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement(SQL_FIND_CLASSES_BY_CHAR)) {
                pst.setString(0, uuid.toString());
                try (ResultSet rs = pst.executeQuery()) {
                    JPACharacterClass characterClass = loadCharacterClass(characterBase, rs);
                    characterClasses.add(characterClass);
                }
            }
        } catch (SQLException e) {

        }

        List<JPACharacterSkill> characterSkills = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement(SQL_FIND_SKILLS)) {
                pst.setString(0, uuid.toString());
                try (ResultSet rs = pst.executeQuery();) {
                    while (rs.next()) {
                        JPACharacterSkill characterSkill = loadCharacterSkills(characterBase, characterClasses, rs);
                        characterSkills.add(characterSkill);
                    }
                }
            }
        } catch (SQLException e) {

        }

        characterBase.postLoad();
        return characterBase;
    }

    private JPACharacterSkill loadCharacterSkills(JPACharacterBase characterBase, List<JPACharacterClass> characterClasses, ResultSet rs) throws SQLException {
        JPACharacterSkill characterSkill = new JPACharacterSkill();
        characterSkill.setCatalogId(rs.getString("catalog_id"));
        characterSkill.setCooldown(rs.getLong("cooldown"));
        characterSkill.setId(rs.getLong("skill_id"));
        characterSkill.setLevel(rs.getInt("level"));
        characterSkill.setCharacterBase(characterBase);


        long classId = rs.getLong("class_id");
        for (JPACharacterClass characterClass : characterClasses) {
            if (characterClass.getId() == classId) {
                characterSkill.setFromClass(characterClass);
                break;
            }
        }
        populateCommonDateFields(characterSkill, rs);
        return characterSkill;
    }

    private JPACharacterClass loadCharacterClass(JPACharacterBase characterBase, ResultSet rs) throws SQLException {
        JPACharacterClass characterClass = new JPACharacterClass();
        characterClass.setCharacterBase(characterBase);
        characterClass.setId(rs.getLong("class_id"));
        characterClass.setExperiences(rs.getDouble("experiences"));
        characterClass.setLevel(rs.getInt("level"));
        characterClass.setName(rs.getString("name"));
        characterClass.setSkillPoints(rs.getInt("skillpoints"));
        characterClass.setUsedSkillPoints(rs.getInt("used_skil_points"));
        populateCommonDateFields(characterBase, rs);
        return characterClass;
    }

    private void loadCharacterBase(JPACharacterBase characterBase, ResultSet rs) throws SQLException {
        characterBase.setId(rs.getLong("character_id"));
        characterBase.setUuid(rs.getObject("uuid", UUID.class));
        characterBase.setName(rs.getString("name"));
        characterBase.setName(rs.getString("info"));
        characterBase.setAttributePoints(rs.getInt("attribute_points"));
        characterBase.setCanResetskills(rs.getBoolean("can_reset_skills"));
        characterBase.setHealthScale(rs.getDouble("health_scale"));
        characterBase.setLastKnownPlayerName(rs.getString("last_known_player_name"));
        characterBase.setLastReset(rs.getDate("last_reset_time"));
        characterBase.setInventoryEquipSlotOrder(new EquipedSlot2Json().convertToEntityAttribute(rs.getString("inventory_equip_slot_order")));
        characterBase.setMarkedForRemoval(rs.getBoolean("marked_for_removal"));
        characterBase.setAttributePointsSpent(rs.getInt("attribute_points_spent"));
        characterBase.setX(rs.getInt("x"));
        characterBase.setZ(rs.getInt("z"));
        characterBase.setY(rs.getInt("y"));
        characterBase.setWorld(rs.getString("world"));
        populateCommonDateFields(characterBase, rs);
    }

    private void populateCommonDateFields(TimestampEntity characterBase, ResultSet rs) throws SQLException {
        characterBase.setUpdated(rs.getDate("updated"));
        characterBase.setUpdated(rs.getDate("created"));
    }

    @Override
    public CharacterBase getCharacter(UUID player, String name) {
        return null;
    }

    @Override
    public int getCharacterCount(UUID uuid) {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement("SELECT count(*) FROM rpg_character_base WHERE uuid = ? and marked_for_removal = false")) {
                pst.setString(0, uuid.toString());
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        return rs.getInt(0);
                    }
                }
            }
        } catch (SQLException s) {

        }
        return 0;
    }

    @Override
    public int deleteData(UUID uniqueId) {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement("delete from rpg_character_base where uuid = ? CASCADE")) {
                pst.setString(0, uniqueId.toString());
                return pst.executeUpdate();
            }
        } catch (SQLException e) {

        }
        return 0;
    }

    @Override
    public void createAndUpdate(CharacterBase base) {

    }

    @Override
    public int markCharacterForRemoval(UUID player, String charName) {
        return 0;
    }

    @Override
    public CharacterBase fetchCharacterBase(CharacterBase characterBase) {
        return null;
    }

    @Override
    public void update(CharacterBase characterBase) {

    }

    @Override
    public void removePersitantSkill(CharacterSkill characterSkill) {

    }
}
