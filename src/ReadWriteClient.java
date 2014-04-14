import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ReadWriteClient {

	private static String filepath = "/home/shashank/workspace/index";
	private static int Size = 65536;
	private static String filename = "index";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReadWriteInterface rface;
		try {
			String serverAddress = args[1];
			String serverPort = args[2];
			filepath = args[0];
			filename = new File(filepath).getName();
			int port = Integer.parseInt(serverPort);
			Registry registry = LocateRegistry.getRegistry(serverAddress, port);
			rface = (ReadWriteInterface) registry.lookup("rmiserver");

			File towrite = new File(filepath);
			FileInputStream is = new FileInputStream(towrite);
			byte[] chunk = new byte[Size];
			int chunkLen = 0;
			int offset = 1;
			//System.out.println(filename + ":" + filepath);
			while ((chunkLen = is.read(chunk)) != -1) {
				byte chunkarray[] = new byte[chunkLen];

				for (int i = 0; i < chunkLen; i++)
					chunkarray[i] = chunk[i];
				rface.FileWrite64K(filename, offset, chunkarray);
				offset++;
			}
			is.close();

			long countchunks = rface.NumFileChunks(filename);
			File outdir = new File("./output");
			if (!outdir.exists()) {
				outdir.mkdirs();
			}
			if (countchunks >= 1) {
				File finalfile = new File("./output/" + filename);
				FileWriter fw = new FileWriter(finalfile);
				BufferedWriter bw = new BufferedWriter(fw);
				FileOutputStream fo = new FileOutputStream(finalfile);

				for (int i = 1; i <= countchunks; i++) {
					byte[] serverchunk = rface.FileRead64K(filename, i);
					/*
					 * char[] convertedChar = new char[serverchunk.length]; for
					 * (int j = 0; j < serverchunk.length; j++) {
					 * convertedChar[j] = (char) serverchunk[j]; }
					 */
					fo.write(serverchunk);
				}
				fo.close();
				bw.close();
				fw.close();
				System.out.println("Done");
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
