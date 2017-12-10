import java.math.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;

public abstract class DiffieHellmanServer extends DiffieHellmanKeyGenerator implements KeyGenerator {
	Map<UUID,ClientInfo> clients = new HashMap<>(); 

	public DiffieHellmanServer() {
		super();
		System.setProperty("java.security.policy", "mypolicy");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
	}

	public UUID connect() throws RemoteException {
		UUID uniqueID = UUID.randomUUID();
		while (clients.keySet().contains(uniqueID))
			uniqueID = UUID.randomUUID();

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

		ClientInfo uniqueClient = new ClientInfo(uniquePV);
		clients.put(uniqueID, uniqueClient);
		System.out.println(uniqueID + " : " + uniqueClient.privateValue);
		return uniqueID;
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
	public void setKey(UUID client, BigInteger primitive) {
		clients.get(client).key = calculateKey(prime, primitive);
	}

	public String sendToMainServer(UUID client, String username) {
		try {
			Registry timReg = LocateRegistry.getRegistry("svm-tjn1f15-comp2207.ecs.soton.ac.uk", 12345);
			CiphertextInterface ctstub = (CiphertextInterface) timReg.lookup("CiphertextProvider");
			return (ctstub.get(username, clients.get(client).key.intValue()));
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		return "ERROR";
	}

	private class ClientInfo {
		public BigInteger privateValue;
		public BigInteger key;
		public ClientInfo(BigInteger privateValue) {
			this.privateValue = privateValue;
		}
	}
}
