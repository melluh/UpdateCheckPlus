package tech.mistermel.updatecheckplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

public class UpdateCheckCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 0 && args[0].equalsIgnoreCase("reload")) {
			UpdateCheckPlus.instance().getConfigHandler().load();
			UpdateCheckPlus.instance().loadPlugins((pluginCount) -> {
				sender.sendMessage(ChatColor.GREEN + "Reload completed");
			});
			
			return true;
		}
		
		if(!UpdateCheckPlus.instance().isLoadingFinished()) {
			sender.sendMessage(ChatColor.RED + "Loading has not finished, please try again later");
			return true;
		}
		
		List<Plugin> unknownPlugins = new ArrayList<>();
		List<UCPPlugin> outdatedPlugins = new ArrayList<>();
		List<UCPPlugin> updatedPlugins = new ArrayList<>();
		
		for(Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			UCPPlugin ucpPlugin = UpdateCheckPlus.instance().getPlugins().get(plugin);
			if(ucpPlugin == null) {
				unknownPlugins.add(plugin);
				continue;
			}
			
			if(ucpPlugin.isUpdated()) {
				updatedPlugins.add(ucpPlugin);
			} else {
				outdatedPlugins.add(ucpPlugin);
			}
		}
		
		if(outdatedPlugins.size() != 0) {
			sender.sendMessage(ChatColor.GRAY + ">> " + ChatColor.RED + ChatColor.BOLD.toString() + "OUTDATED" + ChatColor.GRAY + " <<");
			for(UCPPlugin plugin : outdatedPlugins) {
				sender.sendMessage(ChatColor.GRAY + "- " + plugin.getSpigotPlugin().getName() + ": Running version " + plugin.getCurrentVersion() + ", latest version is " + plugin.getCachedLatestVersion());
			}
		}

		if(unknownPlugins.size() != 0) {
			if(outdatedPlugins.size() != 0) {
				sender.sendMessage("");
			}
			
			sender.sendMessage(ChatColor.GRAY + ">> " + ChatColor.GRAY + ChatColor.BOLD.toString() + "UNKNOWN" + ChatColor.GRAY + " <<");
			for(Plugin plugin : unknownPlugins) {
				sender.sendMessage(ChatColor.GRAY + "- " + plugin.getName() + ": Not present in configuration.json");
			}
		}
		
		if(updatedPlugins.size() != 0) {
			if(unknownPlugins.size() != 0) {
				sender.sendMessage("");
			}
			
			sender.sendMessage(ChatColor.GRAY + ">> " + ChatColor.GREEN + ChatColor.BOLD.toString() + "UPDATED" + ChatColor.GRAY + " <<");
			for(UCPPlugin plugin : updatedPlugins) {
				sender.sendMessage(ChatColor.GRAY + "- " + plugin.getSpigotPlugin().getName() + ": Running version " + plugin.getCurrentVersion() + ", latest version is " + plugin.getCachedLatestVersion());
			}
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return Collections.emptyList();
	}

}
