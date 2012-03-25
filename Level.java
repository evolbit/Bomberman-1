/**
 * class Level
 * 
 * @version 2008-01-11
 */
import java.util.ArrayList;
import java.util.Arrays;
public class Level
{
    // instance variables
    private int lid; // level id
    private String file,name,author,email,desc;
    
    // constructors
    public Level()
    {
        init(0,"","","","","");
    } 
    public Level(String line)
    {
        // split the line by /'s
        ArrayList<String>parts = new ArrayList<String>(Arrays.asList(line.split("/")));
        if(parts.size() == 6)
            init(Integer.parseInt(parts.get(0)),parts.get(1),parts.get(2).trim(),parts.get(3).trim(),parts.get(4).trim(),parts.get(5).trim());
        else
            init(0,"","","","","");
    }
    public Level(String newFile,String newName,String newAuthor,String newEmail,String newDesc)
    {
        int newLID = 0;
        // create a level
        ArrayList<String>lines = BombermanPanel.getLineArrayFromAddr(BombermanPanel.webPath+"query.php?action=new_level&file="+BombermanPanel.encURL(newFile)+"&name="+BombermanPanel.encURL(newName)+"&author="+BombermanPanel.encURL(author)+"&email="+BombermanPanel.encURL(email)+"&desc="+BombermanPanel.encURL(desc));
        if(lines != null && lines.size() > 0 && !lines.get(0).equals("0"))
            newLID = Integer.parseInt(lines.get(0));
        init(newLID,newFile,newName,newAuthor,newEmail,newDesc);
    }
    private void init(int newLID,String newFile,String newName,String newAuthor,String newEmail,String newDesc)
    {
        lid = newLID;
        file = newFile;
        name = newName;
        author = newAuthor;
        email = newEmail;
        desc = newDesc;
    }
    
    // accessors
    public int getLID(){
        return lid;
    }
    public String getFile(){
        return file;   
    }
    public String getName(){
        return name;   
    }
    public String getAuthor(){
        return author;   
    }
    public String getEmail(){
        return email;  
    }
    public String getDesc(){
        return desc;   
    }
}