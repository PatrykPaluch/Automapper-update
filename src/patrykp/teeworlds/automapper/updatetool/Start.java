package patrykp.teeworlds.automapper.updatetool;

public class Start {

	public static void main(String [] args){
		if(checkInputAndHelp(args)){
			new UpdateTool().UpdateFile(args);
		}
	}


	public static boolean checkInputAndHelp(String [] args){
		if(args.length==0){
			showHelp();
			return false;
		}
		return true;
	}

	public static void showHelp(){
		System.out.println("Usage: updatetool rules...");
		System.out.println("");
		System.out.println("\trules\tFiles to update");
		System.out.println("");
		System.out.println("");
		System.out.println("Updates old .rules file (tw 0.6) to new .json files (tw 0.7)");
		System.out.println("File search locations:");
		for(int i = 0 ; i < UpdateTool.locations.length ; i++)
			System.out.printf("\t%s\n", UpdateTool.locations[i]);
		System.out.println("");
		System.out.println("Example: updatetool grass_main.rules winter_main.rules");
	}

}