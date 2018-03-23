import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws IOException {
        //String firstFileName = "descargas.txt";
        //String downloadFile = "https://github.com/jesussanchezoro/PracticaPC/raw/master/descargas.txt";
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type or paste the link with the file of download links for the parts:");
        String downloadFile = scanner.nextLine().split(" ")[0];
        String firstFileName = downloadFile.substring(downloadFile.lastIndexOf('/') + 1);

        FileDownloader fd = new FileDownloader(Runtime.getRuntime().availableProcessors());

        fd.process(downloadFile);
        Path path = fd.getPath("/" + firstFileName);

        List<String> donwloadLinks = fd.getDownloadsList(path);
        List<Element> elements = ringfanceElements(donwloadLinks);

        for (Element element: elements){
            fd.downloadParts(element);
        }

        SplitAndMerge sam = new SplitAndMerge();
        for (Element element: elements){
            sam.mergeFile(fd.getPath().toString(), element.getName());
        }

        clearDir(fd.getPath().toString(), firstFileName);
        System.out.println("\nProcess has been finished, check out: " + fd.getPath());
    }

    private static List<Element> ringfanceElements(List<String> links){
        List<Element> elements = new ArrayList<Element>();
        String name;
        Element element = null;
        for (String link: links) {
            if(link.contains("Fichero:")){
                if (element != null){
                    elements.add(element);
                }
                name = link.split(" ")[1];
                element = new Element(name);
            }else{
                element.addLink(link);
            }
        }
        elements.add(element);
        return elements;
    }

    private static void clearDir(String dir, String firstFileName){
        File directory = new File(dir);
        File[] files = directory.listFiles();
        Path path;

        for (File file: files){
            if ((file.getName().contains(".part")) || (file.getName().equals(firstFileName))){
                file.delete();
                System.out.println("Deleting: " + file.getAbsolutePath() + "\\" + file.getName());
            }
        }
    }
}
