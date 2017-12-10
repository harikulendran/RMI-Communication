import java.math.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.IOException;
import java.util.UUID;

public class MyClient extends DiffieHellmanKeyGenerator {
	private KeyGenerator server;
	private UUID myID;

	public MyClient(String host, String username) {
		connect(host);
		readServerValues(username);
	}

	public void connect(String host) {
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			server = (KeyGenerator) registry.lookup("DHG");
			myID = server.connect();

		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	public void readServerValues(String username) {
		try {
			prime = server.getPrime();
			primitiveRoot = server.getPrimitive();
			mod = calculateKey(prime, primitiveRoot);
			key = calculateKey(prime, server.getMod());
			server.setKey(myID, mod);
			String encoded = server.sendToMainServer(myID, username);
			Decrypter d = new Decrypter(encoded);
			System.out.println(d.decrypt(key.intValue()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MyClient client = new MyClient(args[0],args[1]);
	}
}
