package patrykp.teeworlds.automapper;

import java.util.Set;
import java.util.HashSet;

public class Rule {

	private int index;
	public Rotation rotation = Rotation.R0;
	public boolean hFlip = false;
	public boolean vFlip = false;
	public int random = -1;
	Set<Condition> conditions;

	public Rule(int tileIndex){
		this(tileIndex, 4);
	}
	public Rule(int tileIndex, int conditionsCount){
		conditions = new HashSet<Condition>(conditionsCount);
		this.index = tileIndex;
	}

	public void setRotation(int r){
		setRotation(Rotation.formInt(r));
	}
	public void setRotation(Rotation r){
		rotation = r;
	}
	public void setHFlip(boolean hf){
		hFlip = hf;
	}
	public void setVFlip(boolean vf){
		vFlip = vf;
	}
	public void setRandom(int rv){
		random = rv;
	}
	public void removeRandom() {
		random = -1;
	}
	public void addCondition(Condition ... cond){
		for(int i = 0 ; i < cond.length ; i++){
			conditions.add(cond[i]);
		}
	}

	//shortcuts
	public void addCondition(int x, int y, String value){
		conditions.add(new Condition(x, y, value));
	}
	public void addCondition(int x, int y, int value){
		conditions.add(new Condition(x, y, value));
	}

	public void removeCondition(Condition cond){
		conditions.remove(cond);
	}

	//====== GETTERS
	public int getTileIndex(){
		return index;
	}

	/**
	 * @return copy of conditions array
	 */
	public Condition[] getConditions(){
		return conditions.toArray(new Condition[0]);
	}


	//============ ENUM

	public static enum Rotation {
		R0(0), R90(90), R180(180), R270(270);

		private final int rotation;

		private Rotation(int rotation){
			this.rotation = rotation;
		}

		public int getValue(){
			return rotation;
		}

		@Override
		public String toString(){
			return ""+rotation;
		}

		public static Rotation formInt(int r) throws IllegalArgumentException{
			switch(r){
				case 0: return R0;
				case 90: return R90;
				case 180: return R180;
				case 270: return R270;
			}
			throw new IllegalArgumentException("expected 0, 90, 180 or 270");
		}
	}

}