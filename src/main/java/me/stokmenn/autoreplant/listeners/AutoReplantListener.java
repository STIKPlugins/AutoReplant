package me.stokmenn.autoreplant.listeners;

import me.stokmenn.autoreplant.config.Config;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

public class AutoReplantListener implements Listener {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    private final EnumMap<Material, BiConsumer<Block, Integer>> DROPPERS = new EnumMap<>(Material.class);

    public AutoReplantListener() {
        DROPPERS.put(Material.CARROTS, this::dropCarrots);
        DROPPERS.put(Material.POTATOES, this::dropPotatoes);
        DROPPERS.put(Material.WHEAT, this::dropWheat);
        DROPPERS.put(Material.BEETROOTS, this::dropBeetroots);
        DROPPERS.put(Material.NETHER_WART, this::dropNetherWart);
    }

    @EventHandler
    public void onPlantBreak(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block crop = event.getClickedBlock();

        if (crop == null) return;
        if (!Config.validCrops.contains(crop.getType())) return;
        if (!(crop.getBlockData() instanceof Ageable ageable)) return;
        if (ageable.getAge() != ageable.getMaximumAge()) return;

        Player player = event.getPlayer();
        if (!player.hasPermission(Config.permission)) return;
        if (Config.onlyInSurvival && player.getGameMode() != GameMode.SURVIVAL) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (Config.requireMaterial && !Config.materials.contains(item.getType())) return;
        if (Config.skipBoneMeal && item.getType() == Material.BONE_MEAL) return;

        ageable.setAge(0);
        crop.setBlockData(ageable);

        int fortune = !Config.considerFortune ? 0 : item.getEnchantmentLevel(Enchantment.FORTUNE);
        drop(crop.getType(), fortune, crop);
    }

    private void drop(Material type, int fortune, Block crop) {
        BiConsumer<Block, Integer> fn = DROPPERS.get(type);
        if (fn != null) fn.accept(crop, fortune);
    }

    private void dropCarrots(Block crop, int fortune) {
        crop.getWorld().dropItemNaturally(crop.getLocation(),
                new ItemStack(Material.CARROT, RANDOM.nextInt(1, 5 + fortune)));
    }

    private void dropPotatoes(Block crop, int fortune) {
        crop.getWorld().dropItemNaturally(crop.getLocation(),
                new ItemStack(Material.POTATO, RANDOM.nextInt(1, 5 + fortune)));
    }

    private void dropNetherWart(Block crop, int fortune) {
        crop.getWorld().dropItemNaturally(crop.getLocation(),
                new ItemStack(Material.NETHER_WART, RANDOM.nextInt(1, 4 + fortune)));
    }

    private void dropWheat(Block crop, int fortune) {
        World world = crop.getWorld();
        world.dropItemNaturally(crop.getLocation(), new ItemStack(Material.WHEAT, 1));
        world.dropItemNaturally(crop.getLocation(),
                new ItemStack(Material.WHEAT_SEEDS, RANDOM.nextInt(1, 3 + fortune)));
    }

    private void dropBeetroots(Block crop, int fortune) {
        World world = crop.getWorld();
        world.dropItemNaturally(crop.getLocation(), new ItemStack(Material.BEETROOT, 1));
        world.dropItemNaturally(crop.getLocation(),
                new ItemStack(Material.BEETROOT_SEEDS, RANDOM.nextInt(1, 3 + fortune)));
    }
}