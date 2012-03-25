/**
 * class Server
 * 
 * @version 2008-01-07
 */
import java.util.ArrayList;
import java.util.Arrays;
public class Server
{
    // instance variables
    private int sid,lid; // server id, level id
    private String name,hostAlias,remoteAlias,localAlias;
    
    // constructors
    public Server()
    {
        init(0,0,"","","","");
    }
    public Server(String line,String newLocalAlias)
    {
        // split the line by /'s
        ArrayList<String>parts = new ArrayList<String>(Arrays.asList(line.split("/")));
        if(parts.size() == 5)
            init(Integer.parseInt(parts.get(0)),Integer.parseInt(parts.get(1)),parts.get(4).trim(),parts.get(2).trim(),parts.get(3).trim(),newLocalAlias);
        else
            init(0,0,"","","","");
    }
    public Server(int newLID,String newName,String newHostAlias,String newRemoteAlias,String newLocalAlias)
    {
        int newSID = 0;
        // create a server
        ArrayList<String>lines = BombermanPanel.getLineArrayFromAddr(BombermanPanel.webPath+"query.php?action=new_server&lid="+newLID+"&name="+BombermanPanel.encURL(newName)+"&hostAlias="+BombermanPanel.encURL(newLocalAlias)+"&hostCode="+ServerPanel.localCode);
        if(lines != null && lines.size() > 0 && !lines.get(0).equals("0"))
            newSID = Integer.parseInt(lines.get(0));
        init(newSID,newLID,newName,newHostAlias,newRemoteAlias,newLocalAlias);
    }
    private void init(int newSID,int newLID,String newName,String newHostAlias,String newRemoteAlias,String newLocalAlias)
    {
        sid = newSID;
        lid = newLID;
        name = newName;
        hostAlias = newHostAlias;
        remoteAlias = newRemoteAlias;
        localAlias = newLocalAlias;
    }
    
    // server methods
    public boolean join(){
        ArrayList<String>lines = BombermanPanel.getLineArrayFromAddr(BombermanPanel.webPath+"query.php?action=join_server&sid="+sid+"&remoteAlias="+BombermanPanel.encURL(localAlias)+"&remoteCode="+ServerPanel.localCode);
        if(lines != null && lines.size() > 0 && lines.get(0).equals("1")){
            remoteAlias = localAlias;
            return true;
        }
        return false;
    }
    public int leave(){
        // return 2 if user was host and server was deleted
        // return 1 if user was remote and left server
        // return 0 upon other failure
        ArrayList<String>lines = BombermanPanel.getLineArrayFromAddr(BombermanPanel.webPath+"query.php?action=leave_server&sid="+sid+"&localAlias="+BombermanPanel.encURL(localAlias)+"&localCode="+ServerPanel.localCode);
        if(lines != null && lines.size() > 0){
            int resp = Integer.parseInt(lines.get(0));
            if(resp == 1)
                remoteAlias = "";
            return resp;
        }
        return 0;
    }
    public boolean updateAlias(){
        ArrayList<String>lines = BombermanPanel.getLineArrayFromAddr(BombermanPanel.webPath+"query.php?action=update_alias&sid="+sid+"&localAlias="+BombermanPanel.encURL(localAlias)+"&localCode="+ServerPanel.localCode);   
        if(lines != null && lines.size() > 0 && !lines.get(0).equals("0")){
            if(Integer.parseInt(lines.get(0)) == 2)
                hostAlias = localAlias;
            else
                remoteAlias = localAlias;
            return true;
        }
        return false;
    }
    
    // accessors
    public int getSID(){
        return sid;
    }
    public int getLID(){
        return lid;
    }
    public String getName(){
        return name;   
    }
    public String getHostAlias(){
        return hostAlias;   
    }
    public String getRemoteAlias(){
        return remoteAlias;   
    }
}