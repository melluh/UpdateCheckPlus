package tech.mistermel.updatecheckplus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;

public class UpdateCheckPlus extends JavaPlugin {

	private static UpdateCheckPlus instance;
	
	private JsonFileHandler configHandler;
	
	private Map<Plugin, UCPPlugin> plugins = new HashMap<>();
	
	private boolean allRequestsInitiated;
	private int loadedPluginCount;
	
	@Override
	public void onEnable() {
		instance = this;
		
		this.getCommand("updatecheck").setExecutor(new UpdateCheckCommand());
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		
		if(!this.getDataFolder().exists())
			this.getDataFolder().mkdir();
		
		File configFile = new File(this.getDataFolder(), "configuration.json");
		this.configHandler = new JsonFileHandler(configFile);
		configHandler.copyIfNotExists();
		configHandler.load();
		
		// Scheduler tasks only start executing when the server is fully loaded,
		// so loadPlugins will only get run after all plugins are loaded
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> loadPlugins((pluginCount) -> {
			this.getLogger().info("Finished loading plugins");
		}));
	}
	
	public void loadPlugins(Consumer<Integer> callback) {
		plugins.clear();
		this.loadedPluginCount = 0;
		this.allRequestsInitiated = false;

		PluginManager pm = this.getServer().getPluginManager();
		
		JSONObject idsObj = (JSONObject) configHandler.getJson().get("ids");
		for(Plugin plugin : pm.getPlugins()) {
			Long pluginId = (Long) idsObj.get(plugin.getName());
			if(pluginId == null) {
				this.getLogger().info(plugin.getName() + " has no resource ID in configuration.json");
				continue;
			}
			
			UCPPlugin ucpPlugin = new UCPPlugin(plugin, pluginId);
			plugins.put(plugin, ucpPlugin);
			
			ucpPlugin.retrieveLatestVersion((version) -> {
				loadedPluginCount++;
			});
		}
		
		this.allRequestsInitiated = true;
		callback.accept(loadedPluginCount);
	}
	
	public boolean isLoadingFinished() {
		return allRequestsInitiated && loadedPluginCount == plugins.size();
	}
	
	public JsonFileHandler getConfigHandler() {
		return configHandler;
	}
	
	public Map<Plugin, UCPPlugin> getPlugins() {
		return plugins;
	}
	
	public static UpdateCheckPlus instance() {
		return instance;
	}
	
}
