

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class ReadWriteServer extends UnicastRemoteObject implements
		ReadWriteInterface {

	protected ReadWriteServer() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int FileWrite64K(String filename, long offset, byte[] data)
			throws IOException, RemoteException {
		// TODO Auto-generated method stub
		try {
			if(data.length<65536)
				return -1;
			System.out.println(filename);
			File towrite = new File(filename);
			if (!towrite.isDirectory()) {
				towrite.mkdirs();
			}
			String dirpath = filename + "/";
			File chunkwrite = new File(dirpath + "chunk" + offset);
			FileOutputStream os = new FileOutputStream(chunkwrite);
			FileWriter fw = new FileWriter(chunkwrite);
			BufferedWriter bw = new BufferedWriter(fw);
			/*char[] convertedChar = new char[data.length];
			for (int i = 0; i < data.length; i++) {
				convertedChar[i] = (char) data[i];
			}
			// bw.write(data.toString());
			bw.write(convertedChar);*/
			os.write(data);
			os.close();
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data.length;
	}

	@Override
	public long NumFileChunks(String filename) throws IOException,
			RemoteException {
		File file =new File(filename);
		long length=0;
		if(file.exists())
			length=file.listFiles().length;
		return length;
	}

	@Override
	public byte[] FileRead64K(String filename, long offset) throws IOException,
			RemoteException {
		File towrite=new File(filename+"/chunk"+offset);
		FileInputStream is = new FileInputStream(towrite);
	    byte[] chunk = new byte[65536];
	    int len=is.read(chunk);
	    byte chunkarray[]=new byte[len];
    	for(int i=0;i<len;i++)
    		chunkarray[i]=chunk[i];
	    is.close();
	    //System.out.println(chunkarray.length);
		return chunkarray;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ReadWriteInterface rInterface = new ReadWriteServer();
			int portnum=Integer.parseInt(args[0]);
			Registry registry = LocateRegistry.createRegistry(portnum);
			registry.bind("rmiserver", rInterface);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
