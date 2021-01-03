package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Yannis Tzitzikas (yannistzitzik@gmail.com)
 * 
 * A class for reading one column from a CSV file
 *
 */
class Entity{
	String name;
	String category;
	Entity(String name,String category){
		this.name = name;
		this.category = category;
	}
	public String toString() {
		return name+"_"+category;
		
	}
}

public class CSVReader {
	
	/**
	 * Reads s csvFile and returns a Arraylist of arrays to Strings
	 * @param csvFile
	 * @return
	 */
	static public ArrayList readFile(String csvFile) {
		ArrayList resultAL = new ArrayList();
		
		String line = "";
        String cvsSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] values = line.split(cvsSplitBy);
                resultAL.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultAL;
	}
	
	/**
	 * It takes as input an arraylist (of tables to strings) and a column number
	 * and returns an arrayList with those strings that occur  in column i
	 * @param a  ArrayList of arrays to strings
	 * @param i  column number starting from 0
	 * @return An arrayList with those strings that occur  in column i
	 */
	public static ArrayList getValueColumn(ArrayList a, int i) {
		ArrayList ra = new ArrayList();
		for (Object o: a) {
			String[] sa = ((String[]) o);
			ra.add(sa[i]);
		}
		return ra;
	}

	/**
	 * It takes as input an arraylist of string, it cases them to integer
	 * sorts them  in desc order and returns an arrayList of integers
	 * @param a ArrayList of strings
	 * @return an arraylist of 
	 */
	public static ArrayList<Integer> toIntDescending(ArrayList a) {
		
		ArrayList<Integer> ret = new ArrayList<>();
		for (Object o: a) {
			String s = (String) o;
			float f = Float.parseFloat(s); // the float case
			int i = Math.round(f);
			
			ret.add(i);
		}
		
		ret.sort((p1, p2) -> {return p2 - p1;});
		return ret;
	}
	
	
	/**
	 * Manos
	 * @param a
	 * @return
	 */
	public static ArrayList<String> getNamesOfBuildings(ArrayList a) {
		ArrayList<String> ret = new ArrayList<>();
		for (Object o: a) {
			String s = (String) o;
			ret.add(s);
		}
		return ret;
	}
	
	/**
	 * It takes as input a filename and two columns ids, and returns a map
	 * that has grouped the value of the ValCol according to the values of the groupByCol
	 * @param filename
	 * @param groupByCol
	 * @param ValCol
	 * @return
	 */
	public static Map<String,ArrayList<String>> readGroupedByValues(String filename, int groupByCol, int ValCol){
	      System.out.println("READ GROUPED BY VALUES");
	      String csvFile = filename;  // e.g.  "Resources/citiesContinents.csv";
		  
	      // PART A: Loading the  data of the file (as an arraylist of arrays to string)
	      ArrayList data = CSVReader.readFile(csvFile); // arraylist of arrays to string
	     // ArrayList dataTmp = new ArrayList(data);
	      //System.out.println(data);
	      printReadValues(data);
	      
	      /*
	       * Before the map creation begins, we may sort the data (inevitable action to include labels)
	       */
	      
	     /* Map<Integer,Entity>sortProc = new TreeMap<>(Collections.reverseOrder());
	      data.clear();
	      for(Object o:dataTmp) {
	    	  String[] sa = ((String[]) o);
	    	  int val = Integer.parseInt(sa[ValCol]);
	      }*/
	      
	      // PART B: Grouping the values according to the grouByCol and ValCol
	      // The map will contain groupByVal-->ArrayList(vals)
	      Map<String,ArrayList<String>> mapGV = new TreeMap<>(); 
	      
	      for (int row=0; row < data.size(); row++) {
	    	   String rowGroupByVal = ((String[])data.get(row))[groupByCol]; // the groupBy value in the current row
	    	   String rowVal = ((String[])data.get(row))[ValCol];   // the value (to be visualized) in the current row
	    	   System.out.println("G:"+rowGroupByVal+" V:"+rowVal);
	    	   
	    	   if (!mapGV.containsKey(rowGroupByVal)) {  // if we haven't encountered this groupBy value
	    		   ArrayList newa = new ArrayList();  // new arraylist for the values that appear with this groupbyvalue
	    		   newa.add(rowVal);
	    		   mapGV.put(rowGroupByVal,newa); // addition to the map
	    	   } else {  // the groupBy val already exists in the map
	    		   mapGV.get(rowGroupByVal).add(rowVal); //adds the rowVal to the arraylist of the groupby val
	    	   }
	      }
	      return mapGV;
	 }
	
	
	//test
	public static Map<Integer,Map<String,String>> readDataForPieChart(String filename, int groupByCol, int ValCol,int nameCol){
		System.out.println("READ GROUPED BY VALUES");
	    String csvFile = filename;  // e.g.  "Resources/citiesContinents.csv";
		  
	    // PART A: Loading the  data of the file (as an arraylist of arrays to string)
	    ArrayList data = CSVReader.readFile(csvFile); // arraylist of arrays to string
	    //System.out.println(data);
	    //printReadValues(data);
		
	    //Sort the dataSet
	    Map<Integer,Entity>tmpMap = new HashMap<>();
	    for (int row=0; row < data.size(); row++) {
	    	   String rowGroupByVal = ((String[])data.get(row))[groupByCol]; // the groupBy value in the current row
	    	   String rowVal = ((String[])data.get(row))[ValCol];   // the value (to be visualized) in the current row
	    	   String name = ((String[])data.get(row))[nameCol];
	    	   int rowIntVal = Integer.parseInt(rowVal);
	    	   tmpMap.put(rowIntVal, new Entity(name,rowGroupByVal));
	    }
	    Map<Integer,Entity>sortedMap = new TreeMap<>(Collections.reverseOrder());
	    sortedMap.putAll(tmpMap);
	    
	    Map<String,ArrayList<String>> mapGV = new TreeMap<>(); 
	    
	    tmpMap.forEach((k, v) -> {
	    	if (!mapGV.containsKey(v.category)) {  // if we haven't encountered this groupBy value
	    		   ArrayList newa = new ArrayList();  // new arraylist for the values that appear with this groupbyvalue
	    		   newa.add(k);
	    		   mapGV.put(v.category,newa); // addition to the map
	    	   } else {  // the groupBy val already exists in the map
	    		   mapGV.get(v.category).add(Integer.toString(k)); //adds the rowVal to the arraylist of the groupby val
	    	   }
	    	//System.out.println(k + " " + v );
		});
		
	    System.out.println(mapGV);
	   //s\ System.out.println(sortedMap);
		return null;
	}
	
	/**
	 * Reads the names of values to add labels to pie chart. TO BE INCLUDED IN MAIN IPUT METHOD
	 * @param filename
	 * @param groupByCol
	 * @param name
	 * @return
	 * @author Manos Chatzakis
	 */
	public static Map<String,ArrayList<String>> readNames(String filename, int groupByCol, int name){
	      System.out.println("READ GROUPED BY VALUES");
	      String csvFile = filename;  // e.g.  "Resources/citiesContinents.csv";
		  
	      // PART A: Loading the  data of the file (as an arraylist of arrays to string)
	      ArrayList data = CSVReader.readFile(csvFile); // arraylist of arrays to string
	      
	      // PART B: Grouping the values according to the grouByCol and ValCol
	      // The map will contain groupByVal-->ArrayList(vals)
	      Map<String,ArrayList<String>> mapGV = new TreeMap<>(); 
	      
	      for (int row=0; row < data.size(); row++) {
	    	   String rowGroupByVal = ((String[])data.get(row))[groupByCol]; // the groupBy value in the current row
	    	   String rowVal = ((String[])data.get(row))[name];   // the value (to be visualized) in the current row
	    	   System.out.println("G:"+rowGroupByVal+" V:"+rowVal);
	    	   
	    	   if (!mapGV.containsKey(rowGroupByVal)) {  // if we haven't encountered this groupBy value
	    		   ArrayList newa = new ArrayList();  // new arraylist for the values that appear with this groupbyvalue
	    		   newa.add(rowVal);
	    		   mapGV.put(rowGroupByVal,newa); // addition to the map
	    	   } else {  // the groupBy val already exists in the map
	    		   mapGV.get(rowGroupByVal).add(rowVal); //adds the rowVal to the arraylist of the groupby val
	    	   }
	      }
	      return mapGV;
	 }

	/**
	 * It takes as input an ArrayList of Integers whose values are in desc order,
	 * and normalizes the values of the array list.
	 * The max element of the arraylist will become max
	 * The min element of the arraylist will become min
	 * @param a the input arraylist
	 * @param min the min value of the target scale
	 * @param max the max value of the target scale
	 * @return the arrayList with normalized values
	 */
	public static void Normalize(ArrayList<Integer> a, int min, int max) {
		int  amax = (int) a.get(0); // max value
		int  amin = (int) a.get(a.size()-1); // min value
		Normalize(a,min,max,amin,amax);
	}
	
	/**
	 * It takes as input an ArrayList of Integers 
	 * and normalizes the values of the array list.
	 * @param a the input arraylist
	 * @param min the min value of the target scale
	 * @param max the max value of the target scale
	 * @param dataMin the min value of the data (not necessarily in the arrayList, e.g. in pie chart)
	 * @param dataMax the max value of the data (not necessarily in the arrayList, e.g. in pie chart)
	 * @return the arrayList with normalized values
	 */
	public static void Normalize(ArrayList<Integer> a, int min, int max, int dataMin, int dataMax) {
		for (int i=0; i< a.size(); i++) {
			int v = a.get(i);
			int vn = (int) ( min + ((v-dataMin+0.0)*(max-min) / (dataMax-dataMin)));
			a.set(i, vn);
			//System.out.println("Before:" + v + "\t After:" + vn);
		}
	}
	
	/**
	 * It takes as input an ArrayList and prints an arraylist which either contains arrays of strings, or strings
	 * @param a
	 */
	public static void printReadValues(ArrayList a) {
		for (Object o: a) {
			if (o instanceof String[])  {
				String[] sa = ((String[]) o);
				for (String s: sa)
				 	System.out.print(s + " ");
			} else
				System.out.print(o);
			System.out.println();
    	}
	}
	
	/**
	 * Iterates the data arraylist and creates a map of value-name entities sorted by value in  order. 
	 * @param valKeyIndex The value column
	 * @param nameIndex The name column
	 * @param a The data arraylist
	 * @return The map of entities-value name created
	 * @author Manos Chatzakis
	 */
	public static Map<String,Integer> readValuesAndCreateMap(int valKeyIndex,int nameIndex,ArrayList a) {
		Map<String,Integer>nameValMap = new TreeMap<String,Integer>(Collections.reverseOrder());
		
		for (Object o: a) {
			String[] sa = ((String[]) o);
			float floatInput = Float.parseFloat(sa[valKeyIndex]);
			int curVal = Math.round(floatInput);
			String curName = sa[nameIndex];
			nameValMap.put(curName,curVal);
		}
		
		Map<String, Integer> sortedMap = nameValMap.entrySet().stream().sorted(Entry.comparingByValue()).collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		return sortedMap;
	}
	
	/**
	 * Iterates the map and fills the lists with the corresponding values and names
	 * @param originalValues The values of the map
	 * @param originalNames The names of the map
	 * @param mapGV The map holding the value-name entities
	 * @author Manos Chatzakis
	 */
	public static void separateMapToLists(ArrayList<Integer>originalValues,ArrayList<String>originalNames,Map<String,Integer>mapGV) {
		mapGV.forEach((k, v) -> {
			originalValues.add(v);	
			originalNames.add(k);
		});
		Collections.reverse(originalValues);
		Collections.reverse(originalNames);
	}
	
    public static void main(String[] args) {
    	// just some tests
    	
    	String csvFile = "Resources/english.csv";//"Resources/cities.csv";
    	
    	ArrayList tmp = readFile(csvFile);
    	//printReadValues(tmp);
    	System.out.println(tmp.size());  	
    	/*ArrayList tmp2 = getValueColumn(readFile(csvFile),1);
    	printReadValues(tmp2);*/
    	
 
    	/*System.out.println("===");
    	ArrayList tmp3 = toIntDescending(getValueColumn(readFile(csvFile),1));
    	printReadValues(tmp3);
    	Normalize(tmp3, 10, 100);*/
    	
    	//Some tests just to create the maps 
    	ArrayList<Integer>vals = new ArrayList<>();
    	ArrayList<String>names = new ArrayList<>();
    	separateMapToLists(vals,names,readValuesAndCreateMap(1,0,tmp));
    	for(int i = 0; i<vals.size(); i++) {
    		System.out.println(names.get(i)+"=>"+vals.get(i));
    	}
    	System.out.println(vals.size());
    	System.out.println(names.size());
    	
    	//readGroupedByValues(csvFile, 1, 2);
    	//System.out.println(readValuesAndCreateMap(csvFile, 1, 2,0));
    	//readDataForPieChart(csvFile,1,2,0);
    }
}