

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface LogicalClockClientRemote extends LogicalClockRemote{

	void startProcessObject(String threadName) throws MalformedURLException, RemoteException, NotBoundException;
}
