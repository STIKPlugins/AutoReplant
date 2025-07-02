package me.stokmenn.autoreplant.commands;

import me.stokmenn.autoreplant.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public ReloadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || !args[0].equals("reload")) {
            sender.sendMessage(Config.usage);
            return false;
        }
        if (!sender.hasPermission("autoReplant.reload")) {
            sender.sendMessage(Config.noPermissionToReload);
            return false;
        }
        Bukkit.getAsyncScheduler().runNow(plugin, task -> Config.reload());

        sender.sendMessage(Config.configReloaded);
        return true;
    }
}
