public class CuckooHashUtils{
	private static int BASE = 197;
	public static int hashTableOne=1;
	public static int hashTableTwo=2;
	public static int tableSize=10;
	
	
	public static int hashOne(Integer key){
        int intLength = String.valueOf(key).length();
        int base = 10;
        int sum = 0;
        for(int x = 0; x < intLength; x++){
                sum += (key%base)/(base/10);
                base = base*10;
        }
        return sum;
	}
    public static int hashTwo(int key){
        int intLength = String.valueOf(key).length();
        int base = 10;
        int product = 1;
        for(int x = 0; x < intLength; x++){
                product *= (key%base)/(base/10);
                base = base*10;
        }
        return product % tableSize;
	}

	public static int hashThree(int key){
	        int intLength = String.valueOf(key).length();
	        int base = 10;
	        int result = 0;
	        for(int x = 0; x < intLength; x++){
	                result += (key%base)/(base/10)*BASE;
	                base = base*10;
	        }
	        return result % tableSize;
	}

    public static int hash(int hashNum, Integer key) {
            int hash = 0;
            if (hashNum == 1) {
                    hash = hashOne(key);
            } else if (hashNum == 2) {
                    hash = hashTwo(key);
            } else if (hashNum == 3) {
                    hash = hashThree(key);
            }
            return hash % tableSize;
    }
    public static String showHash(String key,int hashNum, int position){
    	String out = "(";
    	for(int ii=0;ii<key.length();ii++){
    		out+=key.substring(ii,ii+1);
	    	if(hashNum==1 && ii!=key.length()-1){
	    		out+= "+";
	    	}else if(hashNum==2 && ii!=key.length()-1){
	    		out+="*";
	    	}else if(ii!=key.length()-1){
	    		out+="*197+";
	    	}
    	}
    	out+= ") % 10 = "+position;
    	return out;
    }
    
}