package patrykp.teeworlds.automapper;

public class Condition {

	public static final int EMPTY = 0;
	public static final int FULL = -1;
	
	public final int x;
	public final int y;
	public final int value;

	public Condition(int x, int y, String value){
		this(x,y, stringToValue(value));
	}

	public Condition(int x, int y, int value){
		this.x = x;
		this.y = y;
		this.value = value;
	}


	@Override
	public String toString(){
		return String.format("%d %d %s", x, y, getValueString());
	}

	@Override
	public int hashCode() {
		int val = x;
		val += val*31 + y;
		val += val*31 + value;
		return val;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(obj==this) return true;
		if(obj.getClass().equals(this.getClass())){
			Condition other = (Condition)obj;
			return
				other.x == this.x &&
				other.y == this.y &&
				other.value == this.value;
		}
		return false;
	}

	public String getValueString() {
		return valueToString(this.value);
	}
	public static String valueToString(int value) {
		switch(value) {
			case EMPTY: return "empty";
			case FULL: return "full";
		}
		return Integer.toString(value);
	}
	
	public static int stringToValue(String v) {
		switch(v.toLowerCase()) {
			case "empty": return EMPTY;
			case "full": return FULL;
		}
		return Integer.parseInt(v);
	}
}