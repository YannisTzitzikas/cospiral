/**
 * 
 */
package utils;

import java.util.ArrayList;

/**
 * @author Yannis Tzitzikas (yannistzitzik@gmail.com)
 *
 */

/**
 * 
 * @author Yannis Tzitzikas (yannistzitzik@gmail.com)
 * It creates a synthetically derived set of values.
 *  Various methods for producing decreasing sets of values are supported.
 */
public class SyntheticDataGenerator {
	
	/**
	 * It creates a decreasing by percentage set of values starting from the maxSize
	 * Never returns a value less than minsize
	 * BUG: NEVER GOES TO 0
	 * @param size
	 * @param maxsize
	 * @param minsize
	 * @param decreasePercentage
	 * @return
	 */
	static public ArrayList<Integer> DecreasingByPercentage(int size, int maxsize, int minsize, int decreasePercentage) {
		 ArrayList<Integer> valuesList = new ArrayList();
		 int nextVal = maxsize;
		 for (int i=0; i<size; i++) {
			 nextVal = Math.max(nextVal, minsize);
			 if (maxsize<minsize)
				 throw new IllegalArgumentException("Max and Min size do not make sense.");
					 
			 valuesList.add(nextVal);
			 if (i%(10*1000)==0)  // just for progress monitoring by the console
				 System.out.println("SyntGenerator [Decreasing]: " + i + "  Added value: " + nextVal);
			 nextVal  = (int) Math.round(nextVal - (decreasePercentage * nextVal)/100.0);
		 }
		 return valuesList;
	}
	
	
	
	
	/**
	 * It creates a decreasing set of integer values, as defined by a 
	 * Power-Law function with  constants a and b 
	 * Never returns a value less than minsize
	 * @param size
	 * @param alpha
	 * @param beta
	 * @param minSize
	 * @return
	 */
	static public ArrayList<Integer> PowerLaw(int size, double alpha, double beta, int minSize) {
		 ArrayList<Integer> valuesList = new ArrayList<>();
		 double nextVal = alpha; // the max value
		 for (int i=1; i<=size; i++) {
			 nextVal  = (int) Math.round(alpha / Math.pow(i, beta));
			 nextVal = Math.max(nextVal, minSize); // to be sure no value < minSize is entered
			 //if (maxsize<minsize)
			 //	 throw new IllegalArgumentException("Max and Min size do not make sense.");
					 
			 valuesList.add((int) Math.round(nextVal));
			 
			 System.out.println("SyntGenerator [PL]: " + i + " Added value: "  +
					 	alpha + "/(" + i + "^" + beta  + ")= " +  nextVal);
			  
		 }
		 return valuesList;
	}

	 public static void main(String[] lala) {
		 DecreasingByPercentage(50, 100, 8, 12); // size, maxsize, minsize, decreasePercentage
		 PowerLaw(40, 1024, 2, 1);  // size, altha, beta, minsize
	 }
}
