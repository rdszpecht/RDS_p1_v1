import java.io.*;
import java.net.URL;
import java.nio.file.*;

public class FileDownloader {
	String path = "./downloads";
	
	public FileDownloader() {
		File dir = new File("downloads");

		if (!dir.exists()){
			dir.mkdir();
		}
	}


	
	public void process(String downloadsFileURL) throws IOException {
		int sepIndx = downloadsFileURL.lastIndexOf('/');
		String name = downloadsFileURL.substring(sepIndx);


		URL website = new URL(downloadsFileURL);
		InputStream in = website.openStream();
		Path pathOut = Paths.get(this.path + "/" + name);
		Files.copy(in, pathOut, StandardCopyOption.REPLACE_EXISTING);
		in.close();
	}
	
	public static void main(String[] args) throws IOException {
		String downloadFile = "https://github.com/jesussanchezoro/PracticaPC/raw/master/descargas.txt";
		FileDownloader fd = new FileDownloader();
		fd.process(downloadFile);

	}
}
