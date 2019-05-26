package kvetinac97.Game;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import kvetinac97.Object.PlayerData;

public class GameListener implements Listener {

    //Data
    private Game game;

    public GameListener (Game game){
        this.game = game;
    }

    @EventHandler
    public void onPreLogin(PlayerPreLoginEvent e){
        Player player = e.getPlayer();

        //Začnutá hra
        if (game.getTask().getPhase() > GameSchedule.PHASE_WAITING){
            e.setCancelled();
            e.setKickMessage("§0[§7The §cMurderer§0] §4Hra již začala!");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        player.setGamemode(2);

        e.setJoinMessage("§0[§7The §cMurderer§0] §e" + player.getName() + " §bse pripojil §7(" +
                (game.getPlayers().size() + 1) + "/16)§b.");
        game.joinPlayer(player);
    }

    @EventHandler
    public void onMsg(PlayerChatEvent e){
        e.setCancelled();

        Player p = e.getPlayer();
        PlayerData pd = game.getPlayerData(p);

        String prefix = "";
        if (p.getName().equals("kvetinac97"))
            prefix = "§0[§4You§fTube§0] ";

        for (PlayerData playerData : game.getPlayers().values())
            playerData.getPlayer().sendMessage(prefix + "§e" + p.getName() + " §7> §f" + e.getMessage());
    }

    @EventHandler
    public void onSprint(PlayerFoodLevelChangeEvent e){
        e.setFoodLevel(20);
        e.setFoodSaturationLevel(100f);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if (game.getTask().getPhase() == GameSchedule.PHASE_WAITING && (e.getFrom().getX() !=
                e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()))
            e.setCancelled();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();

        if (game.getTask().getPhase() == GameSchedule.PHASE_WAITING)
            e.setQuitMessage("§0[§7The §cMurderer§0] §e" + player.getName() + " §bse odpojil §7(" +
                (game.getPlayers().size() - 1) + "/16)§b.");
        else
            e.setQuitMessage("");

        game.quitPlayer(player);
    }

    @EventHandler
    public void onAttack(EntityDamageEvent e){
        if (!(e.getEntity() instanceof Player))
            return;

        Player player = (Player) e.getEntity();
        PlayerData pd = game.getPlayerData(player);
        e.setCancelled();

        if (pd == null || game.getTask().getPhase() != GameSchedule.PHASE_GAME)
            return;

        if (e instanceof EntityDamageByEntityEvent){
            EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) e;
            if (!(ev.getDamager() instanceof Player))
                return;

            Player damager = (Player) ev.getDamager();
            PlayerData pl = game.getPlayerData(damager);

            if (pl == null)
                return;

            //Murder zabije hráče/detektiva
            if (pl.isMurderer() && damager.getInventory().getItemInHand().getId() == Item.IRON_SWORD)
                game.diePlayer(player, damager);

            //Hráč/detektiv zabije vraha
            if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE && !pl.isMurderer())
                game.diePlayer(player, damager);

            //Detektiv zabije hráče - trest mimo sebevraždy
            if (!pl.isMurderer() && !pd.isMurderer() && !player.getName().toLowerCase()
                    .equals(damager.getName().toLowerCase()))
                game.diePlayer(damager, damager);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        e.setCancelled();
    }

    @EventHandler
    public void onInventoryMove(InventoryTransactionEvent e){
        e.setCancelled();
    }

    @EventHandler
    public void onBowGoldPickup(InventoryPickupItemEvent e){
        if (!(e.getInventory().getHolder() instanceof Player))
            return;

        Player player = (Player) e.getInventory().getHolder();
        PlayerData pl = game.getPlayerData(player);

        if (pl == null) {
            e.setCancelled();
            e.getItem().close();
            return;
        }

        Item item = e.getItem().getItem();
        if (item.getId() == Item.BOW){
            e.setCancelled();

            if (pl.isMurderer() || player.getInventory().getItem(0).getId() == Item.BOW) //ignore
                return;

            e.getItem().close();

            game.messageAllPlayers("§bLuk detektiva byl nalezen!");
            for (PlayerData pd : game.getPlayers().values())
                pd.getPlayer().getInventory().clear(2, true);

            Item bow = Item.get(Item.BOW);
            bow.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_BOW_INFINITY));
            bow.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DURABILITY).setLevel(30));

            pl.setFakeDetective(true);
            player.getInventory().setItem(1, bow, true);
            player.getInventory().setItem(23, Item.get(Item.ARROW, 0, 1), true);
        }

        if (item.getId() == Item.GOLD_INGOT){
            e.setCancelled();
            e.getItem().close();

            if (pl.isMurderer())
                return;

            Item gold = player.getInventory().getItem(8);

            Item bow = Item.get(Item.BOW);
            bow.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_DURABILITY).setLevel(30));

            if (gold.getCount() == 9){
                player.getInventory().clear(8, true);

                if (player.getInventory().getItem(1).getId() == Item.BOW){
                    if (player.getInventory().getItem(23).getId() == Item.ARROW)
                        player.getInventory().setItem(23, Item.get(Item.ARROW, 0,
                            player.getInventory().getItem(23).getCount() + 1), true);
                    else
                        player.getInventory().setItem(23, Item.get(Item.ARROW, 0, 1), true);

                    return;
                }

                player.getInventory().clear(8, true);
                player.getInventory().setItem(1, bow, true);
                player.getInventory().setItem(23, Item.get(Item.ARROW), true);
                return;
            }

            if (gold.getId() != Item.GOLD_INGOT)
                player.getInventory().setItem(8, Item.get(Item.GOLD_INGOT, 0, 1), true);
            else
                player.getInventory().setItem(8, Item.get(Item.GOLD_INGOT, 0, gold.getCount() + 1), true);

        }

    }

}