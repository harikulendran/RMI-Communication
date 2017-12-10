import java.math.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;

public abstract class DiffieHellmanServer extends DiffieHellmanKeyGenerator implements KeyGenerator {

	public BigInteger getPrime() throws RemoteException {
		return prime;
	}
	public BigInteger getPrimitive() throws RemoteException {
		return primitiveRoot;
	}
	public BigInteger getMod() throws RemoteException {
		return mod;
	}
	public void setKey(BigInteger primitive) {
		key = calculateKey(prime, primitive);
		System.out.println("Key: " + key);
	}

	public String sendToMainServer(String username, int key) {
		System.setProperty("java.security.policy", "mypolicy");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		try {
			Registry timReg = LocateRegistry.getRegistry("svm-tjn1f15-comp2207.ecs.soton.ac.uk", 12345);
			CiphertextInterface ctstub = (CiphertextInterface) timReg.lookup("CiphertextProvider");
			return (ctstub.get(username, key));
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		return "ERROR";
	}
}
