/**
 * class GateBlock
 * 
 * @version 2007-12-04
 */
public class GateBlock extends ReinforcedBlock
{
    private Player owner;
    public GateBlock(int newX, int newY,Player newOwner){
        super(newX,newY);
        imageCode = 53;
        owner = newOwner;
        if(owner != null)
            imageCode += owner.getNumber();
    }
    public void close(){ // closing Gate means adding it to the Grid
        if(!grid.containsObject(this))
            grid.addObject(this);
          
    }
    public void open(){ // opening Gate means removing it from the Grid
        if(grid.containsObject(this))
            grid.removeObject(this);
    }
    public boolean radiusEmpty(){
        // return true if there are no Players in neighboring spots
        return(!(grid.getObjectAt(x,y) instanceof Player) &&
               !(grid.getObjectAt(x-1,y) instanceof Player) &&
               !(grid.getObjectAt(x+1,y) instanceof Player) &&
               !(grid.getObjectAt(x,y-1) instanceof Player) &&
               !(grid.getObjectAt(x,y+1) instanceof Player));
    }
    public boolean playerInRadius(Player p){
        // return true if Player p is in a neighboring spot
        return(grid.getObjectAt(x,y) == p ||
               grid.getObjectAt(x-1,y) == p ||
               grid.getObjectAt(x+1,y) == p ||
               grid.getObjectAt(x,y-1) == p ||
               grid.getObjectAt(x,y+1) == p);
    }
    public boolean die(boolean force){
        grid.removeObject(this);
        grid.removeGateBlock(this);
        return true;
    }
    public Player getOwner(){
        return owner;   
    }
    public String toString(){
        if(owner.getNumber()==1)
            return "Player 1 Gate";
        else
            return "Player 2 Gate";
    }
}
