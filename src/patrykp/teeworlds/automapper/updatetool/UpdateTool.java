package patrykp.teeworlds.automapper.updatetool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.*;

import patrykp.teeworlds.automapper.Condition;
import patrykp.teeworlds.automapper.Rule;
import patrykp.teeworlds.automapper.Tileset;

public class UpdateTool {

	public static final String[] locations = {
		"./",
		"./data/editor/",
		"./data/editor/automap/"
	};

	public UpdateTool(){}


	public void UpdateFile(String[] files){
		for(int i = 0 ; i < files.length ; i++){
			UpdateFile(files[i]);
		}
	}

	public void UpdateFile(String file){
		File fileToUpdate = findFile(file);
		if(fileToUpdate==null) {
			System.out.printf("File %s not found!\n", file);
			return;
		}
		System.out.printf("Found: %s\n",fileToUpdate.toString());
		//Check name for new file
		String newName=fileToUpdate.getName();
		if(newName.endsWith(".rules")) {
			newName = newName.substring(0, newName.length()-6); //remove ".rule"
		}
		newName += ".json";
		File saveFile = new File(fileToUpdate.getParentFile(), newName);
		if(saveFile.exists()) {
			System.out.printf("Can't create '%s'. File arleady exists\n", saveFile.toString());
			return;
		}
		
		//read .rules file
		ArrayList<Tileset> readedTilesets = readFile(fileToUpdate);
		if(readedTilesets==null) {
			System.err.println("File read error");
			return;
		}
		//create json
		JSONObject jsonTilesets = createJSon(readedTilesets);
		if(jsonTilesets==null) {
			System.err.println("JSON prassing error");
			return;
		}
		
		//save json
		if( saveNewFile(saveFile, jsonTilesets) ) {
			System.out.printf("Success! File saved as %s\n", saveFile.toString());
		}else {
			System.out.printf("Error with saving %s\n", saveFile.toString());
		}
		
	}
	
	File findFile(String file) {
		for(int i = 0 ; i < locations.length ; i++) {
			File f = new File(locations[i], file);
			if(f.exists() && f.isFile()) {
				return f;
			}
		}
		return null;
	}
	
	ArrayList<Tileset> readFile(File f){ //this can be done better
		
		ArrayList<Tileset> tilesets = new ArrayList<Tileset>();
		
		int lineNumber = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			//simple read line by line
			Tileset currTileset = null;
			Rule currRule = null;
			
			String line;
			while( (line=br.readLine()) != null) {
				++lineNumber; //for error info
				if(line.isEmpty() || line.startsWith("#")) continue;
				if(line.startsWith("[") && line.endsWith("]")) {
					String tilesetName = line.substring(1, line.length()-1);
					
					//add prev Tileset
					if(currTileset!=null) tilesets.add(currTileset);
					
					currTileset = new Tileset(tilesetName);
					currRule = null; //clear rule
					continue;
				}
				if( line.startsWith("Index") ) {
					String[] args = line.split(" ");
					int tileId = Integer.parseInt(args[1]);
					if(currTileset.baseTile==0) {
						currTileset.baseTile = tileId;
						continue;
					}
					if(currRule!=null) currTileset.addRule(currRule);
					currRule = new Rule(tileId);
					
					for(int i = 2 ; i < args.length ; i++) {//[0]=="Index"; [1]==tileId
						if(args[i].equals("XFLIP")) currRule.hFlip = true;
						else if(args[i].equals("YFLIP")) currRule.vFlip = true;
					}
					continue;
				}
				
				if( line.startsWith("Pos") ) {
					//i: 0   1 2 3     4
					//s: Pos 0 0 Index 1
					//s: Pos 0 0 EMPTY
					String[] args = line.split(" ");
					int x = Integer.parseInt(args[1]);
					int y = Integer.parseInt(args[2]);
					if(args.length==4) { //Pos 0 0 EMPTY
						currRule.addCondition(new Condition(x, y, args[3]) );
					}else {//Pos 0 0 Index 1
						currRule.addCondition(new Condition(x, y, args[4]) );
					}
					
					continue;
				}
				
				if( line.startsWith("Random") ) {
					currRule.random = Integer.parseInt(line.substring(7)); //"Random ".length();
					continue;
				}
				
			}
			if(currTileset!=null) tilesets.add(currTileset);
			br.close();
		}catch(IOException er) {
			System.err.printf("IOException: %s\n", er.getMessage() );
		}catch(NullPointerException er) {
			System.err.printf("[line: %d] NPE: %s\n", lineNumber, er.getMessage());
		}
		
		
		return tilesets;
	}
	
	JSONObject createJSon(ArrayList<Tileset> input) {
		JSONObject root = new JSONObject();
		JSONArray jsonTilesets = new JSONArray();
		for(Tileset tileset : input) {
			JSONObject jsonTilesetroot = new JSONObject();
			JSONObject jsonTileset = new JSONObject();
			jsonTileset.put("basetile", tileset.baseTile);
			
			JSONArray jsonRules = new JSONArray();
			
			for(Rule rule : tileset.getRules()) {
				JSONObject jsonRule = new JSONObject();
				jsonRule.put("index", rule.getTileIndex());
				if(rule.hFlip) jsonRule.put("hflip", 1);//TODO move check before file processing
				if(rule.vFlip) jsonRule.put("vflip", 1);
				if(rule.rotation != Rule.Rotation.R0) jsonRule.put("rotate", rule.rotation.getValue());
				if(rule.random >= 0) jsonRule.put("random", rule.random);
				
				JSONArray jsonConditions = new JSONArray();
				for(Condition condition : rule.getConditions()) {
					JSONObject jsonCondition = new JSONObject();
					jsonCondition.put("x", condition.x);
					jsonCondition.put("y", condition.y);
					jsonCondition.put("value", condition.value);
					jsonConditions.put(jsonCondition);
				}
				
				jsonRule.put("condition", jsonConditions);
				jsonRules.put(jsonRule);
			}
			
			jsonTileset.put("rules", jsonRules);
			
			jsonTilesetroot.put(tileset.getName(), jsonTileset);
			jsonTilesets.put(jsonTilesetroot);
		}
		root.put("tileset", jsonTilesets);
		
		return root;
	}
	
	boolean saveNewFile(File f, JSONObject json) {
		if(f.exists()) return false;
		try {
			FileWriter fw = new FileWriter(f);
			json.write(fw);
			fw.close();
		}catch(IOException er) {
			System.err.printf("Writting file error: %s\n", er.getMessage());
			return false;
		}
		return true;
	}
}









