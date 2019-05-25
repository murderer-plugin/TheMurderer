package kvetinac97.Object;

import cn.nukkit.Player;
import de.theamychan.scoreboard.network.*;

public class PlayerData {

    //Data
    private Player player;
    private boolean murderer = false;
    private boolean detective = false;
    private boolean fakeDetective = false;

    //Scoreboard
    private Scoreboard scoreboard = new Scoreboard();
    private ScoreboardDisplay display;

    private DisplayEntry eidAlive = null;
    private DisplayEntry eidTime = null;
    private DisplayEntry eidDetective = null;

    public PlayerData (Player p){
        this.player = p;
    }

    public Player getPlayer() {
        return player;
    }

    //Score

    public void showScoreboard(int playersAlive){
        display = scoreboard.addDisplay(DisplaySlot.SIDEBAR, "stats",
                "§7The §cMurderer", SortOrder.DESCENDING);

        display.addLine(" ", 6);
        display.addLine("Role: " + (isMurderer() ? "§cVrah" : (isDetective() ? "§1Detektiv" : "§aNevinny")), 5);
        display.addLine("  ", 4);
        eidAlive = display.addLine("§fNevinni: §e" + playersAlive, 3);
        eidTime = display.addLine("§fCas: §e09:59", 2);
        display.addLine("   ", 1);
        eidDetective = display.addLine("§fDetektiv: §aNazivu ", 0);

        scoreboard.showFor(player);
    }

    public void updateScoreboard(int playersAlive, String time, boolean detectiveAlive){
        display.removeEntry(eidAlive);
        display.removeEntry(eidDetective);
        display.removeEntry(eidTime);

        eidAlive = display.addLine("§fNevinni: §e" + playersAlive, 3);
        eidTime = display.addLine("§fCas: §e" + time, 2);
        eidDetective = display.addLine("§fDetektiv: " + (detectiveAlive ? "§aNazivu " : "§cMrtev "), 0);
    }

    public void removeScoreboard(){
        scoreboard.removeScoreEntry(eidAlive.getScoreId());
        scoreboard.removeScoreEntry(eidDetective.getScoreId());
        scoreboard.removeScoreEntry(eidTime.getScoreId());

        scoreboard.hideFor(player);
        scoreboard = null;
        display = null;

        eidAlive = null;
        eidDetective = null;
        eidTime = null;
    }

    //Score

    public void msg(String message){
        player.sendMessage("§0[§7The §cMurderer§0] §f" + message);
    }

    public boolean isMurderer() {
        return murderer;
    }
    public boolean isDetective() {
        return detective;
    }
    public boolean isFakeDetective() {
        return fakeDetective;
    }

    public void setMurderer() {
        this.murderer = true;
    }
    public void setDetective() {
        this.detective = true;
    }
    public void setFakeDetective(boolean fakeDetective) {
        this.fakeDetective = fakeDetective;
    }
}