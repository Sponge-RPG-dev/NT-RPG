package cz.neumimto.rpg.persistence.jdbc.dao;

import cz.neumimto.rpg.api.logging.Log;
import cz.neumimto.rpg.api.persistance.model.CharacterBase;
import cz.neumimto.rpg.api.persistance.model.CharacterClass;
import cz.neumimto.rpg.api.persistance.model.CharacterSkill;
import cz.neumimto.rpg.api.persistance.model.TimestampEntity;
import cz.neumimto.rpg.common.persistance.dao.IPlayerDao;
import cz.neumimto.rpg.persistence.jdbc.NamedPreparedStatement;
import cz.neumimto.rpg.persistence.jdbc.converters.EquipedSlot2Json;
import cz.neumimto.rpg.persistence.model.CharacterBaseImpl;
import cz.neumimto.rpg.persistence.model.CharacterClassImpl;
import cz.neumimto.rpg.persistence.model.CharacterSkillImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Singleton
public class JdbcPlayerDao implements IPlayerDao {

    @Inject
    private DataSource dataSource;

    private static final String FIND_CHAR = "SELECT * FROM rpg_character_base WHERE uuid = ? and marked_for_removal = false order by updated desc limit 1";
    private static final String FIND_CHARS = "SELECT * FROM rpg_character_base WHERE uuid = ? and marked_for_removal = false";
    private static final String FIND_CHAR_BY_NAME = "SELECT * FROM rpg_character_base WHERE uuid = ? and marked_for_removal = false and name = ?";
    private static final String FIND_SKILLS = "SELECT sk.*, cl.class_id as class FROM rpg_character_skill as sk left join rpg_character_base as c on sk.character_id = c.character_id left join rpg_character_class as cl on sk.class_id = cl.class_id WHERE c.uuid = ?";
    private static final String FIND_CLASSES_BY_CHAR = "SELECT cl.* from rpg_character_class as cl left join rpg_character_base as cb on cb.character_id = cl.character_id WHERE cb.uuid = ?";

    public JdbcPlayerDao setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
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
            Log.error("Could not retrieve Players characters from database", s);
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
            Log.error("Could not retrieve last played character from database", s);
        }

        List<CharacterClassImpl> characterClasses = loadClasses(uuid, characterBase);
        loadSkills(uuid, characterBase, characterClasses);


        characterBase.postLoad();
        return characterBase;
    }

    protected List<CharacterClassImpl> loadClasses(UUID uuid, CharacterBaseImpl characterBase) {
        List<CharacterClassImpl> characterClasses = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement pst = con.prepareStatement(FIND_CLASSES_BY_CHAR)) {
            pst.setString(1, uuid.toString());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    CharacterClassImpl characterClass = loadCharacterClass(characterBase, rs);
                    characterClasses.add(characterClass);
                }
            }
        } catch (SQLException e) {
            Log.error("Could not load classes for character " + characterBase.toString());
        }
        characterBase.setCharacterClasses(new HashSet<>(characterClasses));
        return characterClasses;
    }

    protected List<CharacterSkillImpl> loadSkills(UUID uuid, CharacterBaseImpl characterBase, List<CharacterClassImpl> characterClasses) {
        List<CharacterSkillImpl> characterSkills = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement pst = con.prepareStatement(FIND_SKILLS)) {
                pst.setString(0, uuid.toString());
                try (ResultSet rs = pst.executeQuery()) {
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
        return characterClass;
    }

    private void loadCharacterBase(CharacterBaseImpl characterBase, ResultSet rs) throws SQLException {
        characterBase.setId(rs.getLong("character_id"));
        characterBase.setUuid(UUID.fromString(rs.getString("uuid")));
        characterBase.setName(rs.getString("name"));
        characterBase.setAttributePoints(rs.getInt("attribute_points"));
        characterBase.setCanResetSkills(rs.getBoolean("can_reset_skills"));
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
        } catch (SQLException e) {
            Log.error("Could not process action getCharacterCount", e);
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
            Log.error("Could not process action deleteData", e);
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
            Log.error("Could not process action markCharacterForRemoval", e);
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
            Log.error("Could not delete " + characterSkill, e);
        }
    }


    @Override
    public void create(CharacterBase base) {
        String sql = "insert into rpg_character_base(" +
                "uuid, name, info, health_scale, " +
                "attribute_points, attribute_points_spent," +
                "can_reset_skills, marked_for_removal," +
                "last_known_player_name, last_reset_time, inventory_equip_slot_order," +
                "x, y, z, world" +
                ") VALUES (" +
                ":uuid:, :char_name:, :info:, :health_scale:, " +
                ":attribute_points:, :attribute_points_spent:," +
                ":can_reset_skills:, :marked_for_removal:," +
                ":last_known_player_name:, :last_reset_time:, :inventory_equip_slot_order:," +
                ":x:, :y:, :z:, :world:" +
                ")";
        base.onCreate();
        base.onUpdate();
        try (Connection con = dataSource.getConnection();
             NamedPreparedStatement pst = new NamedPreparedStatement(con, sql, Statement.RETURN_GENERATED_KEYS)) {
            bindCharacterBaseToStatement(pst, base);
            long l = pst.executeQueryAndGetId();
            base.setId(l);
        } catch (SQLException ex) {
            Log.error("Could not execute SQL to insert a new record to rpg_character_base table", ex);
            throw new CannotCreateCharacterBaseSQL();
        }
        for (CharacterClass characterClass : base.getCharacterClasses()) {
            createCharacterClass(characterClass);
        }
    }

    @Override
    public void update(CharacterBase characterBase) {
        characterBase.onUpdate();
        updateCharacterBase(characterBase);
        Set<CharacterClass> characterClasses = characterBase.getCharacterClasses();
        for (CharacterClass characterClass : characterClasses) {
            if (characterClass.getId() == null) {
                createCharacterClass(characterClass);
            } else {
                updateCharacterClass(characterClass);
            }
        }

        for (CharacterSkill characterSkill : characterBase.getCharacterSkills()) {
            if (characterSkill.getId() == null) {
                createCharacterSkill(characterSkill);
            } else {
                updateCharacterSkill(characterSkill);
            }
        }
    }

    private void updateCharacterSkill(CharacterSkill characterSkill) {
        String sql = "update rpg_character_skill set level=:level: where skill_id = :skill_id:";
        try (Connection con = dataSource.getConnection();
             NamedPreparedStatement pst = new NamedPreparedStatement(con, sql, Statement.RETURN_GENERATED_KEYS)) {
            bindCharacterSkill(pst, characterSkill);
            long id = pst.executeQueryAndGetId();
            characterSkill.setId(id);
        } catch (SQLException e) {
            Log.error("Could not create CharacterClass " + characterSkill.toString(), e);
        }
    }

    private void createCharacterSkill(CharacterSkill characterSkill) {
        String sql = "insert into rpg_character_skill(catalog_id,level,character_id,class_id) values (:catalog_id:,:level:,:character_id:,:class_id:)";
        try (Connection con = dataSource.getConnection();
             NamedPreparedStatement pst = new NamedPreparedStatement(con, sql, Statement.RETURN_GENERATED_KEYS)) {
            bindCharacterSkill(pst, characterSkill);
            long id = pst.executeQueryAndGetId();
            characterSkill.setId(id);
        } catch (SQLException e) {
            Log.error("Could not create CharacterClass " + characterSkill.toString(), e);
        }
    }

    private void bindCharacterSkill(NamedPreparedStatement pst, CharacterSkill characterSkill) throws SQLException {
        if (characterSkill.getId() != null) {
            pst.setLong(":skill_id:", characterSkill.getId());
        }
        pst.setString(":catalog_id:", characterSkill.getCatalogId());
        pst.setInt(":level:", characterSkill.getLevel());
        pst.setLong(":character_id:", characterSkill.getCharacterBase().getId());
        if (characterSkill.getFromClass() != null) {
            pst.setLong(":class_id:", characterSkill.getFromClass().getId());
        }
    }

    private void updateCharacterClass(CharacterClass characterClass) {
        String sql = "update rpg_character_class set experiences = :experiences: ,name = :name:,skillpoints = :skillpoints: ,used_skil_points = :used_skil_points: where class_id = :class_id:";
        try (Connection con = dataSource.getConnection();
             NamedPreparedStatement pst = new NamedPreparedStatement(con, sql)) {
            pst.executeQuery();
            bindCharacterClass(pst, characterClass);
        } catch (SQLException e) {
            Log.error("Could not update Character class  " + characterClass.toString(), e);
        }
    }

    private void createCharacterClass(CharacterClass characterClass) {
        String sql = "insert into rpg_character_class(experiences,name,level,skillpoints,used_skil_points,character_id) VALUES(:experiences:,:name:,:level:,:skillpoints:,:used_skil_points:,:character_id:)";
        try (Connection con = dataSource.getConnection();
             NamedPreparedStatement pst = new NamedPreparedStatement(con, sql, Statement.RETURN_GENERATED_KEYS)) {
            bindCharacterClass(pst, characterClass);
            long id = pst.executeQueryAndGetId();
            characterClass.setId(id);
        } catch (SQLException e) {
            Log.error("Could not create CharacterClass " + characterClass.toString(), e);
        }
    }

    private void bindCharacterClass(NamedPreparedStatement pst, CharacterClass characterClass) throws SQLException {
        if (characterClass.getId() != null) {
            pst.setLong(":class_id:", characterClass.getId());
        }

        pst.setDouble(":experiences:", characterClass.getExperiences());
        pst.setString(":name:", characterClass.getName());
        pst.setDouble(":level:", characterClass.getLevel());
        pst.setInt(":skillpoints:", characterClass.getSkillPoints());
        pst.setInt(":used_skil_points:", characterClass.getUsedSkillPoints());
        pst.setLong(":character_id:", characterClass.getCharacterBase().getId());
    }

    private void updateCharacterBase(CharacterBase characterBase) {
        String sql = "UPDATE into rpg_character_base " +
                " set info = :info:, health_scale = :health_scale:, " +
                "attribute_points = :attribute_points:, attribute_points_spent = :attribute_points_spent:," +
                "can_reset_skills = :can_reset_skills:, marked_for_removal = :marked_for_removal:," +
                "last_known_player_name = :last_known_player_name:, last_reset_time = :last_reset_time:, inventory_equip_slot_order = :inventory_equip_slot_order:," +
                "x = :x:, y = :y:, z = :z:, world = :world:" +
                " where uuid = :uuid: AND name = :char_name:";

        try (Connection con = dataSource.getConnection();
             NamedPreparedStatement pst = new NamedPreparedStatement(con, sql)) {
            bindCharacterBaseToStatement(pst, characterBase);
        } catch (SQLException e) {
            Log.error("Could not update " + characterBase, e);
        }
    }


    private void bindCharacterBaseToStatement(NamedPreparedStatement pspt, CharacterBase characterBase) throws SQLException {
        pspt.setString(":uuid:", characterBase.getUuid().toString());
        pspt.setString(":char_name:", characterBase.getName());
        pspt.setDouble(":health_scale:", characterBase.getHealthScale());
        pspt.setInt(":attribute_points:", characterBase.getAttributePoints());
        pspt.setInt(":attribute_points_spent:", characterBase.getAttributePointsSpent());
        pspt.setBoolean(":can_reset_skills:", characterBase.canResetSkills());
        pspt.setBoolean(":marked_for_removal:", characterBase.getMarkedForRemoval());
        pspt.setString(":last_known_player_name:", characterBase.getLastKnownPlayerName());
        pspt.setDate(":last_reset_time:", characterBase.getLastReset());
        pspt.setString(":inventory_equip_slot_order:", new EquipedSlot2Json().convertToDatabaseColumn(characterBase.getInventoryEquipSlotOrder()));
        pspt.setInt(":x:", characterBase.getX());
        pspt.setInt(":y:", characterBase.getY());
        pspt.setString(":info:", "");
        pspt.setInt(":z:", characterBase.getZ());
        pspt.setString(":world:", characterBase.getWorld());
    }

    private static class CannotCreateCharacterBaseSQL extends RuntimeException {
    }

    private static class CannotFetchCharacterBaseSQL extends RuntimeException {
    }

    private static class NonUniqueResultSql extends RuntimeException {
    }
}
