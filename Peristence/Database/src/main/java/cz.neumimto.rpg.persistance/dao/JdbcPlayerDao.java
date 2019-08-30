package cz.neumimto.rpg.persistance.dao;

import com.google.j2objc.annotations.ReflectionSupport;
import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.persistance.model.*;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistance.converters.EquipedSlot2Json;
import cz.neumimto.rpg.persistance.model.BaseCharacterAttributeImpl;
import cz.neumimto.rpg.persistance.model.CharacterBaseImpl;
import cz.neumimto.rpg.persistance.model.CharacterClassImpl;
import cz.neumimto.rpg.persistance.model.CharacterSkillImpl;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class JdbcPlayerDao implements IPlayerDao {

    private final DataSource dataSource;

    private static final String FIND_CHAR = "SELECT * FROM rpg_character_base WHERE uuid = ? and marked_for_removal = false order by updated desc limit 1";
    private static final String FIND_CHARS = "SELECT * FROM rpg_character_base WHERE uuid = ? and marked_for_removal = false";
    private static final String FIND_CHAR_BY_NAME = "SELECT * FROM rpg_character_base WHERE uuid = ? and marked_for_removal = false and name = ?";
    private static final String FIND_SKILLS = "SELECT sk.*, cl.class_id as class FROM rpg_character_skill as sk left join rpg_character_base as c on sk.character_id = c.character_id left join rpg_character_class as cl on sk.class_id = cl.class_id WHERE c.uuid = ?";
    private static final String FIND_CLASSES_BY_CHAR = "SELECT cl.* from rpg_character_class as cl left join rpg_character_base as cb on cb.character_id = cl.character_id WHERE cb.uuid = ?";

    public JdbcPlayerDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List getPlayersCharacters(UUID uuid) {
        List<CharacterBaseImpl> list = new ArrayList<>();

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement(FIND_CHARS)) {
                pst.setString(0, uuid.toString());
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        CharacterBaseImpl characterBase = new CharacterBaseImpl();
                        loadCharacterBase(characterBase, rs);
                        list.add(characterBase);
                    }
                }
            }
        } catch (SQLException s) {

        }

        for (CharacterBaseImpl characterBase : list) {
            List<CharacterClassImpl> characterClassImpls = loadClasses(uuid, characterBase);
            loadSkills(uuid, characterBase, characterClassImpls);
        }

        return list;
    }

    @Override
    public CharacterBase getLastPlayed(UUID uuid) {
        CharacterBaseImpl characterBase = new CharacterBaseImpl();

        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement(FIND_CHAR)) {
                pst.setString(0, uuid.toString());
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        loadCharacterBase(characterBase, rs);
                    }
                }
            }
        } catch (SQLException s) {

        }

        List<CharacterClassImpl> characterClasses = loadClasses(uuid, characterBase);
        loadSkills(uuid, characterBase, characterClasses);


        characterBase.postLoad();
        return characterBase;
    }

    protected List<CharacterClassImpl> loadClasses(UUID uuid, CharacterBaseImpl characterBase) {
        List<CharacterClassImpl> characterClasses = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement(FIND_CLASSES_BY_CHAR)) {
                pst.setString(0, uuid.toString());
                try (ResultSet rs = pst.executeQuery()) {
                    CharacterClassImpl characterClass = loadCharacterClass(characterBase, rs);
                    characterClasses.add(characterClass);
                }
            }
        } catch (SQLException e) {

        }
        characterBase.setCharacterClasses(new HashSet<>(characterClasses));
        return characterClasses;
    }

    protected List<CharacterSkillImpl> loadSkills(UUID uuid, CharacterBaseImpl characterBase, List<CharacterClassImpl> characterClasses) {
        List<CharacterSkillImpl> characterSkills = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement(FIND_SKILLS)) {
                pst.setString(0, uuid.toString());
                try (ResultSet rs = pst.executeQuery();) {
                    while (rs.next()) {
                        CharacterSkillImpl characterSkill = loadCharacterSkills(characterBase, characterClasses, rs);
                        characterSkills.add(characterSkill);
                    }
                }
            }
        } catch (SQLException e) {

        }
        characterBase.setCharacterSkills(new HashSet<>(characterSkills));
        return characterSkills;
    }

    private CharacterSkillImpl loadCharacterSkills(CharacterBaseImpl characterBase, List<CharacterClassImpl> characterClasses, ResultSet rs) throws SQLException {
        CharacterSkillImpl characterSkill = new CharacterSkillImpl();
        characterSkill.setCatalogId(rs.getString("catalog_id"));
        characterSkill.setCooldown(rs.getLong("cooldown"));
        characterSkill.setId(rs.getLong("skill_id"));
        characterSkill.setLevel(rs.getInt("level"));
        characterSkill.setCharacterBase(characterBase);


        long classId = rs.getLong("class_id");
        for (CharacterClassImpl characterClass : characterClasses) {
            if (characterClass.getId() == classId) {
                characterSkill.setFromClass(characterClass);
                break;
            }
        }
        populateCommonDateFields(characterSkill, rs);
        return characterSkill;
    }

    private CharacterClassImpl loadCharacterClass(CharacterBaseImpl characterBase, ResultSet rs) throws SQLException {
        CharacterClassImpl characterClass = new CharacterClassImpl();
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

    private void loadCharacterBase(CharacterBaseImpl characterBase, ResultSet rs) throws SQLException {
        characterBase.setId(rs.getLong("character_id"));
        characterBase.setUuid(UUID.fromString(rs.getString("uuid")));
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
    public CharacterBase getCharacter(UUID uuid, String name) {
        CharacterBaseImpl characterBase = new CharacterBaseImpl();
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement(FIND_CHAR_BY_NAME)) {
                pst.setString(1, uuid.toString());
                pst.setString(2, name);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        loadCharacterBase(characterBase, rs);
                    }
                }
            }
        } catch (SQLException s) {
            throw new CannotFetchCharacterBaseSQL();
        }

        List<CharacterClassImpl> characterClasses = loadClasses(uuid, characterBase);
        List<CharacterSkillImpl> characterSkills = loadSkills(uuid, characterBase, characterClasses);

        characterBase.setCharacterClasses(new HashSet<>(characterClasses));
        characterBase.setCharacterSkills(new HashSet<>(characterSkills));
        characterBase.postLoad();
        return characterBase;
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
    public int markCharacterForRemoval(UUID uniqueId, String charName) {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement("update rpg_character_base set marked_for_removal = true where uuid = ? and name = ?")) {
                pst.setString(0, uniqueId.toString());
                pst.setString(1, charName);
                return pst.executeUpdate();
            }
        } catch (SQLException e) {

        }
        return 0;
    }


    @Override
    public void removePersitantSkill(CharacterSkill characterSkill) {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement("delete from rpg_character_skill where skill_id = ?")) {
                pst.setLong(0, characterSkill.getId());
                pst.executeUpdate();
            }
        } catch (SQLException e) {

        }
    }


    @Override
    public void create(CharacterBase base) {
        String sql = "insert into rpg_character_base" +
                "(" +
                "uuid, name, info, health_scale, " +
                "attribute_points, attribute_points_spent," +
                "can_reset_skills, marked_for_removal," +
                "last_known_player_name, last_reset_time, inventory_equip_slot_order," +
                "x, y, z, world" +
                ") VALUES (" +
                "?,?,?," +
                "?,?,?," +
                "?,?,?," +
                "?,?,?," +
                "?,?,?" +
                ")";
        base.onCreate();
        base.onUpdate();
        try (Connection con = dataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, base.getUuid().toString());
            pst.setString(2, base.getName());
            pst.setString(3, base.getInfo());
            pst.setDouble(4, base.getHealthScale());

            pst.setInt(5, base.getAttributePoints());
            pst.setInt(6, base.getAttributePointsSpent());

            pst.setBoolean(7, base.isCanResetskills());
            pst.setBoolean(8, base.getMarkedForRemoval());

            pst.setString(9, base.getLastKnownPlayerName());
            pst.setDate(10, null);
            pst.setString(11, new EquipedSlot2Json().convertToDatabaseColumn(base.getInventoryEquipSlotOrder()));


            pst.setInt(12, base.getX());
            pst.setInt(13, base.getY());
            pst.setInt(14, base.getZ());
            pst.setString(15, base.getWorld());
            int i = pst.executeUpdate();
            if (i == 0) {
                throw new CannotCreateCharacterBaseSQL();
            }
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long id = generatedKeys.getInt(1);
                    base.setId(id);
                }
            }

        } catch (SQLException ex) {
            Log.error("Could not execute SQL to insert a new rectord to rpg_character_base table", ex);
            throw new CannotCreateCharacterBaseSQL();
        }
    }

    @Override
    public void update(CharacterBase characterBase) {

    }

    private static class CannotCreateCharacterBaseSQL extends RuntimeException {}
    private static class CannotFetchCharacterBaseSQL extends RuntimeException {}
    private static class NonUniqueResultSql extends RuntimeException {}
}
