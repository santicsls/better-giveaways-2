package me.kartonixon.bettergiveaways.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;
import me.kartonixon.bettergiveaways.BetterGiveaways;
import me.kartonixon.bettergiveaways.Giveaway;

public class SQLGetter {

    private BetterGiveaways plugin;
    public SQLGetter(BetterGiveaways plugin) {
        this.plugin = plugin;
    }

    public void createTableIfNotExists() {
        PreparedStatement ps;
        try {

            ps = plugin.SQL.getConnection().prepareStatement(
                "CREATE TABLE IF NOT EXISTS giveaways (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (id), uuid VARCHAR(255), expires VARCHAR(255), expired INT(255))");
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getGiveaways() {

        try {

            // Obtenemos todos los giveaways que no han expirado
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement(
                "SELECT * FROM giveaways WHERE expired = 0");

            // Ejecutamos la query y guardamos los resultados en la variable results
            ResultSet results = ps.executeQuery();
            results.next();

            // Si hay más de 2 resultados que no han expirado, entonces...
            if (results.getInt("expired") >= 2) {
                System.out.println("[BetterGiveaways] Fatal error. More than 2 giveaways at the same time.");
                Giveaway.currentlyGiveaway = true;
                return false;
            } else {
                
                // Si hay 1 resultado, entonces hay un giveaway activo
                if (results.getInt(1) == 1) {
                    System.out.println("[BetterGiveaways] A giveaway is active on the database.");
                    Giveaway.currentlyGiveaway = true;
                    Giveaway.playersInGiveaway = playersInGiveaway();
                    return false;
                } else {
                    // Si no hay resultados, entonces no hay ningún giveaway activo
                    System.out.println("[BetterGiveaways] A none giveaway is active on the database.");
                    Giveaway.currentlyGiveaway = false;
                    return true;
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        Giveaway.currentlyGiveaway = true;
        return false;

    } 

    public boolean createGiveaways() {

        try {

            // Obtenemos todos los giveaways que no han expirado
            if (getGiveaways()) {

                // Creamos un nuevo giveaway
                PreparedStatement ps = plugin.SQL.getConnection().prepareStatement(
                    "INSERT INTO giveaways (uuid, expires, expired) VALUES (?, ?, 1)");

                // Añadimos los valores a la query
                ps.setString(1, "none");
                ps.setString(2, "none");
                ps.setInt(3, 0);

                // Ejecutamos la query
                ps.executeUpdate();

                // Mostramos un mensaje en la consola
                System.out.println("[BetterGiveaways] Created a new giveaway on the database.");
                return true;
                
            } else {

                System.out.println("[BetterGiveaways] Can''t create a new giveaway on the database. There is already one active.");
                return false;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public ArrayList<Player> playersInGiveaway() {
        
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement(
                "SELECT * FROM giveaways WHERE expired = 0");
            ResultSet results = ps.executeQuery();
            results.next();
            String uuids = results.getString(2);
            ArrayList<Player> players = new ArrayList<Player>();
            if (uuids.equals("none")) {
                return players;
            } else {
                String[] uuidsArray = uuids.split(",");
                for (String uuid : uuidsArray) {
                    players.add(plugin.getServer().getPlayer(UUID.fromString(uuid)));
                }
                return players;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        
    }

    public boolean updateArrayList(ArrayList<Player> arraylist) {
        
        // Almacenamos el arraylist en la columna uuid
        try {
            PreparedStatement ps = plugin.SQL.getConnection().prepareStatement(
                "UPDATE giveaways SET uuid = ? WHERE expired = 0");
            ps.setString(1, arraylist.toString());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

}
