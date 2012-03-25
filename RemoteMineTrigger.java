/**
 * class LandMineTrigger
 * 
 * @version 2007-12-07
 */
import java.util.ArrayList;
public class RemoteMineTrigger extends Powerup
{
    ArrayList<Integer>sets;
    public RemoteMineTrigger(int newX, int newY,ArrayList<Integer> newSets){
        x = newX;
        y = newY;
        sets = newSets;
        imageCode = 56;
    }
    public ArrayList<Integer> getSets(){
        return sets;   
    }
    public void setSets(ArrayList<Integer> newSets){
        sets = newSets;
    }
    public boolean usePowerup(Player p){
        // trigger all RemoteMines which have a set covered by this RemoteMineTriggers
        ArrayList <RemoteMine> remoteMines = grid.getRemoteMineArray();
        for(int ind = 0; ind < remoteMines.size(); ind++){
            if(sets.contains(remoteMines.get(ind).getSet())){
                remoteMines.get(ind).trigger(true);
                ind--;
            }
        }
        // remove all RemoteMineTriggers that only covered sets triggered by this RemoteMineTrigger
        ArrayList <RemoteMineTrigger> rmts = grid.getRemoteMineTriggerArray();
        for(int ind = 0; ind < rmts.size(); ind++){
            if(rmts.get(ind) != this){
                for(int set = 0; set < sets.size(); set++){
                    if(rmts.get(ind).getSets().contains(sets.get(set))){
                        rmts.get(ind).getSets().remove(rmts.get(ind).getSets().indexOf(sets.get(set)));
                        set--;
                    }
                }
                // remove RemoteMineTrigger if it doesn't cover any sets anymore
                if(rmts.get(ind).getSets().size() == 0)
                    grid.removeObject(rmts.get(ind));
            }
        }
        grid.removeRemoteMineTrigger(this);
        return true;
    }
    // cover and uncover are supposed to be like a trigger button with a cover protecting it
    // and it gets uncovered when you get close to it (are preparing to push it)
    public boolean cover(){ // return true if it was uncovered before
        if(imageCode != 56){
            imageCode = 56;
            return true;
        }
        return false;
    }
    public void uncover(){
        imageCode = 55;
    }
    public void highlight(){
        ArrayList <RemoteMine> remoteMines = grid.getRemoteMineArray();
        for(int ind = 0; ind < remoteMines.size(); ind++){
            if(sets.contains(remoteMines.get(ind).getSet()))
                remoteMines.get(ind).setImage(58);
        }
    }
    public void unhighlight(){
        ArrayList <RemoteMine> remoteMines = grid.getRemoteMineArray();
        for(int ind = 0; ind < remoteMines.size(); ind++){
            if(sets.contains(remoteMines.get(ind).getSet()))
                remoteMines.get(ind).setImage(38);
        }
    }
    public boolean radiusEmpty(){
        return(!(grid.getObjectAt(x,y) instanceof Player) &&
               !(grid.getObjectAt(x-1,y) instanceof Player) &&
               !(grid.getObjectAt(x+1,y) instanceof Player) &&
               !(grid.getObjectAt(x,y-1) instanceof Player) &&
               !(grid.getObjectAt(x,y+1) instanceof Player));
    }
    public boolean playerInRadius(Player p){
        return(grid.getObjectAt(x,y) == p ||
               grid.getObjectAt(x-1,y) == p ||
               grid.getObjectAt(x+1,y) == p ||
               grid.getObjectAt(x,y-1) == p ||
               grid.getObjectAt(x,y+1) == p);
    }
    public String toString(){
        return "Remote Mine Set(s) "+sets+" Trigger";   
    }
}
