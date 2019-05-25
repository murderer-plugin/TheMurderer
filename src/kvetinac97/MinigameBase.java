package kvetinac97;

import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import kvetinac97.Game.Game;

import java.util.ArrayList;
import java.util.Arrays;

public class MinigameBase extends PluginBase {

    public static ArrayList<Position> goldPlayerPos = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Enabled!");

        Level level = getServer().getDefaultLevel();
        goldPlayerPos = new ArrayList<>(Arrays.asList(
                new Position(18, 66, 23, level),
                new Position(0, 66, 23, level),
                new Position(-15, 66, 20, level),
                new Position(-15, 66, 1, level),
                new Position(-9, 66, -15, level),
                new Position(8, 63, -34, level),
                new Position(-7, 63, -37, level),
                new Position(8, 64, 0, level),
                new Position(0, 64, 7, level),
                new Position(-7, 64, 1, level),
                new Position(0, 64, -7, level),
                new Position(12, 76, -14, level),
                new Position(16, 76, 5, level),
                new Position(4, 76, 20, level),
                new Position(-14, 76, 23, level),
                new Position(-14, 76, 8, level)
        ));

        new Game(this);
    }

}