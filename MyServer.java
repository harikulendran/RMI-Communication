import java.math.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.IOException;

public class MyServer extends DiffieHellmanServer {

	public MyServer(String prime, String primitive) {
		setPrimeValues(prime, primitive);
		mod = calculateKey(this.prime, this.primitiveRoot);
		//initServer();
	}

	private void initServer() {
		try {
			KeyGenerator DHG = (KeyGenerator) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("DHG", DHG);
			System.out.println("Ready...");
		} catch (IOException | AlreadyBoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MyServer server = new MyServer("191","131");
		server.initServer();
	}

	/*public void setClient(MyClient client) {
		this.client = client;
		key = calculateKey(prime, client.getMod());
		System.out.println("Server Key: " + key);
	}*/
}
