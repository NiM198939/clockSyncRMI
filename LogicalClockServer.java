

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Scanner;
import java.util.Set;


class ProcessMain implements Runnable {
    String name;
    Thread t;
    LogicalClock logicalClock;
    CounterObject counterObject;
    List<ProcessObject> processObjectList;

    ProcessMain(String threadname, LogicalClock logicalClock, List<ProcessObject> processObjectList) {
    	counterObject =  new CounterObject();
        counterObject.start();
        
        this.name = threadname;
        this.t = new Thread(this, name);
        System.out.println("New Main Process: " + t);
        this.logicalClock = logicalClock;
        this.processObjectList = processObjectList;
        this.t.start();
        
    }
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int numberIterations = 0;
		List<LogicalClockClientRemote> lookUpClient = new LinkedList<LogicalClockClientRemote>();
		for(int i=0;i<processObjectList.size();i++)
		{
			try {
				ProcessObject processObject = processObjectList.get(i);
				String bindLocation = "//" + processObject.ipAddress + ":" + processObject.rmiPort + "/LogicalClockClient"+processObject.name;
				lookUpClient.add( (LogicalClockClientRemote)Naming.lookup(bindLocation));
			}
			 catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(true)
		{
			logicalClock.setCounter(counterObject);
			boolean check = receiveTime(this.logicalClock, lookUpClient, processObjectList);
			numberIterations = numberIterations + 1;
			if(check)
			{
				for(int i = 0; i < lookUpClient.size(); i++)
				{
					try {
						lookUpClient.get(i).setLogicaEndFlag(true);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				counterObject.stop();
				break;
			}
				
		}
		System.out.println("numberIterations" + numberIterations);
		
	}
	
	private boolean receiveTime(LogicalClock logicalClock, List<LogicalClockClientRemote> lookUpClient, List<ProcessObject> processObjectList) {
		Set<Long>values = new HashSet<Long>();
		while(true)
		{	
		
			ArrayList<Boolean> booleanFlags =  new ArrayList<Boolean>();
			Set<Long>counterValue = new HashSet<Long>();
			for(int i = 0; i < lookUpClient.size(); i++)
			{
				try {
					booleanFlags.add(lookUpClient.get(i).getLogicaTimerFlag());
					counterValue.add(lookUpClient.get(i).getLogicaCounter().getCounter());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(!booleanFlags.contains(false))
			{
				int[] num = counterValue.parallelStream().mapToInt(Long::intValue).toArray();
				OptionalDouble averageThreads = Arrays.stream(num).average();
				Double average = averageThreads.getAsDouble();
				try {
					
					System.out.println("Average is "+average);
					for(int i = 0; i < lookUpClient.size(); i++)
					{
						Long offset = average.intValue() - lookUpClient.get(i).getLogicaCounter().getCounter() ;
						CounterObject counterObject = lookUpClient.get(i).getLogicaCounter();
						long realValue = counterObject.getCounter();
						long value = realValue+offset;
						
						System.out.println("threadName= "+processObjectList.get(i).name+" offset= "+offset+" realValue+offset= "+value);
						values.add(realValue);
						counterObject.setCounter(value);
						lookUpClient.get(i).setLogicalCounter(counterObject);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			
		}
		if(values.size()==1)
		{
			return true;
		}
		else
		{
			return false;
		}
			
		
	}
}


public class LogicalClockServer extends UnicastRemoteObject implements LogicalClockServerRemote {

	CounterObject counterObject;
	List<ProcessObject> processObjectList;
	boolean processObjectComplete;
	LogicalClock logicalClock;
	
	protected LogicalClockServer() throws RemoteException {
		super();
		processObjectList = new ArrayList<ProcessObject>();
		processObjectComplete = false;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean registerClient(String processObjectName, String ipaddress, int rmiPort) throws RemoteException, MalformedURLException, NotBoundException {
		// TODO Auto-generated method stub
		if(!processObjectComplete)
		{
			System.out.println("Whether a ProcessObject to be added Yes/No");
			Scanner scanner = new Scanner(System.in);
			String processObjectConfirm = scanner.nextLine();
			if(processObjectConfirm.equalsIgnoreCase("yes"))
			{
				ProcessObject processObject = new ProcessObject(ipaddress,rmiPort,processObjectName);
				processObjectList.add(processObject);
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			System.out.println("Operation already started no more logical clocks can be added");
			return false;
		}
	}
	
	public static void main(String[] args){

		if(args.length<1)
		{
			System.out.println("port of the LogicalClockServer");
			System.out.println("java LogicalClockServer <rmiport>");
			return;
		}
		
		
		System.setProperty("java.security.policy","file:./security.policy");
		if (System.getSecurityManager() == null) 
		{
			 System.setSecurityManager(new RMISecurityManager());
		}
	    try {
	    	LogicalClockServerRemote logicalClockServerRemote = new LogicalClockServer();
	    	InetAddress localhost = InetAddress.getLocalHost(); 
	    	String bindLocation = "//" + localhost.getHostAddress().trim() + ":" + args[0] + "/LogicalClockServer";
	        Naming.bind(bindLocation, logicalClockServerRemote);
	        
            System.err.println("Server ready");
            
            

        } catch (Exception e) {

            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();

        }

    }

	@Override
	public void setLogicalCounter(CounterObject counterObject) throws RemoteException {
		// TODO Auto-generated method stub
		logicalClock.setCounter(counterObject);
		
	}

	@Override
	public void setLogicaEndFlag(boolean flag) throws RemoteException {
		// TODO Auto-generated method stub
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

	@Override
	public void closeClients() throws RemoteException, MalformedURLException, NotBoundException {
		// TODO Auto-generated method stub
		System.out.println("Process Object cant be added");
		System.out.println("No more processes will be added");
		processObjectComplete = true;
		
		LogicalClock logicalClock = new LogicalClock();
		ProcessMain processMain = new ProcessMain("Main", logicalClock, processObjectList);
		LogicalClockClientRemote look_up;
		for(int i=0;i<processObjectList.size();i++)
		{
			ProcessObject processObject = processObjectList.get(i);
			String bindLocation = "//" + processObject.ipAddress + ":" + processObject.rmiPort + "/LogicalClockClient"+processObject.name;
			look_up = (LogicalClockClientRemote)Naming.lookup(bindLocation);
	
			look_up.startProcessObject(processObjectList.get(i).name);
		}
	}	
	

}
