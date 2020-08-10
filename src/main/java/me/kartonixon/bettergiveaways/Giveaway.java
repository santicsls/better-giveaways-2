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
                    return true;
                }
                
                if (!currentlyGiveaway) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getCustomConfig().getString("giveaway-not-found")));
                    return true;
                } else {
                    Player player = (Player) sender;
                    
                    if (!playersInGiveaway.contains(player)) {
                        playersInGiveaway.add(player);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getCustomConfig().getString("giveaway-joined")));

                        int mod = plugin.getCustomConfig().getInt("every-x-joined");
                        if (playersInGiveaway.size() % mod == 0) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                    plugin.getCustomConfig().getString("chat-prefix") + " " + playersInGiveaway.size() + " " + plugin.getCustomConfig().getString("giveaway-playercount")));
                        }
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getCustomConfig().getString("giveaway-already-in")));
                    }
                    
                    return true;
                }
                
            }

            if (sender.hasPermission("bettergiveaways.use")) {
                
                if (args[0].equalsIgnoreCase("start")) {

                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.getCustomConfig().getString("chat-prefix") + " " + plugin.getCustomConfig().getString("giveaway-start")));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.getCustomConfig().getString("chat-prefix") + " " + plugin.getCustomConfig().getString("giveaway-info")));

                    currentlyGiveaway = true;
                    return true;
                }
                
                if (args[0].equalsIgnoreCase("end")) {
                    //TODO: case when playersInGiveaway is empty
                    Random r = new Random();
                    int randomIndex = r.nextInt(playersInGiveaway.size());
                    String winner = playersInGiveaway.get(randomIndex).getName();

                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            plugin.getCustomConfig().getString("chat-prefix") + " " + plugin.getCustomConfig().getString("giveaway-end") + winner));

                    currentlyGiveaway = false;
                    playersInGiveaway.clear();
                    return true;
                }
                
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
            }
            
        }
        
        return false;
        
    }
}