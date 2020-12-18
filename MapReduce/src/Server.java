import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Server implements MachineManager{
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private PrintWriter out;
	private DataInputStream in;
	private File savedFile = new File("files/testCreatedByServer.txt");
	public ArrayList<String> splitted = new ArrayList<String>();
	public ArrayList<MapMachine> listMapMachines = new ArrayList<MapMachine>();
	public ArrayList<ReduceMachine> listReduceMachines = new ArrayList<ReduceMachine>();
	public Hashtable<String,Integer> mappedReduced = new Hashtable<String,Integer>();
	
	public void start(int port) throws IOException {
		
		///////////////////////////////////////////////////////////////////////////////////
		//------------------------- Ajout des Machines ----------------------------------//
		///////////////////////////////////////////////////////////////////////////////////
		
		// Map = 3 machines
		addMachine(new MapMachine());
		addMachine(new MapMachine());
		addMachine(new MapMachine());
		//addMachine(new MapMachine()); // Possibilité d'ajouter des MapMachines
		//addMachine(new MapMachine());
		
		//Reduce = 2 machines (impossible d'en ajouter)
		addMachine(new ReduceMachine());
		addMachine(new ReduceMachine());
		
		///////////////////////////////////////////////////////////////////////////////////
		//---------------------- Attente fichier client ---------------------------------//
		///////////////////////////////////////////////////////////////////////////////////
		
		serverSocket = new ServerSocket(port);
		clientSocket = serverSocket.accept();
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new DataInputStream(clientSocket.getInputStream());
		receiveFileAndCopy(savedFile);
		
		///////////////////////////////////////////////////////////////////////////////////
		//-------------------- Traitement du fichier reçu -------------------------------//
		///////////////////////////////////////////////////////////////////////////////////
		
		String words = readFile(savedFile);
		split(words);
		
		map();
		reduce();
		
		///////////////////////////////////////////////////////////////////////////////////
		//-------------------- Envoi du résultat au client ------------------------------//
		///////////////////////////////////////////////////////////////////////////////////
				
		out.println(mappedReducedSorted());
	
	}

	private String readFile(File file) throws IOException {
    	FileInputStream fis = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        StringBuilder text = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {        	
            text.append(line); // add every line to StringBuilder 
            if (!line.equals("")) text.append(" "); // simulation d'un retour à la ligne
        }
        return text.toString();	
	}

	public void receiveFileAndCopy(File file) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(file);
        byte[] buf = new byte[Short.MAX_VALUE];
        int bytesSent;        
        while( (bytesSent = in.readShort()) != -1 ) {
            in.readFully(buf,0,bytesSent);
            fileOut.write(buf,0,bytesSent);
        }
        fileOut.close();
    } 
	
	@Override
	public boolean addMachine(Machine machine) {
		if (machine.getClass().toString().equals("class MapMachine"))
			return this.listMapMachines.add( (MapMachine) machine);
		else
			return this.listReduceMachines.add( (ReduceMachine) machine);
	}

	@Override
	public boolean removeMachine(Machine machine) {
		if (machine.getClass().toString().equals("class MapMachine"))
			return this.listMapMachines.remove( (MapMachine) machine);
		else
			return this.listReduceMachines.remove( (ReduceMachine) machine);
	}
	
	public ArrayList<MapMachine> getMapMachines(){
		return this.listMapMachines;
	}
	
	public ArrayList<ReduceMachine> getReduceMachines(){
		return this.listReduceMachines;
	}
	

	public void split(String input) {
		input = input.replace(".", "").replace(":", "").replace("!", "").replace("?", "").replace(";", "").replace(")", "").replace("  ", " ");
	    input = input.replace("Ã©", "é").replace("Ãª", "ê").replace("Ã¨", "è").replace("(", "").replace("Ã¢", "â").replace("â€™", "'");
	    input = input.replace("Ã§", "ç").replace("Ã", "à");
	    Collections.addAll(splitted, input.toLowerCase().split(" ")); //Insère chaque mot de input dans un ArrayList splitted
	}
	
	public void map() {
		int rgMachine = 0;
		int i=0;
		while (i<splitted.size()) {
			ArrayList<String> temp = new ArrayList<String>();
			for (int j=0;j<splitted.size()/getMapMachines().size();j++) {
				if (i+j<splitted.size()) {
					temp.add(splitted.get(i+j));
				}
			}
			getMapMachines().get(rgMachine).mapData(temp);
			i = i + (splitted.size())/(getMapMachines().size());
			if (rgMachine < getMapMachines().size()-1) rgMachine++;
		}
	}
	
	public void reduce() {
		for (MapMachine mm : getMapMachines()) {
			for (String key : mm.getData().keySet()) {
				Hashtable<String,Integer> temp = new Hashtable<String,Integer>();
				temp.put(key, mm.getData().get(key));
				if ((int) key.charAt(0) < 111)
					getReduceMachines().get(0).addData(temp);
				else
					getReduceMachines().get(1).addData(temp);
			}
		}
		mappedReduced = getReduceMachines().get(0).reduce();
		mappedReduced.putAll(getReduceMachines().get(1).reduce());
	}
	
    private String mappedReducedSorted() {
    	Map<String,Integer> topHundred = 
    			mappedReduced.entrySet().stream()
    			.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
    			.limit(150)
    			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		return topHundred.toString();
	}
	
	public void stop() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
		serverSocket.close();
	}

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.start(1600);
	}

}
