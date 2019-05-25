package kvetinac97.Game;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.network.protocol.SetSpawnPositionPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.potion.Effect;
import kvetinac97.MinigameBase;
import kvetinac97.Object.PlayerData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class Game {

    //Base & Task & Listener
    private PluginBase base;
    private GameSchedule task;

    //Players
    private HashMap<String, PlayerData> players = new HashMap<>();
    private ArrayList<Position> positionsToDistribute = new ArrayList<>();

    public Game (PluginBase base){
        this.task = new GameSchedule(this);
        this.base = base;

        base.getServer().getScheduler().scheduleRepeatingTask(task, 20);
        base.getServer().getPluginManager().registerEvents(new GameListener(this), base);

        positionsToDistribute.addAll(MinigameBase.goldPlayerPos);
        Collections.shuffle(positionsToDistribute);

        base.getLogger().info("Game registered!");
    }

    //Start hry
    public void gameStarted(){
        messageAllPlayers("§bHra zacala!");

        ArrayList<PlayerData> pls = new ArrayList<>(players.values());

        Random r = new Random();
        int i = r.nextInt(pls.size());

        PlayerData murderer = pls.get(i);
        pls.remove(i);

        murderer.msg("§bJsi §cVrah§b.\n" +
                "§bZabij vsechny hrace!");
        murderer.setMurderer();

        int ix = r.nextInt(pls.size());

        PlayerData detective = pls.get(ix);
        pls.remove(ix);

        Player player = detective.getPlayer();

        Item bow = Item.get(Item.BOW);
        bow.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_BOW_INFINITY));
        bow.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DURABILITY).setLevel(30));

        player.getInventory().setItem(1, bow, true);
        player.getInventory().setItem(23, Item.get(Item.ARROW), true);

        detective.msg("§bJsi §1Detektiv§b.\n" +
                "§bZachran nevinne pred vrahem!");
        detective.setDetective();

        pls.forEach((pl -> {
            pl.msg("§bJsi §aNevinny§b.\n" +
                 "§bSbirej goldy, ziskej luk a zabij vraha!");
        }));

        players.values().forEach(px -> px.showScoreboard(getLivingPlayerCount()));
    }

    //Konec hry
    public void resetGame(){
        positionsToDistribute.clear();
        positionsToDistribute.addAll(MinigameBase.goldPlayerPos);
        Collections.shuffle(positionsToDistribute);

        for (Player player : base.getServer().getOnlinePlayers().values()){
            player.setGamemode(2);
            messageAllPlayers("§e" + player.getName() + " §bse pripojil (" + (getPlayers().size() + 1) + "/16).");
            joinPlayer(player);
        }
    }

    //Připojení hráče
    public void joinPlayer(Player player){
        PlayerData pd = new PlayerData(player);

        base.getServer().getScheduler().scheduleDelayedTask(base, new Runnable() {
            @Override
            public void run() {
                player.teleport(positionsToDistribute.get(0));
                positionsToDistribute.remove(0);
            }
        }, 10);

        player.setNameTag("");
        players.put(player.getName().toLowerCase(), pd);
        pd.msg("Byl jsi pripojen do areny.");
    }

    //Odpojení hráče
    public void quitPlayer(Player player){
        PlayerData pd = getPlayerData(player);
        if (pd == null)
            return;

        //Pouhé odpojení na začátku nebo konci hry
        if (task.getPhase() != GameSchedule.PHASE_GAME){
            players.remove(player.getName().toLowerCase());
            return;
        }

        if (pd.isMurderer()){
            messageAllPlayers("§4Vrah byl zabit!\n" +
                    "§aNevinni §ba §1Detektiv §bvitezi!");
            endGame();
        }

        if (pd.isDetective() || pd.isFakeDetective()){
            if (pd.isDetective())
                messageAllPlayers("§4Detektiv byl zabit!");

            Item bow = Item.get(Item.BOW, 0, 1);
            bow.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_BOW_INFINITY));
            player.getLevel().dropItem(player, bow);

            SetSpawnPositionPacket packet = new SetSpawnPositionPacket();
            packet.x = (int) player.x;
            packet.y = (int) player.y;
            packet.z = (int) player.z;
            packet.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;

            for (PlayerData pl : players.values()){
                if (!pl.isMurderer())
                    pl.getPlayer().getInventory().setItem(2, Item.get(Item.COMPASS, 0, 1)
                            .setCustomName("§r§bLuk detektiva"), true);

                pl.getPlayer().dataPacket(packet);
            }
        }

        //poslední hráč
        if (getLivingPlayerCount() == 1) {
            messageAllPlayers("§bVsichni hraci byli zabiti!\n" +
                    "§4Vrah §bvyhrava hru.");
            endGame();
        }
    }

    //Hráč byl zabit
    public void diePlayer(Player player, Player killer){
        PlayerData pd = getPlayerData(player);
        if (pd == null)
            return;

        if (pd.isMurderer()){
            messageAllPlayers("§4Vrah byl zabit!\n" +
                   (killer == null ? "" : "§bHrdina: §e" + killer.getName() + "\n") +
                    "§aNevinni §ba §1Detektiv §bvitezi!");
            endGame();
        }

        if (pd.isDetective() || pd.isFakeDetective()){
            if (pd.isDetective())
                messageAllPlayers("§4Detektiv byl zabit!");

            Item bow = Item.get(Item.BOW, 0, 1);
            bow.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_BOW_INFINITY));
            player.getLevel().dropItem(player, bow);

            SetSpawnPositionPacket packet = new SetSpawnPositionPacket();
            packet.x = (int) player.x;
            packet.y = (int) player.y;
            packet.z = (int) player.z;
            packet.spawnType = SetSpawnPositionPacket.TYPE_WORLD_SPAWN;

            for (PlayerData pl : players.values()){
                if (!pl.isMurderer())
                    pl.getPlayer().getInventory().setItem(2, Item.get(Item.COMPASS, 0, 1)
                            .setCustomName("§r§bLuk detektiva"), true);

                pl.getPlayer().dataPacket(packet);
            }
        }

        base.getServer().getDefaultLevel().addSound(player, Sound.GAME_PLAYER_ATTACK_NODAMAGE);

        pd.msg("§cByl jsi zabit!");
        player.getInventory().clearAll();
        player.addEffect(Effect.getEffect(Effect.BLINDNESS).setAmbient(true).setVisible(false).setDuration(50));
        player.setGamemode(3);

        if (getLivingPlayerCount() == 0) {
            messageAllPlayers("§bVsichni hraci byli zabiti!\n" +
                    "§4Vrah §bvyhrava hru.");
            endGame();
        }
    }
    private void endGame(){
        task.setPhase(GameSchedule.PHASE_ENDING);

        players.values().forEach(px -> {
            px.removeScoreboard();
            px.getPlayer().getInventory().clearAll();
        });
        players.clear();

        for (Entity entity : base.getServer().getDefaultLevel().getEntities()){
            if (entity instanceof EntityItem) //odstranění goldů a luku
                entity.kill();
        }
    }

    //Zpráva všem hráčům
    public void messageAllPlayers(String message){
        for (PlayerData pd : players.values()){
            pd.msg("§b" + message);
        }
    }

    public void updateScoreboard(){
        boolean detectiveAlive = false;

        for (PlayerData pd : players.values())
            if (pd.isDetective() && pd.getPlayer().getGamemode() == 2) {
                detectiveAlive = true;
                break;
            }

        for (PlayerData pd : players.values())
            pd.updateScoreboard(getLivingPlayerCount(), task.getTime(), detectiveAlive);
    }

    //Getter
    public HashMap<String, PlayerData> getPlayers() {
        return players;
    }
    public GameSchedule getTask() {
        return task;
    }
    public PlayerData getPlayerData(Player player){
        return players.get(player.getName().toLowerCase());
    }

    public int getLivingPlayerCount(){
        int count = 0;

        for (PlayerData pd : players.values())
            if (pd.getPlayer().getGamemode() == 2 && !pd.isMurderer())
                count++;

        return count;
    }
    public PluginBase getBase() {
        return base;
    }
}