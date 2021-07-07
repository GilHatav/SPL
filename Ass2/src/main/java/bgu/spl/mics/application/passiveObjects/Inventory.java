package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 *  That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private List<String> gadgets = new LinkedList<>();
	private static class SingletonHolder {
		private static Inventory instance = new Inventory();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * Initializes the inventory. This method adds all the items given to the gadget
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	public void load (String[] inventory) {
		for(int i=0; i< inventory.length; i++){
			gadgets.add(inventory[i]);
		}
	}

	/**
	 * acquires a gadget and returns 'true' if it exists.
	 * <p>
	 * @param gadget 		Name of the gadget to check if available
	 * @return 	‘false’ if the gadget is missing, and ‘true’ otherwise
	 */
	public synchronized boolean getItem(String gadget){
		boolean ans = false;
		for(String s : gadgets){
			if(s.equals(gadget)){
				gadgets.remove(s);
				ans = true;
				break;
			}
		}
		return ans;
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Gadget> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){
		Gson gson = new Gson();

		String json = gson.toJson(this.gadgets);

		File myFile = new File(filename);
		try {
			myFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(myFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
		try {
			myOutWriter.append(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			myOutWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}