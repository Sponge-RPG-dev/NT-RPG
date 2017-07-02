package cz.neumito.rpg.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import cz.neumimto.core.ioc.Inject;
import cz.neumimto.core.ioc.PostProcess;
import cz.neumimto.core.ioc.Singleton;
import cz.neumimto.rpg.GroupService;
import cz.neumimto.rpg.NtRpgPlugin;
import cz.neumimto.rpg.players.CharacterService;
import cz.neumimto.rpg.players.ExtendedNClass;
import cz.neumimto.rpg.players.IActiveCharacter;
import cz.neumimto.rpg.players.groups.ConfigClass;
import cz.neumimto.rpg.skills.*;
import cz.neumito.rpg.rest.model.SkillDataRequestBean;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import spark.Spark;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    @Inject
    private GroupService groupService;

    private Thread t;

    @Inject
    NtRpgPlugin plugin;

    private static String index;

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
                ConfigClass configClass = primaryClass.getConfigClass();
                SkillTree skillTree = configClass.getSkillTree();
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
                if (skillData.getSkillName().equalsIgnoreCase(StartingPoint.name)) {
                    continue;
                }
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
        Spark.port(WebserverConfig.WEBSERVER_PORT);
        Spark.threadPool(WebserverConfig.WEBSERVER_THREADPOOL);
        try {
            index = new String(Files.readAllBytes(Paths.get(NtRpgPlugin.workingDir + "/index.html")), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Spark.after((request, response) -> {
            response.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
        });
        Spark.get("/", (request, response) -> {
            response.body(index);
            return response.body();
        });
        Spark.get("/getSkeleton/:player",(request, response) -> {
            String player = request.params(":player");
            CountDownLatch countDownLatch = new CountDownLatch(1);
            getSkeleton(player,s -> {
                response.body(s);
                countDownLatch.countDown();
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return response.body();
        });
        Spark.get("/createToken/:player", (request, response) -> {
            String player = request.params(":player");
            createToken(player,request.ip());
            return "{status:\"requested\"}";
        });
        Spark.get("/getSkills/:player", (request, response) -> {
            CountDownLatch latch = new CountDownLatch(1);
            String player = request.params(":player");
            getSkills(player, map -> {
                String json = toJson(map);
                response.body(json);
                latch.countDown();
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return response.body();
        });
        Spark.post("/getCharacter/:player",(request, response) -> {
            CountDownLatch l = new CountDownLatch(1);
            String player = request.params(":player");
            Token token = gson.fromJson(request.body(),Token.class);
            System.out.println(token);
            if (token.equals(tokens.get(player.toLowerCase()))) {
                getCharacterData(player,r -> {
                    response.body(gson.toJson(r));
                    l.countDown();
                });
            } else {
                l.countDown();
            }
            try {
                l.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return response.body();
        });
        Spark.post("/updateSkills/:player",(request,response) -> {
            CountDownLatch l = new CountDownLatch(1);
            String player = request.params(":player");

            return response.body();
        });
        Spark.get("/getTree/:class", (request, response) -> {
            String params = request.params(":class");
            CountDownLatch l = new CountDownLatch(1);
            ConfigClass configClass = groupService.getNClass(params);
            if (configClass == null) {
                //TODO
            } else {
                response.body(toJson(configClass.getSkillTree()));
            }
            return response.body();
        });
        Spark.post("/getSkillSetting",(request, response) -> {
            SkillDataRequestBean s = gson.fromJson(request.body(), SkillDataRequestBean.class);
            final ConfigClass configClass = groupService.getNClass(s.getClassname());
            final String skill = s.getSkill().toLowerCase();
            final CountDownLatch l = new CountDownLatch(1);

            Sponge.getScheduler().createTaskBuilder().execute(() ->{
                SkillData skillData = configClass.getSkillTree().getSkills().get(skill);
                //todo cache
                l.countDown();
            }).submit(plugin);
            try {
                l.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    private static final int TOKEN_LIFETIME = 7200000;

    private void cleanupTokenCache() {
        Iterator<Map.Entry<String, Token>> iterator = tokens.entrySet().iterator();
        long l = System.currentTimeMillis();
        while (iterator.hasNext()) {
            Map.Entry<String, Token> next = iterator.next();
            if (next.getValue().time + TOKEN_LIFETIME <= l ) {
                iterator.remove();
            }
        }
    }

    private void createToken(String player, String ip) {
        final Token token = new Token(getToken());
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            Optional<Player> player1 = Sponge.getGame().getServer().getPlayer(player);
            if (player1.isPresent()) {
                tokens.put(player1.get().getName().toLowerCase(),token);
                player1.get().sendMessage(Text.of(String.format("Requested token from IP ,%s ",ip)));
                player1.get().sendMessage(Text.of(String.format("Token: %s ",token)));
            }
        }).submit(plugin);
    }

    public void startServer() {
        System.out.println("Game thread :" + Thread.currentThread().getName());
        if (t == null || t.isInterrupted()) {
            t = new Thread(r);
            t.start();
            System.out.println("WebServer thread :" + t.getName());
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
