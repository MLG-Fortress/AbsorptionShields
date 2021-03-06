package com.robomwm.absorptionshields;

import com.robomwm.absorptionshields.shield.Shield;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 6/30/2017.
 *
 * @author RoboMWM
 */
public class ConfigManager
{
    private Map<String, String> unformattedShieldNameConverter = new HashMap<>(); //idk
    private Map<String, Shield> shields = new HashMap<>();
    private Map<String, String> sounds = new HashMap<>();
    private ArmorSlot armorSlot;
    private FileConfiguration config;

    ConfigManager(JavaPlugin plugin)
    {
        config = plugin.getConfig();

        ConfigurationSection soundsSection = config.getConfigurationSection("Sounds");
        if (soundsSection == null)
        {
            soundsSection = config.createSection("Sounds");
            soundsSection.set("shieldHitSelf", "fortress.shieldhitself");
            soundsSection.set("lowShieldHitSelf", "fortress.lowshieldhitself");
            soundsSection.set("shieldOfflineAlert", "fortress.shieldoffline");
            soundsSection.set("shieldBroken", "fortress.shieldbroken");
            soundsSection.set("shieldBootingUp", "fortress.shieldbootingup");
            soundsSection.set("shieldFullyRecharged", "fortress.shieldrecharged");
        }
        for (String soundKey : soundsSection.getKeys(false))
        {
            try
            {
                sounds.put(soundKey, soundsSection.get(soundKey).toString());
            }
            catch (Exception ignored){}
        }

        ConfigurationSection shieldsSection = config.getConfigurationSection("Shields");
        if (shieldsSection == null)
        {
            shieldsSection = config.createSection("Shields");
            ConfigurationSection bumbleBee = shieldsSection.createSection("bumblebee");
            bumbleBee.set("strength", 6);
            bumbleBee.set("time", 2);
            bumbleBee.set("rate", 3);
            ConfigurationSection sponge = shieldsSection.createSection("sponge");
            sponge.set("strength", 24);
            sponge.set("time", 5);
            sponge.set("rate", 1);
            ConfigurationSection goldHelmet = shieldsSection.createSection("GOLDEN_HELMET");
            goldHelmet.set("strength", 18);
            goldHelmet.set("time", 3);
            goldHelmet.set("rate", 2);
        }
        for (String sectionName : shieldsSection.getKeys(false))
        {
            ConfigurationSection section = shieldsSection.getConfigurationSection(sectionName);

            //nullcheck
            if (section.get("strength") == null
                    || section.get("time") == null
                    || section.get("rate") == null)
                continue;

            float strength;
            long time;
            float rate;

            try
            {
                strength = (float)section.getDouble("strength");
                time = (long)section.getDouble("time");
                rate = (float)section.getDouble("rate");
            }
            catch (Exception e)
            {
                plugin.getLogger().warning("The \"" + sectionName + "\" shield is not configured correctly.");
                plugin.getLogger().warning(e.getMessage());
                continue;
            }

            sectionName = ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', sectionName);
            shields.put(sectionName, new Shield(ChatColor.stripColor(sectionName), sectionName, strength, time * 20L, rate));
            unformattedShieldNameConverter.put(ChatColor.stripColor(sectionName), sectionName);
        }

        config.addDefault("armorSlotToCheck", "HELMET");
        config.options().copyDefaults(true);

        try
        {
            armorSlot = ArmorSlot.valueOf(config.getString("armorSlotToCheck"));
        }
        catch (Throwable rock)
        {
            config.set("armorSlotToCheck", "HELMET");
            armorSlot = ArmorSlot.valueOf(config.getString("armorSlotToCheck"));
        }


        plugin.saveConfig();
    }

    public ItemStack getArmorItem(Player player)
    {
        switch (armorSlot)
        {
            case HELMET:
                return player.getInventory().getHelmet();
            case CHESTPLATE:
                return player.getInventory().getChestplate();
            case LEGGINGS:
                return player.getInventory().getLeggings();
            case BOOTS:
                return player.getInventory().getBoots();
        }
        return null;
    }

    public Set<String> getShieldNames()
    {
        return unformattedShieldNameConverter.keySet();
    }

    public boolean isValidShieldName(String name, boolean formatted)
    {
        if (name == null || name.isEmpty())
            return false;
        if (formatted)
            return shields.containsKey(name);
        return unformattedShieldNameConverter.containsKey(name);
    }


    public Shield createShield(String name, boolean formatted)
    {
        if (!isValidShieldName(name, formatted))
            return null;

        if (formatted)
            return new Shield(shields.get(name));

        return new Shield(shields.get(unformattedShieldNameConverter.get(name)));
    }

    public void playSound(Player player, String soundKey, boolean applyPitch)
    {
        float pitch = 1.0f;
        if (applyPitch)
            pitch = r4nd0m(0.8f, 1.2f);
        if (sounds.get(soundKey) == null)
            return;
        player.playSound(player.getLocation(), sounds.get(soundKey), 3000000f, pitch);
    }

    public void playSound(Location location, String soundKey, boolean applyPitch)
    {
        float pitch = 1.0f;
        if (applyPitch)
            pitch = r4nd0m(0.8f, 1.2f);
        if (sounds.get(soundKey) == null)
            return;
        location.getWorld().playSound(location, sounds.get(soundKey), SoundCategory.PLAYERS, 1.0f, pitch);
    }

    public float r4nd0m(float min, float max) {
        return (float) ThreadLocalRandom.current().nextDouble(min, max + 1.0D);
    }
}

enum ArmorSlot
{
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS
}
