package me.kartonixon.bettergiveaways;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.kartonixon.bettergiveaways.mysql.MySQL;
import me.kartonixon.bettergiveaways.mysql.SQLGetter;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class Giveaway implements CommandExecutor {
    
    private final BetterGiveaways plugin;

    public static boolean currentlyGiveaway;
    public static ArrayList<Player> playersInGiveaway;

    public MySQL SQL;
    public SQLGetter data;
    

    public Giveaway (BetterGiveaways plugin) {
        this.plugin = plugin;
        Giveaway.playersInGiveaway = new ArrayList<Player>();
        currentlyGiveaway = false;
        
        // MySQL connection
        this.SQL = new MySQL();
        this.data = new SQLGetter(this.plugin);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (label.equalsIgnoreCase("giveaway")) {

            if (args.length == 0) {
                
                // If the command is executed by the console:

                if (!(sender instanceof Player)) {

                    String prefix = plugin.getCustomConfig().getString("chat-prefix");

                    for (String message : plugin.getCustomConfig().getStringList("giveaway-help")) {
                        
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                    }

                } else {

                    Player player = (Player) sender;

                    // If currently there is no giveaway running

                    if (!currentlyGiveaway) {

                        String prefix = plugin.getCustomConfig().getString("chat-prefix");

                        for (String message : plugin.getCustomConfig().getStringList("giveaway-not-found")) {

                            // Send message to ONLY ADMIN with the permission "bettergiveaways.manage"

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    
                        }

                    } else {

                        // If giveaway is running and the player is not in the giveaway

                        if (!playersInGiveaway.contains(player)) {

                            playersInGiveaway.add(player);

                            if (BetterGiveaways.mysql) {

                                this.data.updateArrayList(playersInGiveaway);

                            }

                            String prefix = plugin.getCustomConfig().getString("chat-prefix");

                            for (String message : plugin.getCustomConfig().getStringList("giveaway-joined")) {

                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                            }

                            int mod = plugin.getCustomConfig().getInt("every-x-joined");

                            if (playersInGiveaway.size() % mod == 0) {

                                for (String message : plugin.getCustomConfig().getStringList("giveaway-on-player-join")) {

                                    String formattedMessage = message.replace("{count}", Integer.toString(playersInGiveaway.size()));
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + formattedMessage));

                                }

                            }

                        } else {

                            String prefix = plugin.getCustomConfig().getString("chat-prefix");

                            for (String message : plugin.getCustomConfig().getStringList("giveaway-already-in")) {

                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                            }

                        }

                    }

                }

                return true;

            }

            if (sender.hasPermission("bettergiveaways.manage")) {

                // "/giveaway start" command - Starts the giveaway

                if (args[0].equalsIgnoreCase("start")) {

                    // If admin tries to start a giveaway while one is already running

                    if (currentlyGiveaway == true) {

                        String prefix = plugin.getCustomConfig().getString("chat-prefix");

                        for (String message : plugin.getCustomConfig().getStringList("giveaway-end-first")) {

                            // Send message to ONLY ADMIN with the permission "bettergiveaways.manage"

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
    
                        }

                        return true;

                    }

                    String prefix = plugin.getCustomConfig().getString("chat-prefix");

                    // Start the giveaway

                    for (String message : plugin.getCustomConfig().getStringList("giveaway-start")) {

                        // Send message to EVERYONE on the server

                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                    }

                    // Obtenemos de la config discord-webhook.enabled y si es true, enviamos el mensaje al webhook 

                    if (plugin.getCustomConfig().getBoolean("discord-webhook.enabled")) {

                        String webhookURL = plugin.getCustomConfig().getString("discord-webhook.url");
                        String embedTitle = plugin.getCustomConfig().getString("discord-webhook.embed.title");
                        String embedContent = plugin.getCustomConfig().getString("discord-webhook.embed.content");
                        String embedImage = plugin.getCustomConfig().getString("discord-webhook.embed.image");
                        //DiscordWebhookMessage.sendWebhook(webhookURL, embedTitle, embedContent, embedImage);

                    }

                    // Si en database.yml está activado el uso de MySQL, se añade un registro a la tabla giveaways

                    if (BetterGiveaways.mysql) {

                        if (this.data.createGiveaways()) {
                            sender.sendMessage("Giveaway created in database!");
                        } else {
                            sender.sendMessage("Error creating giveaway in database.");
                        }

                    }

                    currentlyGiveaway = true;
                    return true;

                }

                // "/giveaway end" command - Ends the giveaway

                if (args[0].equalsIgnoreCase("end")) {

                    if (!currentlyGiveaway) {


                        String prefix = plugin.getCustomConfig().getString("chat-prefix");

                        for (String message : plugin.getCustomConfig().getStringList("giveaway-not-found")) {

                            // Send message to ONLY ADMIN with the permission "bettergiveaways.manage"

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                        }
                        return true;

                    }

                    if (playersInGiveaway.size() == 0) {

                        String prefix = plugin.getCustomConfig().getString("chat-prefix");

                        for (String message : plugin.getCustomConfig().getStringList("giveaway-end-empty")) {

                            // Send message to EVERYONE on the server, because there are no players in the giveaway

                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                        }

                        currentlyGiveaway = false;
                        return true;

                    }

                    Random r = new Random();
                    int randomIndex = r.nextInt(playersInGiveaway.size());
                    String winner = playersInGiveaway.get(randomIndex).getName();

                    int playerCount = playersInGiveaway.size();

                    String prefix = plugin.getCustomConfig().getString("chat-prefix");

                    for (String message : plugin.getCustomConfig().getStringList("giveaway-end-winner")) {

                        // Send message to EVERYONE on the server, with the winner and the amount of players in the giveaway

                        String formattedMessage = message.replace("{winner}", winner).replace("{count}", Integer.toString(playerCount));
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + formattedMessage));

                    }

                    currentlyGiveaway = false;
                    playersInGiveaway.clear();

                    return true;

                }

                // "/giveaway list" command - Lists the players in the giveaway

                if (args[0].equalsIgnoreCase("list")) {

                    if (!currentlyGiveaway) {

                        String prefix = plugin.getCustomConfig().getString("chat-prefix");

                        for (String message : plugin.getCustomConfig().getStringList("giveaway-start-first")) {

                            // Send message to ONLY ADMIN with the permission "bettergiveaways.manage"

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                        }

                        return true;

                    }

                    ArrayList<String> nicknames = new ArrayList<String>();

                    for (Player player : playersInGiveaway) {
                        nicknames.add(player.getName());
                    }

                    // Make Customizable message for this, and placeholder for the list of players

                    sender.sendMessage(ChatColor.GREEN + nicknames.toString());

                }

                // "/giveaway reload" command - Reloads the config

                if (args[0].equalsIgnoreCase("reload")) {
                        
                        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
                        Bukkit.getServer().getPluginManager().enablePlugin(plugin);
    
                        String prefix = plugin.getCustomConfig().getString("chat-prefix");

                        for (String message : plugin.getCustomConfig().getStringList("giveaway-reload")) {
                            
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                        }
                        return true;
    
                }

                // "/giveaway help" command - Shows the help message

                if (args[0].equalsIgnoreCase("help")) {
                        
                    String prefix = plugin.getCustomConfig().getString("chat-prefix");

                    for (String message : plugin.getCustomConfig().getStringList("giveaway-help")) {
                        
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                    }
                    
                    return true;

                }

                // "/giveaway debug" command - Shows the debug message

                if (args[0].equalsIgnoreCase("debug")) {
                        
                    String prefix = plugin.getCustomConfig().getString("chat-prefix");

                    for (String message : plugin.getCustomConfig().getStringList("giveaway-debug")) {
                        
                        //this.data.getGiveaways();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                    }
                    
                    return true;

                }

            } else {

                // If the player doesn't have permission to use the command

                String prefix = plugin.getCustomConfig().getString("chat-prefix");

                for (String message : plugin.getCustomConfig().getStringList("giveaway-no-permission")) {
                            
                    Player player = (Player) sender;

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                }
                return false;

            }
            
        }
        
        return false;
        
    }
}
