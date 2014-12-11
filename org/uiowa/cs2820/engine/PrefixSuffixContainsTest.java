package org.uiowa.cs2820.engine;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PrefixSuffixContainsTest {

	@Test
	public void setup() {	
		CheckPoint.reset();
		DiskSpace.reset();
		// sample data from file
		HashMap<String,ArrayList<Field>> H = new HashMap<String,ArrayList<Field>>();
		String CurrentIndex = null;
		ArrayList<Field> CurrentFields = null;
	 	try {
		  BufferedReader B = new BufferedReader(new FileReader("Resources/PrefixSuffixContains.txt"));
		  String line;
		  String[] tokens;
		  // NOTE: has a bug (EOF not handled well); use extra fake input line at end
		  while ((line = B.readLine()) != null) {
			tokens = line.split("\\s+");
			if (CurrentIndex == null || !CurrentIndex.equals(tokens[0])) {
			  if (CurrentIndex != null) H.put(CurrentIndex,CurrentFields);
			  CurrentIndex = tokens[0];
			  CurrentFields = new ArrayList<Field>();
			  }
			CurrentFields.add(new Field(tokens[1],tokens[2]));
			}
		  B.close();
		  }
		catch (Exception e) { }
		// put dummy data into the database
		Iterator I = H.entrySet().iterator();
		while (I.hasNext()) {
		  Map.Entry pairs = (Map.Entry)I.next();
		  Indexer N = new Indexer((String)pairs.getKey());
		  CurrentFields = (ArrayList<Field>)pairs.getValue();
		  for (Field f: CurrentFields) {
		    N.addField(f);
		    //System.out.println("Added "+N+' '+f);
		    }
		  }
	    }
	  
	@Test
	public void PrefixSearchTest() {
		Field f = new Field("Noun","Dan");
		PrefixSearch F = new PrefixSearch(f);
		String[] S = F.doSearch();

		//make an ArrayList with expected result to compare with actual return value
		ArrayList<String> compare = new ArrayList<String>();
		compare.add("3.txt");
		compare.add("5.txt");
		assertEquals(getResult(S),compare);
	    } 
	  
	@Test
	public void SuffixSearchTest() {
    	Field f = new Field("Verb","ing");
		SuffixSearch F = new SuffixSearch(f);
		String[] S = F.doSearch();
		
		ArrayList<String> compare = new ArrayList<String>();
		compare.add("0.txt");
		compare.add("2.txt");
		assertEquals(getResult(S),compare);
	    } 
  
	@Test
	public void ContainsSearchTest() {
    	Field f = new Field("Noun","an");
		ContainsSearch F = new ContainsSearch(f);
		String[] S = F.doSearch();
		//System.out.println(getResult(S));
		
		ArrayList<String> compare = new ArrayList<String>();
		compare.add("3.txt");
		compare.add("1.txt");
		compare.add("5.txt");
		assertEquals(getResult(S),compare);
	}	
  
	//Added method to compare the content return by the search vs just the length of the returned value
	public ArrayList<String> getResult(String[] S) {
		ArrayList<String> result = new ArrayList<String>();
		for (String s: S) {
			result.add(s.toString());
		}
		return result;
	}
		
}
