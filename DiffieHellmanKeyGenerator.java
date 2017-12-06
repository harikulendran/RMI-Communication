import java.util.*;
import java.math.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;

public abstract class DiffieHellmanKeyGenerator implements KeyGenerator {
	protected BigInteger mod; 
	protected BigInteger prime;
	protected BigInteger primitiveRoot;
	protected BigInteger privateValue;
	protected BigInteger key;
	private Random rand;
	
	public DiffieHellmanKeyGenerator() {
		rand = new Random();
		privateValue = new BigInteger(3, rand);
	}

	protected void setPrimeValues(String prime, String primitiveRoot) {
		this.prime = new BigInteger(prime);
		this.primitiveRoot = new BigInteger(primitiveRoot);
	}

	protected BigInteger calculateKey(BigInteger prime, BigInteger primitiveRoot) {
		return primitiveRoot.modPow(privateValue, prime);
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
	public void setKey(BigInteger primitive) {
		key = calculateKey(prime, primitive);
		System.out.println("Key: " + key);
	}

	public String sendToMainServer(String username, int key) {
		System.setProperty("java.security.policy", "mypolicy");
		System.out.println(">");
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
