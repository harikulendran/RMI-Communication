import java.math.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.IOException;

public class MyClient extends DiffieHellmanKeyGenerator {
	private KeyGenerator server;

	public MyClient() {
		connect();
		readServerValues();
		System.out.println("Client Key: " + key);
	}

	public void connect() {
		try {
			Registry registry = LocateRegistry.getRegistry();
			server = (KeyGenerator) registry.lookup("DHG");
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	public void readServerValues() {
		try {
			prime = server.getPrime();
			primitiveRoot = server.getPrimitive();
			mod = calculateKey(prime, primitiveRoot);
			key = calculateKey(prime, server.getMod());
			server.setKey(mod);
			System.out.println(server.sendToMainServer("hk1n15", key.intValue()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MyClient client = new MyClient();
	}
}
