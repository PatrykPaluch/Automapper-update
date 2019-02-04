package patrykp.teeworlds.automapper;

import java.util.List;
import java.util.ArrayList;

public class Tileset {
	String name;
	public int baseTile;
	List<Rule> rules;

	public Tileset(String name){
		this(name, 30);
	}
	public Tileset(String name, int rulesCount){
		this.name = name;
		rules = new ArrayList<Rule>(rulesCount);
	}

	public void addRule(Rule rule){
		rules.add(rule);
	}

	public void RemoveRule(Rule rule){
		rules.remove(rule);
	}

	//=========== GETTERS

	/**
	 * @return copy of rules array
	 */
	public Rule[] getRules(){
		return rules.toArray(new Rule[0]);
	}

	public String getName(){
		return name;
	}
	public int getBaseTile(){
		return baseTile;
	}
}