import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

class ProcessThread implements Runnable {
    String name;
    Thread t;
    LogicalClock logicalClock;
    CounterObject counterObject;
    
    ProcessThread(String threadname, LogicalClock logicalClock) {
    	counterObject =  new CounterObject();
 		
        counterObject.start(); 
        this.name = threadname;
        this.t = new Thread(this, name);
        System.out.println("New Process: " + t);
        this.t.start();
        this.logicalClock = logicalClock;    
           
    } 

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while(true)
		{
			
			if(logicalClock.getTimerFlag() == false)
			{
					logicalClock.setCounter(counterObject);
			}
			if(logicalClock.getEndFlag() == true)
			{
				counterObject.stop();
				break;
			}
			
		}
	
	}

	public int receive() {
		return 0;
		
	}
   
}

public class LogicalClockClient extends UnicastRemoteObject implements LogicalClockClientRemote{

	
	protected LogicalClockClient() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void startProcessObject(String threadName) throws RemoteException {
		// TODO Auto-generated method stub
		logicalClock = new LogicalClock();
		processThread= new ProcessThread(threadName, logicalClock);
	}
	
	@Override
	public void setLogicalCounter(CounterObject counterObject) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Setting the Counter Object from Server");
		logicalClock.setCounter(counterObject);
	}
	
	@Override
	public void setLogicaEndFlag(boolean flag) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Setting the End Flag from Server");
		logicalClock.setEndFlag(flag);	
	}
	
	@Override
	public boolean getLogicaTimerFlag() throws RemoteException {
		// TODO Auto-generated method stub
		return logicalClock.getTimerFlag();
	}

	@Override
	public CounterObject getLogicaCounter() throws RemoteException {
		// TODO Auto-generated method stub
		return logicalClock.getCounter();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static LogicalClockServerRemote look_up;
	LogicalClock logicalClock;
	ProcessThread processThread;

	public static void main(String[] args) 
		throws MalformedURLException, RemoteException, NotBoundException, UnknownHostException, AlreadyBoundException {
		
		if(args.length<2)
		{
			System.out.println("Host name and port of the LogicalClockServer");
			System.out.println("java LogicalClockClient <hostname> <rmiport>");
			return;
		}
		String bindLocation = "//" + args[0] + ":" + args[1] + "/LogicalClockServer";
		look_up = (LogicalClockServerRemote)Naming.lookup(bindLocation);
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the Process Object Name");
		String processObject = scanner.nextLine();
		System.out.println("Enter the RMIPort for this machine");
		String rmiPort = scanner.nextLine();
		InetAddress localhost = InetAddress.getLocalHost(); 
		System.out.println(localhost.getHostAddress());
		System.out.println(processObject);
		System.out.println(rmiPort);
		
		boolean response = look_up.registerClient(localhost.getHostAddress(), processObject, Integer.parseInt(rmiPort));
		System.out.println(look_up.registerClient(localhost.getHostAddress(), processObject, Integer.parseInt(rmiPort)));
		if(response==true)
		{
			LogicalClockClientRemote logicalClockClientRemote = new LogicalClockClient();
			
			String name = "/LogicalClockClient"+processObject;
			bindLocation = "//" + localhost.getHostAddress().trim() + ":" + args[0] +name;
	        Naming.bind(bindLocation, logicalClockClientRemote);
            System.err.println("Client ready");
		}
		
		System.out.println("Want to add more Clients yes/no");
		scanner = new Scanner(System.in);
		String processObjectConfirm = scanner.nextLine();
		if(processObjectConfirm.equalsIgnoreCase("No"))
		{
			look_up.closeClients();
		}
		
		
		
	}

	

}
