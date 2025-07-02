package me.stokmenn.autoreplant.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Config {
    private static JavaPlugin plugin;

    public static String permission;
    public static boolean onlyInSurvival;
    public static boolean skipBoneMeal;
    public static boolean considerFortune;

    public static Component usage;
    public static Component noPermissionToReload;
    public static Component configReloaded;

    public static final Set<Material> validCrops = new HashSet<>();

    public static boolean requireMaterial;
    public static final Set<Material> materials = new HashSet<>();

    public static void init(JavaPlugin plugin) {
        Config.plugin = plugin;
        plugin.saveDefaultConfig();
        reload();
    }

    public static void reload() {
        plugin.reloadConfig();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));

        permission = config.getString("permission", "autoReplant.use");
        onlyInSurvival = config.getBoolean("onlyInSurvival", true);
        skipBoneMeal = config.getBoolean("skipBoneMeal", true);
        considerFortune = config.getBoolean("considerFortune", true);

        usage = MiniMessage.miniMessage().deserialize(config.getString(
                "usage", "<red>✘ <white>Usage: /autoreplant reload"));

        noPermissionToReload = MiniMessage.miniMessage().deserialize(config.getString(
                "noPermissionToReload", "<red>✘ <white>You don't have permission to reload Config!"));

        configReloaded = MiniMessage.miniMessage().deserialize(config.getString(
                "configReloaded", "<green>✔ <white>Config reloaded!"));

        validCrops.clear();

        for (String cropName : config.getStringList("validCrops")) {
            Material material = Material.getMaterial(cropName);
            if (material == null) {
                material = Material.matchMaterial(cropName);
            }
            if (material == null) {
                plugin.getLogger().warning("Invalid crop name: " + cropName);
            } else {
                validCrops.add(material);
            }
        }

        requireMaterial = config.getBoolean("requireMaterial", false);
        materials.clear();
        for (String materialName : config.getStringList("items")) {
            Material material = Material.getMaterial(materialName);
            if (material == null) {
                material = Material.matchMaterial(materialName);
            }
            if (material == null) {
                plugin.getLogger().warning("Invalid crop name: " + materialName);
            } else {
                materials.add(material);
            }
        }
    }
}
