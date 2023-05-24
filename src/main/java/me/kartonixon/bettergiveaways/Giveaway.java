package me.kartonixon.bettergiveaways;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public class Giveaway implements CommandExecutor {
    
    private final BetterGiveaways plugin;

    private boolean currentlyGiveaway;
    private ArrayList<Player> playersInGiveaway;

    public Giveaway (BetterGiveaways plugin) {
        this.plugin = plugin;
        this.playersInGiveaway = new ArrayList<Player>();
        this.currentlyGiveaway = false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (label.equalsIgnoreCase("giveaway")) {

            if (args.length == 0) {
                
                if (!(sender instanceof Player)) {

                    sender.sendMessage("*console goes brrrr*");

                } else {

                    Player player = (Player) sender;

                    if (!currentlyGiveaway) {

                        for (String message : plugin.getCustomConfig().getStringList("giveaway-not-found")) {

                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

                        }

                    } else {

                        if (!playersInGiveaway.contains(player)) {

                            playersInGiveaway.add(player);

                            for (String message : plugin.getCustomConfig().getStringList("giveaway-joined")) {

                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

                            }

                            int mod = plugin.getCustomConfig().getInt("every-x-joined");

                            if (playersInGiveaway.size() % mod == 0) {

                                String prefix = plugin.getCustomConfig().getString("chat-prefix");

                                for (String message : plugin.getCustomConfig().getStringList("giveaway-on-player-join")) {

                                    String formattedMessage = message.replace("{count}", Integer.toString(playersInGiveaway.size()));
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + formattedMessage));

                                }

                            }

                        } else {

                            for (String message : plugin.getCustomConfig().getStringList("giveaway-already-in")) {

                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

                            }

                        }

                    }

                }

                return true;

            }

            if (sender.hasPermission("bettergiveaways.manage")) {

                if (args[0].equalsIgnoreCase("start")) {

                    if (currentlyGiveaway == true) {

                        sender.sendMessage(ChatColor.RED + "You have to end the giveaway first!");
                        return true;

                    }

                    String prefix = plugin.getCustomConfig().getString("chat-prefix");

                    for (String message : plugin.getCustomConfig().getStringList("giveaway-start")) {

                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                    }

                    currentlyGiveaway = true;
                    return true;

                }

                if (args[0].equalsIgnoreCase("end")) {

                    if (!currentlyGiveaway) {


                        String prefix = plugin.getCustomConfig().getString("chat-prefix");

                        for (String message : plugin.getCustomConfig().getStringList("giveaway-not-found")) {

                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                        }
                        return true;

                    }

                    if (playersInGiveaway.size() == 0) {

                        String prefix = plugin.getCustomConfig().getString("chat-prefix");

                        for (String message : plugin.getCustomConfig().getStringList("giveaway-end-empty")) {

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

                        String formattedMessage = message.replace("{winner}", winner).replace("{count}", Integer.toString(playerCount));
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + formattedMessage));

                    }

                    currentlyGiveaway = false;
                    playersInGiveaway.clear();

                    return true;

                }

                if (args[0].equalsIgnoreCase("list")) {

                    if (!currentlyGiveaway) {

                        String prefix = plugin.getCustomConfig().getString("chat-prefix");

                        for (String message : plugin.getCustomConfig().getStringList("giveaway-end-empty")) {

                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                        }
                        sender.sendMessage(ChatColor.RED + "You have to start the giveaway first!");
                        return true;

                    }

                    ArrayList<String> nicknames = new ArrayList<String>();

                    for (Player player : playersInGiveaway) {
                        nicknames.add(player.getName());
                    }

                    sender.sendMessage(ChatColor.GREEN + "Currently in the giveaway:");
                    sender.sendMessage(ChatColor.GREEN + nicknames.toString());

                }

                if (args[0].equalsIgnoreCase("reload")) {
                        
                        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
                        Bukkit.getServer().getPluginManager().enablePlugin(plugin);
    
                        String prefix = plugin.getCustomConfig().getString("chat-prefix");

                        for (String message : plugin.getCustomConfig().getStringList("giveaway-reload")) {

                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));

                        }
                        return true;
    
                }

            } else {

                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return false;

            }
            
        }
        
        return false;
        
    }
}
