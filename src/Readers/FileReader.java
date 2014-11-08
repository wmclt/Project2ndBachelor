package Readers;

import java.io.*;
import java.util.*;

/**
 * @author Brecht Gosselé & William Mauclet 2Bir: wtk-cws (Gosselé)
 *         en cws-elt(Mauclet)
 * @version 3.0
 *
 */
public class FileReader{
	
	public static String openFile(String nameProgram) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(nameProgram));
		String programString = "";
		while(scanner.hasNext())
			programString += scanner.next();
		scanner.close();
		return programString;
		
	}

	
}