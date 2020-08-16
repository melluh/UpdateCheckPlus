package tech.mistermel.updatecheckplus;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import tech.mistermel.updatecheckplus.util.VersioningUtil;
import tech.mistermel.updatecheckplus.util.VersioningUtil.ComparisonResult;

public class UCPPlugin {
	
	private static final String RESOURCE_URL = "https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=%%ID%%";
	
	private Plugin spigotPlugin;
	private long resourceId;
	
	private String version;
	
	public UCPPlugin(Plugin spigotPlugin, long resourceId) {
		this.spigotPlugin = spigotPlugin;
		this.resourceId = resourceId;
	}
	
	public String getCachedLatestVersion() {
		return version;
	}
	
	public void retrieveLatestVersion(Consumer<String> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(UpdateCheckPlus.instance(), () -> {
			try {
				URL url = new URL(RESOURCE_URL.replace("%%ID%%", Long.toString(resourceId)));
				JSONObject json = (JSONObject) new JSONParser().parse(new InputStreamReader(url.openStream()));
				
				this.version = (String) json.get("current_version");
				consumer.accept(version);
			} catch (IOException e) {
				UpdateCheckPlus.instance().getLogger().log(Level.SEVERE, "Exception occurred while attempting get resource from Spigot API", e);
			} catch (ParseException e) {
				UpdateCheckPlus.instance().getLogger().log(Level.SEVERE, "Exception ocurred while attempting to parse Spigot API response", e);
			}
		});
	}
	
	public boolean isUpdated() {
		if(this.getCurrentVersion().equals(version)) {
			return true;
		}
		
		ComparisonResult result = VersioningUtil.compareSemantic(this.getCurrentVersion(), version);
		if(result != ComparisonResult.INCOMPATIBLE) {
			return result == ComparisonResult.LATEST || result == ComparisonResult.NEWER;
		}
		
		return false;
	}
	
	public String getCurrentVersion() {
		return spigotPlugin.getDescription().getVersion();
	}
	
	public Plugin getSpigotPlugin() {
		return spigotPlugin;
	}
	
	public long getResourceId() {
		return resourceId;
	}
	
}
