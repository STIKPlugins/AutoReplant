package me.stokmenn.autoreplant;

import me.stokmenn.autoreplant.commands.ReloadCommand;
import me.stokmenn.autoreplant.config.Config;
import me.stokmenn.autoreplant.listeners.AutoReplantListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoReplant extends JavaPlugin {

    @Override
    public void onEnable() {
        Config.init(this);

        getCommand("autoreplant").setExecutor(new ReloadCommand(this));
        Bukkit.getPluginManager().registerEvents(new AutoReplantListener(), this);
    }
}