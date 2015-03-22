package com.connorlinfoot.mcfish;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.Map;


public class MCFish extends JavaPlugin implements Listener {
    public static HashMap<Squid, ArmorStand> hashMap = new HashMap<Squid, ArmorStand>();

    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        Server server = getServer();
        ConsoleCommandSender console = server.getConsoleSender();

        console.sendMessage("");
        console.sendMessage(ChatColor.BLUE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        console.sendMessage("");
        console.sendMessage(ChatColor.AQUA + getDescription().getName());
        console.sendMessage(ChatColor.AQUA + "Version " + getDescription().getVersion());
        console.sendMessage("");
        console.sendMessage(ChatColor.BLUE + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        console.sendMessage("");

        Bukkit.getPluginManager().registerEvents(this, this);

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if( hashMap.isEmpty() ) return;

                for (Map.Entry<Squid, ArmorStand> entry : hashMap.entrySet()) {
                    Squid squid = entry.getKey();
                    ArmorStand armorStand = entry.getValue();
                    if (squid.isDead() || armorStand.isDead()) {
                        //hashMap.remove(squid);
                        squid.remove();
                        armorStand.remove();
                        continue;
                    }
                    Location location = squid.getLocation();
                    location.setY(location.getBlockY() - 1);
                    armorStand.teleport(location);
                }
            }
        };

        bukkitRunnable.runTaskTimer(this, 1L, 1L);
    }

    //@EventHandler
    public void onSquidSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.SQUID) {
            spawnFish(entity.getLocation());
            entity.remove();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if( ! (sender instanceof Player ) ){
            return false;
        }

        Player player = (Player) sender;
        if (args.length != 1) {
            spawnFish(player.getLocation());
        } else {
            spawnFish(player.getLocation(), Integer.parseInt(args[0]));
        }

        return false;
    }

    public void onDisable() {
        getLogger().info(getDescription().getName() + " has been disabled!");
    }

    private static void spawnFish(Location location, Integer count) {
        for (int i = 0; i < count; i++) {
            spawnFish(location);
        }
    }

    private static void spawnFish(Location location) {
        Entity entity = location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        ArmorStand armorStand = (ArmorStand) entity;
        armorStand.setVisible(false);
        armorStand.setArms(true);
        ItemStack itemStack = new ItemStack(Material.RAW_FISH);
        armorStand.setItemInHand(itemStack);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setRemoveWhenFarAway(false);

        EulerAngle eulerAngle = new EulerAngle(30,0,0);
        armorStand.setRightArmPose(eulerAngle);
        armorStand.setLeftArmPose(eulerAngle);

        Entity entity1 = location.getWorld().spawnEntity(location, EntityType.SQUID);
        Squid squid = (Squid) entity1;
        squid.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 72000, 72000, true));

        hashMap.put(squid, armorStand);
    }

}
