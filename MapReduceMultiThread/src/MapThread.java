import java.util.ArrayList;

public class MapThread implements Runnable {
	
	private Thread thread;
	private MapMachine mapMachine;
	private ArrayList<String> data;
	
	public MapThread(MapMachine mapMachine, ArrayList<String> data) {
		this.mapMachine = mapMachine;
		this.data = data;
		
		thread = new Thread(this); // instanciation du thread
		thread.start(); // d�marrage du thread, la fonction run() est ici lanc�e
	}


	@Override
	public void run() {
		mapMachine.mapData(data);
	}
}
