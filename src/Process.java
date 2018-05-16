import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public abstract class Process {

    public static void process(String downloadFileURL, String initialPath){
        try {
            System.out.println("Downloading: " + downloadFileURL);
            int sepIndx = downloadFileURL.lastIndexOf('/');
            String name = downloadFileURL.substring(sepIndx);

            URL website = new URL(downloadFileURL);
            InputStream in = website.openStream();
            Path path = Paths.get(initialPath + "/" + name);

            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            in.close();
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e2){
            e2.printStackTrace();
        }
    }
}
