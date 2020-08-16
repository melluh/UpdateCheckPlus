package tech.mistermel.updatecheckplus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(!p.hasPermission("updatecheckplus.notifyjoin")) {
			return;
		}
		
		/* Run later to make sure the player gets the message */
		Bukkit.getScheduler().scheduleSyncDelayedTask(UpdateCheckPlus.instance(), () -> {
			int outdatedCount = 0;
			for(UCPPlugin ucpPlugin : UpdateCheckPlus.instance().getPlugins().values()) {
				if(!ucpPlugin.isUpdated()) {
					outdatedCount++;
				}
			}
			
			if(outdatedCount == 0)
				return;
			
			p.sendMessage(ChatColor.YELLOW + Integer.toString(outdatedCount) + ChatColor.RED + (outdatedCount == 1 ? " plugin is" : " plugins are") + " outdated! Use /updatecheck for a complete overview.");
		}, 20);
	}
	
}
