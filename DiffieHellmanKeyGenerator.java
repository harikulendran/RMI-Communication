import java.util.*;
import java.math.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;

public abstract class DiffieHellmanKeyGenerator {
	protected BigInteger mod; 
	protected BigInteger prime;
	protected BigInteger primitiveRoot;
	protected BigInteger privateValue;
	protected BigInteger key;
	protected Random rand;
	
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

}
