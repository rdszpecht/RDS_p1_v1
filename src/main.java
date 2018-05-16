import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws IOException {
        // "https://github.com/jesussanchezoro/PracticaPC/raw/master/descargas.txt";
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type or paste the link with the file of download links for the parts:");
        String listOfLinks = scanner.nextLine().split(" ")[0];
        String firstFileName = listOfLinks.substring(listOfLinks.lastIndexOf('/') + 1);

        FileDownloader fd = new FileDownloader(Runtime.getRuntime().availableProcessors());

        Process.process(listOfLinks, fd.getInitialPath());

        Path path = fd.getPath("/" + firstFileName);

        List<String> donwloadLinks = fd.getDownloadsList(path);

        fd.initLinks();
        fd.downloadAll();
    }

}
