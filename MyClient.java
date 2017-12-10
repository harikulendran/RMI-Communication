import java.math.*;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.IOException;
import java.util.UUID;

public class MyClient extends DiffieHellmanKeyGenerator implements KeyClient {
	private KeyServer server;
	private UUID myID;
	String username;

	public MyClient(String host, String username) {
		myID = UUID.randomUUID();
		this.username = username;
		initClient();
		connect(host);
		exchange();
	}

	private void initClient() {
		try {
			KeyClient thisClient = (KeyClient) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.bind(myID.toString(), thisClient);
		} catch (IOException | AlreadyBoundException e) {
			e.printStackTrace();
		}
	}

	private void connect(String host) {
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			server = (KeyServer) registry.lookup("DHG");
			server.connect(myID);
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	private void exchange() {
		try {
			prime = server.getPrime();
			primitiveRoot = server.getPrimitive();
			mod = calculateKey(prime, primitiveRoot);
			key = calculateKey(prime, server.getMod());
			server.setKey(myID, mod);
			System.out.println(myID + " : " + key);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void requestCipher() {
		try {
			server.requestCipher(myID, username);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void sendData(String data) {
		Decrypter d = new Decrypter(data);
		System.out.println(d.decrypt(key.intValue()));
	}

	public static void main(String[] args) {
		for (int i=0; i<3; i++) {
			new Thread(() -> {
				MyClient client = new MyClient(args[0],args[1]);
				client.requestCipher();
			}).start();
		}
	}
}
