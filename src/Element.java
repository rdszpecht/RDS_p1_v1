import java.util.ArrayList;
import java.util.List;

public class Element {
    private String name;
    private List<String> linkList;

    public Element(String name){
        this.name = name;
        linkList = new ArrayList<String>();
    }

    public Element(String name, List linkList){
        this.name = name;
        this.linkList = linkList;
    }

    public String getName(){
        return this.name;
    }

    public void addLink(String link){
        linkList.add(link);
    }

    public List<String> getLinkList(){
        return this.linkList;
    }

}
