import java.math.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;

public abstract class DiffieHellmanServer extends DiffieHellmanKeyGenerator implements KeyServer {
	Map<UUID,ClientInfo> clients = new HashMap<>(); 

	public DiffieHellmanServer() {
		super();
		System.setProperty("java.security.policy", "mypolicy");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
	}

	public synchronized void connect(UUID clientID) throws RemoteException {
		BigInteger uniquePV = new BigInteger(5, rand);
		boolean unique = true;
		do {
			unique = true;
			for (Map.Entry<UUID,ClientInfo> e : clients.entrySet())
				if (e.getValue().privateValue.equals(uniquePV)) {
					unique = false;
					uniquePV = new BigInteger(5, rand);
					break;
				}
		} while (!unique);

		ClientInfo uniqueClient = new ClientInfo(uniquePV, connectToClient(clientID));
		clients.put(clientID, uniqueClient);
	}
	private KeyClient connectToClient(UUID client) {
		try {
			Registry registry = LocateRegistry.getRegistry();
			KeyClient clstub = (KeyClient) registry.lookup(client.toString());
			return clstub;
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public BigInteger getPrime() throws RemoteException {
		return prime;
	}
	public BigInteger getPrimitive() throws RemoteException {
		return primitiveRoot;
	}
	public BigInteger getMod() throws RemoteException {
		return mod;
	}
	public synchronized void setKey(UUID client, BigInteger primitive) {
		clients.get(client).key = calculateKey(prime, primitive);
		System.out.println(client + " : " + clients.get(client).key);
	}

	public synchronized void requestCipher(UUID client, String username) {
		try {
			Registry timReg = LocateRegistry.getRegistry("svm-tjn1f15-comp2207.ecs.soton.ac.uk", 12345);
			CiphertextInterface ctstub = (CiphertextInterface) timReg.lookup("CiphertextProvider");
			System.out.println("INNER HERE");
			clients.get(client).client.sendData(ctstub.get(username, clients.get(client).key.intValue()));
		} catch (RemoteException | NotBoundException e) {
			System.out.println("OUTER HERE");
			e.printStackTrace();
		}
	}

	private class ClientInfo {
		public KeyClient client;
		public BigInteger privateValue;
		public BigInteger key;
		public ClientInfo(BigInteger privateValue, KeyClient client) {
			this.privateValue = privateValue;
			this.client = client;
		}
	}
}
