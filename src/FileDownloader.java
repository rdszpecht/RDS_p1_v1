import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FileDownloader {
	private final String initialPath = "./downloads";
	private int maxDonwloads;
	private List<Thread> downloaders = new ArrayList<Thread>();
	private LinkedBlockingDeque<String> links;
	private CyclicBarrier barrier;
	private CountDownLatch latch = new CountDownLatch(1);
	private SplitAndMerge sam;
	private Exchanger<LinkedBlockingDeque> exchanger;
	private final String FIRST_FILE_NAME = "descargas.txt";

	public FileDownloader(int maxDonwloads) {
		initDir();
		this.maxDonwloads = maxDonwloads;
		exchanger = new Exchanger();
		links = new LinkedBlockingDeque<String>();
		barrier = new CyclicBarrier(maxDonwloads, () ->{
			barrierCode();
		});
		sam = new SplitAndMerge();

		for (int i = 0; i < maxDonwloads; i++){
			Thread th = new Thread(new ThreadCode(barrier, latch, links));
			th.start();
			downloaders.add(th);
		}
	}

	public void initLinks(){
		List<String> listOfLinks =  getDownloadsList(this.getPath("/" + FIRST_FILE_NAME));
		String auxName = "";

		for (String line: listOfLinks){
			if (line.contains("Fichero: ")){
				if(!auxName.equals("")){
					links.offer("Fichero: " + auxName);
				}
				auxName = line.split(": ")[1];
			}else {
				links.offer(line);
			}
		}
		links.offer("Fichero: " + auxName);
	}

	protected Path getPath(){
		return Paths.get(initialPath);
	}

	protected Path getPath(String s){
	    return Paths.get(initialPath + s);
    }

    public String getInitialPath(){
		return this.initialPath;
	}

	protected List<String> getDownloadsList(Path path){
		List<String> dsList = new ArrayList<String>();
		try {
			dsList = Files.readAllLines(path);
		} catch (IOException e) {
			System.err.println("ERROR. Something gone wrong trying to read the list of download links from txt.");
		}
		return dsList;
	}

	protected void downloadAll(){
		latch.countDown();
	}

	private String barrierCode(){
		String name = links.poll().split(": ")[1];
		sam.mergeFile(this.getPath().toString(), name);
		System.out.println(name + " downloaded and merged");
		if(links.isEmpty()){
			clearDir(initialPath,FIRST_FILE_NAME);
		}
		return name;
	}

	private static void clearDir(String dir, String firstFileName){
		File directory = new File(dir);
		File[] files = directory.listFiles();
		Path path;
		System.out.println("Deleting reaming files...");
		for (File file: files){
			if ((file.getName().contains(".part")) || (file.getName().equals(firstFileName))){
				file.delete();
			}
		}
	}

	private static void initDir(){
		File dir = new File("downloads");
		if (!dir.exists()) {
			dir.mkdir();
		}
	}
}
