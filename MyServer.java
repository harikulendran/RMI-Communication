import java.math.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.IOException;

public class MyServer extends DiffieHellmanServer {

	public MyServer(String prime, String primitive) {
		setPrimeValues(prime, primitive);
		initServer();
	}

	private void initServer() {
		try {
			KeyServer DHG = (KeyServer) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("DHG", DHG);
			System.out.println("Ready...");
		} catch (IOException | AlreadyBoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MyServer server = new MyServer("191","131");
	}
}
