import java.math.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.IOException;

public class MyClient extends DiffieHellmanKeyGenerator {
	private KeyGenerator server;

	public MyClient(String host, String username) {
		connect(host);
		readServerValues(username);
	}

	public void connect(String host) {
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			server = (KeyGenerator) registry.lookup("DHG");
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
			//System.out.println("Client Key: " + key);
			server.setKey(mod);
			String encoded = server.sendToMainServer(username, key.intValue());
			System.out.println("Encrypted:\n" + encoded);
			Decrypter d = new Decrypter(encoded);
			System.out.println("Decrypted:");
			System.out.println(d.decrypt(key.intValue()));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MyClient client = new MyClient(args[0],args[1]);
	}
}
