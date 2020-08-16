package tech.mistermel.updatecheckplus;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonFileHandler {

	private File file;
	private JSONObject json;
	
	public JsonFileHandler(File file) {
		this.file = file;
	}
	
	public void copyIfNotExists() {
		if(file.exists())
			return;
		
		try(InputStream in = UpdateCheckPlus.instance().getResource(file.getName())) {
			Files.copy(in, file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load() {
		try {
			this.json = (JSONObject) new JSONParser().parse(new FileReader(file));
		} catch (IOException e) {
			UpdateCheckPlus.instance().getLogger().log(Level.SEVERE, "Exception occurred while attempting to load JSON file", e);
		} catch (ParseException e) {
			UpdateCheckPlus.instance().getLogger().log(Level.SEVERE, "Exception occurred while attempting to parse JSON file - is it malformed?", e);
		}
	}
	
	public JSONObject getJson() {
		if(json == null)
			throw new IllegalStateException("JSON has not been loaded yet");
		
		return json;
	}
	
}
