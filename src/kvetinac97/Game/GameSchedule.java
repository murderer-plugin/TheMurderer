package kvetinac97.Game;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.SetSpawnPositionPacket;
import cn.nukkit.scheduler.Task;
import kvetinac97.MinigameBase;
import kvetinac97.Object.PlayerData;

public class GameSchedule extends Task {

    //Const
    public static final int PHASE_WAITING = 0;
    public static final int PHASE_GAME = 1;
    public static final int PHASE_ENDING = 2;

    //Data
    private Game game;
    private int phase = PHASE_WAITING;
    private int time = 60;

    public GameSchedule(Game game){
        this.game = game;
    }

    @Override
    public void onRun(int i) {
        switch (phase){
            case PHASE_WAITING:
                if (game.getLivingPlayerCount() >= 2)
                    time--;
                else
                    game.getPlayers().forEach((name, pl) -> {
                        pl.getPlayer().sendPopup("§7Cekani na hrace...");
                    });

                if (time > 20 && game.getLivingPlayerCount() >= 4) //zrychlení hry
                    time = 20;

                game.getPlayers().forEach((name, pl) -> {
                    Player player = pl.getPlayer();
                    player.sendExperienceLevel(time);
                    player.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE).setValue(time / 60f));
                });

                if (time == 0){
                    game.gameStarted();

                    phase = PHASE_GAME;
                    time = 600;
                }
                break;
            case PHASE_GAME:
                game.updateScoreboard();
                time--;

                if (time > 585)
                    for (PlayerData pl : game.getPlayers().values())
                        if (pl.isMurderer())
                            pl.getPlayer().sendPopup("§7Svuj mec dostanes za " + (time - 585) + " s");
                        else
                            pl.getPlayer().sendPopup("§7Vrah dostane svuj mec za " + (time - 585) + " s");

                if (time == 585){
                    for (PlayerData pl : game.getPlayers().values())
                        if (pl.isMurderer()){
                            pl.getPlayer().getInventory().setItem(1, Item.get(Item.IRON_SWORD), true);
                            break;
                        }

                    game.messageAllPlayers("§bVrah dostal svuj mec!");
                }

                if (time % 30 == 0 && time != 600){
                    MinigameBase.goldPlayerPos.get(game.getMapName()).forEach((pos -> {
                        game.getBase().getServer().getDefaultLevel().dropItem(pos, Item.get(Item.GOLD_INGOT, 0, 1));
                    }));
                }

                //Kompas pro vraha
                if (game.getLivingPlayerCount() == 1){
                    SetSpawnPositionPacket packet = new SetSpawnPositionPacket();
                    for (PlayerData pl : game.getPlayers().values())
                        if (!pl.isMurderer() && pl.getPlayer().getGamemode() == 2){
                            Player p = pl.getPlayer();
                            packet.x = (int) p.x;
                            packet.y = (int) p.y;
                            packet.z = (int) p.z;
                            break;
                        }

                    packet.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;

                    for (PlayerData pl : game.getPlayers().values())
                        if (pl.isMurderer()) {
                            if (pl.getPlayer().getInventory().getItem(2).getId() != Item.COMPASS)
                                pl.getPlayer().getInventory().setItem(2, Item.get(Item.COMPASS, 0, 1)
                                        .setCustomName("§r§bPosledni hrac"), true);

                            pl.getPlayer().dataPacket(packet);
                            break;
                        }
                }

                if (time == 0){
                    phase = PHASE_ENDING;
                    time = 10;
                }
                break;
            case PHASE_ENDING:
                time--;

                game.getBase().getServer().getOnlinePlayers().values().forEach(player -> {
                    player.sendPopup("§7Hra konci za §e" + time + " §7sekund");
                    player.sendExperienceLevel(time);
                    player.setAttribute(Attribute.getAttribute(Attribute.EXPERIENCE).setValue(time / 10f));
                });

                if (time == 0){
                    game.resetGame();
                    phase = PHASE_WAITING;
                    time = 60;
                }
                break;
        }
    }

    //Getters
    public int getPhase(){
        return phase;
    }
    public void setPhase(int phase) {
        this.phase = phase;
        if (phase == PHASE_WAITING)
            time = 60;

        if (phase == PHASE_GAME)
            time = 600;

        if (phase == PHASE_ENDING)
            time = 10;
    }
    public String getTime() {
        String min = "";
        String sec = "";

        int minI = time / 60;
        int secI = time % 60;

        if (minI < 10)
            min = "0" + minI;
        else
            min = Integer.toString(minI);

        if (secI < 10)
            sec = "0" + secI;
        else
            sec = Integer.toString(secI);

        return min + ":" + sec;
    }
}