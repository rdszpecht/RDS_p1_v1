import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

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

	public void process(String downloadsFileURL) throws IOException {
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
