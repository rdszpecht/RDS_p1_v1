import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class FileDownloader {
	String initialPath = "./downloads";
	int maxDonwloads;

	public FileDownloader(int maxDonwloads) {
		initDir();
		this.maxDonwloads = maxDonwloads;
	}

	public Path getPath(){
		return Paths.get(initialPath);
	}

	public Path getPath(String s){
	    return Paths.get(initialPath + s);
    }

	public List<String> getDownloadsList(Path path){
		List<String> dsList = new ArrayList<String>();
		try {
			dsList = Files.readAllLines(path);
		} catch (IOException e) {
			System.err.println("ERROR. Something gone wrong trying to read the list of download links from txt.");
		}
		return dsList;
	}

	// non concurrent
	/*
	public void downloadParts (Element element){
	    List<String> links = element.getLinkList();

	    for (String link: links){
            try {
                process(link);
            } catch (IOException e) {
                System.err.println("ERROR. Something gone wrong downloading some part of: " + element.getName());
            }
        }
    }
	*/

	public void downloadParts(List<Element> elements){
		for (Element element: elements){
			downloadElement(element);
		}
	}

	private void downloadElement(Element element){
		List<String> links = element.getLinkList();
		String link;
		int checksum = links.size();
		Semaphore semaphore = new Semaphore(1);
		int position = 0;

		while(position != checksum) {
			try {
				semaphore.acquire();

				int finalPosition = position;
				position++;

				new Thread(() -> {
					try {
						process(links.get(finalPosition));
					} catch (IOException e) {
						e.printStackTrace();
					}
				},"").run();

				semaphore.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Wait until all the elements are downloaded
		while(!allDonwloaded(element.getName(), checksum)){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Merge the element
		SplitAndMerge sam = new SplitAndMerge();
		sam.mergeFile(this.getPath().toString(), element.getName());
	}

	private boolean allDonwloaded(String name, int checksum){
		int downloadedParts = 0;
		File directory = new File("./downloads");
		File[] files = directory.listFiles();
		Path path;

		for (File file: files){
			if ((file.getName().contains(".part")) && (file.getName().contains(name))){
				downloadedParts++;
			}
		}

		return (downloadedParts == checksum);
	}

	protected void process(String downloadsFileURL) throws IOException {
		// name is the name of the file we are downloading, which means its the last part of the link
		int sepIndx = downloadsFileURL.lastIndexOf('/');
		String name = downloadsFileURL.substring(sepIndx);

		// And now it downloads the file and store it in our downloads directory
		URL website = new URL(downloadsFileURL);
		InputStream in = website.openStream();
		Path path = Paths.get(this.initialPath + "/" + name);

		Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
		in.close();
	}

	private static void initDir(){
		File dir = new File("downloads");
		if (!dir.exists()) {
			dir.mkdir();
		}
	}
}
