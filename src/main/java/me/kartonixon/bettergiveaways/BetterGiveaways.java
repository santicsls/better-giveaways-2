package me.kartonixon.bettergiveaways;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import me.kartonixon.bettergiveaways.mysql.MySQL;
import me.kartonixon.bettergiveaways.mysql.SQLGetter;

import java.sql.*;
import java.io.File;
import java.io.IOException;

public final class BetterGiveaways extends JavaPlugin implements Listener {

    private File customConfigFile;
    private FileConfiguration customConfig;

    private File databaseConfigFile;
    private FileConfiguration databaseConfig;

    public MySQL SQL;
    public SQLGetter data;

    public static boolean mysql = false;

    @Override
    public void onEnable() {

        // Plugin startup logic
        createCustomConfig();
        createDatabaseConfig();
        
        this.getCommand("giveaway").setExecutor(new Giveaway(this));

        // MySQL connection
        this.SQL = new MySQL();
        this.data = new SQLGetter(this);

        if (customConfig.getBoolean("mysql.enabled")) {

            mysql = true;

            try {
                SQL.connect();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
    
            // Check if MySQL is connected
            if (SQL.isConnected()) {
    
                // If MySQL is connected, then...
                System.out.println("[BetterGiveaways] MySQL connected.");
    
                // Create table if not exists
                data.createTableIfNotExists();
    
                // Si los resultados de la tabla es igual a 1 (es decir, si hay algún resultado) entonces...
                data.getGiveaways();
    
                this.getServer().getPluginManager().registerEvents(this, this);
            } else {
                System.out.println("[BetterGiveaways] MySQL not connected. Using In-game database.");
            }
    
        } else {

            System.out.println("[BetterGiveaways] Using In-game database.");

        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration getDatabaseConfig() {
        return this.customConfig;
    }

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    private void createCustomConfig() {

        customConfigFile = new File(getDataFolder(), "custom.yml");

        if (!customConfigFile.exists()) {

            customConfigFile.getParentFile().mkdirs();
            saveResource("custom.yml", false);

        }

        customConfig= new YamlConfiguration();

        try {

            customConfig.load(customConfigFile);

        } catch (IOException | InvalidConfigurationException e) {

            e.printStackTrace();

        }

    }

    private void createDatabaseConfig() {

        databaseConfigFile = new File(getDataFolder(), "database.yml");

        if (!databaseConfigFile.exists()) {

            databaseConfigFile.getParentFile().mkdirs();
            saveResource("database.yml", false);

        }

        databaseConfig = new YamlConfiguration();

        try {

            databaseConfig.load(databaseConfigFile);

        } catch (IOException | InvalidConfigurationException e) {

            e.printStackTrace();

        }

    }

}