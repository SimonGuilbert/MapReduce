import java.util.ArrayList;
import java.util.Hashtable;

public class ReduceMachine extends Machine{
	
	ArrayList<Hashtable<String,Integer>> data = new ArrayList<Hashtable<String,Integer>>();

	public ArrayList<Hashtable<String, Integer>> getData() {
		return data;
	}
	
	public void addData(Hashtable<String, Integer> dictionary) {
		this.data.add(dictionary);
	}
	
//	public Hashtable<String, Integer> reduce(){
//		Hashtable<String, Integer> reduced = getData().get(0);
//		for (Hashtable<String, Integer> dic : getData()) {
//			reduced.putAll(dic);
//		}
//		return reduced;
//	}
	
	public Hashtable<String, Integer> reduce() {
		Hashtable<String, Integer> reduced = new Hashtable<String, Integer>();
		for (Hashtable<String, Integer> dic : getData()) {
			for (String key : dic.keySet()) {
				if (reduced.containsKey(key)) 
					reduced.put(key, reduced.get(key)+dic.get(key));
				else
					reduced.put(key,dic.get(key));
			}
		}
		return reduced;
	}




	
	

}
