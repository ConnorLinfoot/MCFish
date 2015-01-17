package com.connorlinfoot.mcfish;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;


public class MCFish extends JavaPlugin implements Listener {
    HashMap<Squid, ArmorStand> hashMap = new HashMap<Squid, ArmorStand>();

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
                for (Squid squid : hashMap.keySet()) {
                    ArmorStand armorStand = hashMap.get(squid);
                    if( squid.isDead() || armorStand.isDead() ) continue;
                    armorStand.teleport(squid);
                }
            }
        };

        bukkitRunnable.runTaskTimer(this, 1L, 1L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if( ! (sender instanceof Player ) ){
            return false;
        }

        Player player = (Player) sender;

        Entity entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
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

        Entity entity1 = player.getWorld().spawnEntity(player.getLocation(), EntityType.SQUID);
        Squid squid = (Squid) entity1;
        squid.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 72000, 72000, true));

        hashMap.put(squid, armorStand);

        return false;
    }

    public void onDisable() {
        getLogger().info(getDescription().getName() + " has been disabled!");
    }

}
