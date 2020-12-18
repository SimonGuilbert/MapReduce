import java.util.ArrayList;
import java.util.Hashtable;

public class MapMachine extends Machine {
	
	Hashtable<String,Integer> data = new Hashtable<String,Integer>();
	
	public Hashtable<String, Integer> getData() {
		return data;
	}
	
	public void mapData(ArrayList<String> words) {
		for (String word:words) {
			if (data.containsKey(word)) 
				data.put(word, data.get(word)+1);
			else
				data.put(word,1);
		}
	}
	
	

}
