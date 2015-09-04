package cz.neumimto.gui;

import cz.neumimto.NtRpgPlugin;
import cz.neumimto.ioc.Inject;
import cz.neumimto.ioc.PostProcess;
import cz.neumimto.ioc.Singleton;
import cz.neumimto.skills.SkillService;
import cz.neumimto.utils.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by ja on 1.9.2015.
 */
@Singleton
public class GuiService {

    @Inject
    private SkillService skillService;

    private Map<String, String> skillIconsUrls = new HashMap<>();

    @PostProcess(priority = 350)
    public void createStubSkillIcons() {
        Properties properties = new Properties();
        Path prop = Paths.get(NtRpgPlugin.workingDir + "/skillsicons.properties");
        FileUtils.createFileIfNotExists(prop);
        try {
            properties.load(new FileInputStream(prop.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path outputdir = FileUtils.createDirectoryIfNotExists(Paths.get(NtRpgPlugin.workingDir + "/icons/skills"));
        skillService.getSkills().values().stream().forEach(skill -> {
            if (!properties.containsKey(skill.getName())) {
                try {
                    BufferedImage img = createImageFromText(skill.getName());
                    File file = new File(outputdir + "/" + skill.getName() + ".png");
                    ImageIO.write(img, "png", file);
                    String uri = file.toPath().toUri().toString();
                    properties.put(skill.getName(), uri);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        skillIconsUrls = (Map) properties;
    }


    private BufferedImage createImageFromText(String text) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Font font = new Font("Arial", Font.BOLD, 12);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(Color.black);
        g.drawString(text, 2, (img.getHeight() + fm.getHeight()) / 2);
        g.dispose();
        return img;
    }

    public String getIconURI(String skill) {
        return skillIconsUrls.get(skill);
    }
}
