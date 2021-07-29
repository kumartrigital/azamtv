import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


public class TestOne {

	/**
	 * @param args
	 *//*
	 *
	public static void main(String[] args) {
		
			int data [] = {2,6,4,1,2,2};

			System.out.println("Index value of data array is "+data.length);

			for(int i = 0; i < data.length; i++)
			System.out.println(data[i]);
			}
*/
	/*
	   public static void main(String[] args) {
	        // TODO Auto-generated method stub
	        try {
	            String[] x = { "7","7","4","5","4","4","3", "3", "3", "1" };
	            String total[] = new String[50];
	            String sTotal[] = null;
	            for (int i = 0; i < x.length; i++) {
	                total[i] = x[i];
	            }
	            for (int k = 0; k < total.length; k++) {
	                int count = 0;
	                if (total[k] != null) {
	                    sTotal = new String[50];
	                    for (int i = 0; i < total.length; i++) {
	                        if (total[k] == total[i]) {
	                            count++;
	                            if (count <= 1) {
	                                sTotal[i] = total[k];
	                            }
	                        }
	                    }
	                    if (sTotal[k] != null) {
	                        for(int j=0; j<count; j++){
	                            System.out.print(sTotal[k]+"\t");
	                        }
	                        System.out.print("\n");
	                    }
	                }

	            }
	        }
	        catch (Exception e) {

	        }
	    }
*/
	
	/*public static void main(String[] args) {
	        String text = "4 4 4 4 4 5 5 5 1 1 1 1 3 3 2";
	 
	        List<String> list = Arrays.asList(text.split(" "));
	 
	        Set<String> uniqueWords = new HashSet<String>(list);
	        for (String word : uniqueWords) {
	            System.out.println(word + ": " + Collections.frequency(list, word));
	        }
	    }
	 */
	 
	   /* public static void main(String a[]){
	         
	        Map<Integer , Integer> ts = new HashMap<Integer , Integer>(new MyComp());
	        ((Object) ts).put(1, 3);
	        ts.put(2, 4);
	        ts.put(3, 6);
	        ts.put(4, 8);
	        
	        System.out.println(ts);
	    }*/
	   
	   
		   public static void main(String a[]){
	           Scanner sc = new Scanner(System.in);
	           System.out.println("Enter the value ");
	           int n = sc.nextInt();
	           int count = 1;
	           int noOfSpace = 1;
	           int start = 0;

	           for (int i = 1; i < (n * 2); i++) 
	           {

	               for (int spc = n - noOfSpace; spc > 0; spc--) 
	               {
	                   System.out.print(" ");
	               }
	               if (i < n) 
	               {
	                   start = i;            //this is for number
	                   noOfSpace++;    //this is for space
	               } else 
	               {
	                   start = n * 2 - i;        //this is for number
	                   noOfSpace--;             //this is for space
	               }
	               for (int j = 0; j < count; j++) 
	               {
	                   System.out.print(start);
	                   if (j < count / 2) 
	                   {
	                       start--;
	                   } else 
	                   {
	                       start++;
	                   }
	               }
	               if (i < n)
	               {
	                   count = count + 2;
	               } else {
	                   count = count - 2;
	               }

	               System.out.println();
	           }
	       }
	   }
	
	

