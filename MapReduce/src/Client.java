import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	private Socket clientSocket;
	private DataOutputStream out;
	private BufferedReader in;
	
	public void startConnection(String ip, int port) throws UnknownHostException, IOException {
		clientSocket = new Socket(ip, port);
		out = new DataOutputStream(clientSocket.getOutputStream());
		in = new BufferedReader(new
				InputStreamReader(clientSocket.getInputStream()));
	}
	
    public String sendFile(File file) throws IOException {
        FileInputStream fileIn = new FileInputStream(file);
        byte[] buf = new byte[Short.MAX_VALUE];
        int bytesRead;        
        while( (bytesRead = fileIn.read(buf)) != -1 ) {
            out.writeShort(bytesRead);
            out.write(buf,0,bytesRead);
        }
        out.writeShort(-1);
        fileIn.close();
        String resp = in.readLine();
		return resp;
        
    }

	public void stopConnection() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}
	
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		long startTime = System.currentTimeMillis();
		Client client = new Client();
		client.startConnection("127.0.0.1", 1600);
		String wordsCompted = client.sendFile(new File("files/Jules-Verne-Voyage-au-centre-de-la-Terre.txt"));
		long receiveTime = System.currentTimeMillis();
		for (String word : wordsCompted.replace(",","").replace("{", "").replace("}", "").split(" ")) {
			System.out.println(word);
		}
		//System.out.println(wordsCompted);
		System.out.println("---------> TIME : "+(receiveTime-startTime)+"ms <---------");
	}

}
