package kvetinac97;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import kvetinac97.Game.Game;
import kvetinac97.Object.HumanNPC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MinigameBase extends PluginBase {

    //Pozice pro spawn goldů a hráčů
    public static HashMap<String, ArrayList<Position>> goldPlayerPos = new HashMap<>();

    @Override
    public void onEnable() {
        //Murder levels
        getServer().loadLevel("archives");
        getServer().loadLevel("headquarters");
        getServer().loadLevel("library");
        getServer().loadLevel("towerfall");
        getServer().loadLevel("transport");

        Entity.registerEntity("HumanNPC", HumanNPC.class);

        //archives
        Level archives = getServer().getLevelByName("archives");
        goldPlayerPos.put("archives", new ArrayList<>(Arrays.asList(
                new Position(-53, 75, 8, archives),
                new Position(-40, 75, 1, archives),
                new Position(-58, 75, -9, archives),
                new Position(-60, 64, 0, archives),
                new Position(-55, 64, -20, archives),
                new Position(-33, 65, -20, archives),
                new Position(-17, 65, -35, archives),
                new Position(-1, 65, -21, archives),
                new Position(9, 65, 6, archives),
                new Position(1, 65, 22, archives),
                new Position(-13, 65, 36, archives),
                new Position(-29, 65, 29, archives),
                new Position(-41, 64, 19, archives),
                new Position(-54, 64, 21, archives),
                new Position(-30, 75, 1, archives),
                new Position(-8, 65, -11, archives)
        )));

        //headquarters
        Level headquarters = getServer().getLevelByName("headquarters");
        goldPlayerPos.put("headquarters", new ArrayList<>(Arrays.asList(
                new Position(18, 66, 23, headquarters),
                new Position(0, 66, 23, headquarters),
                new Position(-15, 66, 20, headquarters),
                new Position(-15, 66, 1, headquarters),
                new Position(-9, 66, -15, headquarters),
                new Position(8, 63, -34, headquarters),
                new Position(-7, 63, -37, headquarters),
                new Position(8, 64, 0, headquarters),
                new Position(0, 64, 7, headquarters),
                new Position(-7, 64, 1, headquarters),
                new Position(0, 64, -7, headquarters),
                new Position(12, 76, -14, headquarters),
                new Position(16, 76, 5, headquarters),
                new Position(4, 76, 20, headquarters),
                new Position(-14, 76, 23, headquarters),
                new Position(-14, 76, 8, headquarters)
        )));

        //library
        Level library = getServer().getLevelByName("library");
        goldPlayerPos.put("library", new ArrayList<>(Arrays.asList(
                new Position(-21, 65, 1, library),
                new Position(-15, 65, 30, library),
                new Position(-8, 65, 30, library),
                new Position(17, 66, 34, library),
                new Position(26, 65, 22, library),
                new Position(37, 65, 19, library),
                new Position(38, 68, 0, library),
                new Position(16, 65, 2, library),
                new Position(16, 74, 23, library),
                new Position(11, 74, 29, library),
                new Position(6, 74, 23, library),
                new Position(3, 71, 13, library),
                new Position(-2, 71, -3, library),
                new Position(8, 65, -11, library),
                new Position(3, 65, 1, library),
                new Position(-8, 65, 1, library)
        )));

        //towerfall
        Level towerfall = getServer().getLevelByName("towerfall");
        goldPlayerPos.put("towerfall", new ArrayList<>(Arrays.asList(
                new Position(7, 141, -19, towerfall),
                new Position(18, 141, -13, towerfall),
                new Position(23, 141, -5, towerfall),
                new Position(16, 142, 6, towerfall),
                new Position(-2, 142, 17, towerfall),
                new Position(0, 141, -23, towerfall),
                new Position(-8, 141, -7, towerfall),
                new Position(3, 151, 16, towerfall),
                new Position(-19, 151, 0, towerfall),
                new Position(-18, 161, 0, towerfall),
                new Position(-1, 162, 0, towerfall),
                new Position(10, 162, 8, towerfall),
                new Position(10, 162, -7, towerfall),
                new Position(25, 162, 0, towerfall),
                new Position(27, 151, -17, towerfall),
                new Position(11, 151, -21, towerfall)
        )));

        //transport
        Level transport = getServer().getLevelByName("transport");
        goldPlayerPos.put("transport", new ArrayList<>(Arrays.asList(
                new Position(-19, 65, 62, transport),
                new Position(-29, 65, 52, transport),
                new Position(-29, 65, 42, transport),
                new Position(-40, 65, 50, transport),
                new Position(-8, 65, 56, transport),
                new Position(0, 65, 58, transport),
                new Position(16, 65, 48, transport),
                new Position(28, 64, 37, transport),
                new Position(37, 65, 9, transport),
                new Position(18, 64, 0, transport),
                new Position(16, 62, -12, transport),
                new Position(3, 62, -20, transport),
                new Position(-9, 62, -22, transport),
                new Position(-6, 69, -9, transport),
                new Position(-21, 69, -17, transport),
                new Position(-21, 69, -9, transport)
        )));

        //Finish init
        new Game(this);
        getLogger().info("Enabled!");
    }

}