package cz.neumito.rpg.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.NClass;
import cz.neumimto.rpg.skills.ExtendedSkillInfo;
import cz.neumimto.rpg.skills.SkillData;
import cz.neumimto.rpg.skills.SkillService;
import cz.neumimto.rpg.skills.SkillTree;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import spark.Spark;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Singleton
public class RestService {

    @Inject
    private CharacterService characterService;

    @Inject
    private SkillService service;

    @Inject
    private SkillService skillService;

    private Thread t;

    @Inject
    NtRpgPlugin plugin;


    String charset = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ!@#$";

    SecureRandom random = new SecureRandom();

    final private Map<String, Token> tokens = new ConcurrentHashMap<>();

    public void getSkeleton(String player, Consumer<String> consumer) {
        Sponge.getScheduler().createTaskBuilder().execute(()-> {
            Optional<Player> player1 = Sponge.getGame().getServer().getPlayer(player);
            if (player1.isPresent()) {
                UUID uniqueId = player1.get().getUniqueId();
                IActiveCharacter character = characterService.getCharacter(uniqueId);
                ExtendedNClass primaryClass = character.getPrimaryClass();
                NClass nClass = primaryClass.getnClass();
                SkillTree skillTree = nClass.getSkillTree();
                String nodes = toJson(skillTree);
                Sponge.getScheduler().createTaskBuilder().async().execute(()-> consumer.accept(nodes)).submit(plugin);
            }
        }).submit(plugin);
    }

    public String toJson(Map<String, ExtendedSkillInfo> skills) {
        return gson.toJson(skills);
    }

    private Gson gson = new GsonBuilder().registerTypeAdapter(SkillTree.class, new TypeAdapter<SkillTree>() {
        @Override
        public void write(JsonWriter jsonWriter, SkillTree skillTree) throws IOException {
            Collection<SkillData> values = skillTree.getSkills().values();
            jsonWriter.beginObject();
            jsonWriter.name("skills");
            jsonWriter.beginArray();
            for (SkillData skillData : values) {
                jsonWriter.beginObject();

                jsonWriter.name("skill").value(skillData.getSkillName());
                jsonWriter.name("image").value(skillData.getSkill().getIconURL());
                jsonWriter.name("soft");
                jsonWriter.beginArray();
                for (SkillData data : skillData.getSoftDepends()) {
                    jsonWriter.value(data.getSkillName());
                }
                jsonWriter.endArray();
                jsonWriter.name("hard");
                jsonWriter.beginArray();
                for (SkillData data : skillData.getHardDepends()) {
                    jsonWriter.value(data.getSkillName());
                }
                jsonWriter.endArray();
                jsonWriter.name("conflicts");
                jsonWriter.beginArray();
                for (SkillData data : skillData.getConflicts()) {
                    jsonWriter.value(data.getSkillName());
                }
                jsonWriter.endArray();
                jsonWriter.endObject();
            }
            jsonWriter.endArray().endObject();
        }

        @Override
        public SkillTree read(JsonReader jsonReader) throws IOException {
            throw new UnsupportedOperationException();
        }
    }).registerTypeAdapter(ExtendedSkillInfo.class, new TypeAdapter<ExtendedSkillInfo>() {
        @Override
        public void write(JsonWriter jsonWriter, ExtendedSkillInfo o) throws IOException {
            jsonWriter.beginObject();
            jsonWriter.name("level").value(o.getLevel());
            jsonWriter.endObject();

        }

        @Override
        public ExtendedSkillInfo read(JsonReader jsonReader) throws IOException {
            throw new UnsupportedOperationException();
        }
    }).registerTypeAdapter(Token.class, new TypeAdapter<Token>() {
        @Override
        public void write(JsonWriter out, Token value) throws IOException {

        }

        @Override
        public Token read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return new Token("");
            }
            String s = in.nextString();
            return new Token(s);
        }
    }).create();

    private static Map<String,String> cachedTrees = new HashMap<>();

    public String toJson(SkillTree skillTree) {
        String s = cachedTrees.get(skillTree.getId());
        if (s == null) {
            s = gson.toJson(skillTree);
            cachedTrees.put(skillTree.getId(),s);
        }
        return s;

    }
    Runnable r = () -> {
        //todo config
        Spark.port(9080);
        Spark.threadPool(5);
        Spark.get("/getSkeleton/:player",(request, response) -> {
            String player = request.params(":player");
            CountDownLatch countDownLatch = new CountDownLatch(1);
            getSkeleton(player,s -> {
                response.body(s);
                countDownLatch.countDown();
            });
            countDownLatch.await();
            return response.body();
        });
        Spark.get("/createToken/:player", (request, response) -> {
            String player = request.params(":player");
            createToken(player,request.ip());
            return "{'status':'requested'}";
        });
        Spark.get("/getSkills/:player", (request, response) -> {
            CountDownLatch latch = new CountDownLatch(1);
            String player = request.params(":player");
            getSkills(player, map -> {
                String json = toJson(map);
                response.body(json);
                latch.countDown();
            });
            latch.await();
            return response.body();
        });
        Spark.post("/getCharacter/:player",(request, response) -> {
            CountDownLatch l = new CountDownLatch(1);
            String player = request.params(":player");
            Token token = gson.fromJson(request.body(),Token.class);
            if (token.equals(tokens.get(player))) {
                getCharacterData(player,r -> {
                    response.body(gson.toJson(r));
                    l.countDown();
                });
            }
            l.await();
            return response.body();
        });
    };

    private void getCharacterData(String player, Consumer<CharacterData> data) {
        Sponge.getGame().getScheduler().createTaskBuilder().execute(() -> {
            Optional<Player> p = Sponge.getGame().getServer().getPlayer(player);
            if (p.isPresent()) {
                Player l = p.get();
                IActiveCharacter character = characterService.getCharacter(l.getUniqueId());
                data.accept(CharacterData.fromCharacter(character));
            }
        }).submit(plugin);
    }

    private void getSkills(String player, Consumer<Map> consumer) {
        Sponge.getGame().getScheduler().createTaskBuilder().execute(() -> {
            Optional<Player> a = Sponge.getGame().getServer().getPlayer(player);
            if (a.isPresent()) {
                Player player1 = a.get();
                IActiveCharacter character = characterService.getCharacter(player1.getUniqueId());
                consumer.accept(character.getSkills());
            }
        }).submit(plugin);
    }

    public String getToken() {
        StringBuilder token = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            token.append(charset.charAt(random.nextInt(charset.length())));
        }
        return token.toString();
    }

    private void cleanupTokenCache() {
        Iterator<Map.Entry<String, Token>> iterator = tokens.entrySet().iterator();
        while (iterator.hasNext()) {
            long l = System.currentTimeMillis();
            Map.Entry<String, Token> next = iterator.next();
            if (next.getValue().time + 7200000 <= l ) {
                iterator.remove();
            }
        }
    }

    private void createToken(String player, String ip) {
        cleanupTokenCache();
        final Token token = new Token(getToken());
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            Optional<Player> player1 = Sponge.getGame().getServer().getPlayer(player);
            if (player1.isPresent()) {
                tokens.put(player1.get().getName().toLowerCase(),token);
                player1.get().sendMessage(Text.of(String.format("Requested token from ip ,%s ",ip)));
                player1.get().sendMessage(Text.of(String.format("Token: %s ",token)));
            }
        }).submit(plugin);
    }

    public void startServer() {
        if (t == null || t.isInterrupted()) {
            t = new Thread(r);
            t.start();
            return;
        }
        Spark.stop();
        t.interrupt();
        //todo try catch on await
        startServer();
    }

    @PostProcess(priority = 10000)
    public void init()  {
        service.initIcons();
        startServer();
        for (Map.Entry<String, SkillTree> s : skillService.getSkillTrees().entrySet()) {
            toJson(s.getValue());
        }
        Sponge.getScheduler().createTaskBuilder().execute(this::cleanupTokenCache).async().delay(20, TimeUnit.MINUTES).submit(plugin);
    }

}
