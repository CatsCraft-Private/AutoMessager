package message.brainsynder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import simple.brainsynder.files.FileMaker;
import simple.brainsynder.sound.SoundMaker;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Core extends JavaPlugin implements CommandExecutor {
    private FileMaker file = null;
    public void onEnable() {
        getCommand("autobroadcast").setExecutor(this);
        loadDefaults();
        run();
    }

    /**
     * Loads the default values for the Config.
     */
    private void loadDefaults () {
        file = new FileMaker(this, "config.yml");
        if (file.isSet("messages")) file.set("messages", Arrays.asList("&bMessage 1", "&dMessage 2", "&5Message 3", "&9Message 4"));
        if (file.isSet("Header")) file.set("Header", "none");
        if (file.isSet("Footer")) file.set("Footer", "none");
        if (file.isSet("sound")) file.set("sound", "none");
        if (file.isSet("interval")) file.set("interval", "1200");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (alias.equalsIgnoreCase("autobroadcast")) {
            if (!sender.hasPermission("broadcast.reload")) {
                sender.sendMessage("§cYou do not have permission for this command.");
            } else {
                loadDefaults();
                sender.sendMessage(ChatColor.GREEN + "Config file has been reloaded!");
            }
        }
        return false;
    }

    private void run() {
        final String sound = file.getString("sound");
        int interval = file.getInt("interval");
        final String header = file.getString("Header", true);
        final String footer = file.getString("Footer", true);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            List<String> messages = file.getStringList("messages");
            if (messages.isEmpty()) return;
            Random rand = new Random();
            int choice = rand.nextInt(messages.size());
            if ((header != null) && (!header.isEmpty()) && (!header.equalsIgnoreCase("none")))
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', header));

            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', messages.get(choice)));

            if ((footer != null) && (!footer.isEmpty()) && (!footer.equalsIgnoreCase("none")))
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', footer));

            if ((sound != null) && (!sound.isEmpty()) && (!sound.equalsIgnoreCase("none"))) {
                SoundMaker maker = SoundMaker.fromString(sound);
                Bukkit.getOnlinePlayers().forEach(maker::playSound);
            }
        }, 0L, (long) interval * 20);
    }
}
