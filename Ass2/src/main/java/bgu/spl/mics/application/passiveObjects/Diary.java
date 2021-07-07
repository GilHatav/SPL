package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {
	private List<Report> reports = new LinkedList<>();
	private int total = 0;
	private Boolean Writing = false;
	private static class SingletonHolder {
		private static Diary instance = new Diary();
	}
	/**
	 * Retrieves the single instance of this class.
	 */
	public static Diary getInstance() {
		return SingletonHolder.instance;
	}

	public List<Report> getReports() {
		return this.reports;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public void addReport(Report reportToAdd){
		reports.add(reportToAdd);
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename) {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();
		this.Writing = null;

		String json = gson.toJson(this);


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


	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return total;
	}
	/**
	 * update the number of total mission received
	 */
	public void setTotal()
	{

		this.total = getTotal() + 1;
	}

	/**
	 * @return the state of the diary
	 */
	public boolean isWriting() {
		return Writing;
	}
	/**
	 * set the state of the diary
	 */
	public void setWriting() {Writing = !Writing;}
}