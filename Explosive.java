/**
 * abstract class Explosive 
 * 
 * @version 2007-11-10
 */
import java.util.ArrayList;
import java.awt.Image;
public abstract class Explosive implements Locatable
{
    protected int x,y,radius,countDown;
    protected Grid grid;
    protected int imageCode,explosiveDuration;
    protected Player owner;
    protected boolean player1Dead;
    protected boolean player2Dead;
    protected ArrayList<Fire> fires;
    protected ArrayList<DeathBooster> deathBoosters;
    public abstract void explode();
    public abstract String toString();
    public int getX(){
        return x;
    }
    public int getY(){
        return y;   
    }
    public void setX(int newX){
        x = newX;
    }
    public void setY(int newY){
        y = newY;
    }
    public boolean countDown(){ 
        // return true if countDown gets to zero
        countDown--;
        if(countDown == 0 && grid.getObjectAt(x,y) == this){ // explode explosive if countdown is at zero
            explode();
            return true;
        }else if(countDown == -8 && !(this instanceof Nuke)) // remove explosive and fire after this many ticks
            cleanup();
        else if(countDown < -30 && countDown % 30 == 0) // remove DeathBoosters randomly one by one at an interval of 30 for Nukes
            cleanup();
        return false;
    }
    public int getCountDown(){
        return countDown;   
    }
    public void setGrid(Grid newGrid){
        grid = newGrid;   
    }
    public int getRadius(){
        return radius;   
    }
    public void setRadius(int newRadius){
        radius = newRadius;
    }
    public Player getOwner(){
        return owner;   
    }
    public void setOwner(Player newOwner){
        owner = newOwner;   
    }
    public void explodeAt(int newX, int newY, int newDir){
        // This method will deal with the object at a Location that an Explosive is exloding at
        Locatable obj = grid.getObjectAt(newX,newY);
        if(obj instanceof Player){
            Player p = (Player)obj;
            // Store the current x and y of the player before he/she dies
            int pX = p.getX();
            int pY = p.getY();
            // Explode any Explosive that the Player is on
            if(p.getIsOnExplosive()){
                p.setIsOnExplosive(false);
                grid.getExplosiveAt(pX,pY).explode();
            }
            // Adjust Player 1 score depending on who died because of who
            if(!player1Dead && p.getNumber()==1){
                if(owner != null && owner.getNumber() != 1)
                    owner.setScore(owner.getScore()+100);
                else if(owner != null)
                    owner.setScore(owner.getScore()-250);
                player1Dead = true;
                // Kill the Player (moving him/her back to starting location)
                p.die();
            // Adjust Player 2 score depending on who died because of who
            }else if(!player2Dead && p.getNumber()==2){
                if(owner != null && owner.getNumber() != 2)
                    owner.setScore(owner.getScore()+100);
                else if(owner != null)
                    owner.setScore(owner.getScore()-250);
                player2Dead = true;
                // Kill the Player (moving him/her back to starting location)
                p.die();
            }
            // Replace Player with DeathBooster if Player is not back at his/her starting location
            if(this instanceof Nuke && (pX != p.getX() || pY != p.getY())){
                DeathBooster deathBooster = new DeathBooster(pX,pY);
                deathBooster.setGrid(grid);
                deathBoosters.add(deathBooster);
                grid.addObject(deathBooster);
            }
            // Update all the Explosives so they know whether or not they can kill each Player
            ArrayList<Explosive> explosives = grid.getExplosiveArray();
            for(int ind = 0; ind < explosives.size(); ind++){
                explosives.get(ind).setPlayer1Dead(player1Dead);
                explosives.get(ind).setPlayer2Dead(player2Dead);
            }
        }else if(obj instanceof Powerup){
            // if a center Fire is being removed or a Fire going in the opposite direction of the current one is being added,
            // then prepare to replace it with a new center Fire.
            if(obj instanceof Fire && (((Fire)obj).getDir() == 0 || ( ((Fire)obj).getDir() == 1 && newDir == 2) || ( ((Fire)obj).getDir() == 2 && newDir == 1)))
                newDir = 0;
            grid.removeObject(obj);
        }else if(obj instanceof Block){
            // force removal of SemidestructibleBlock if this is a (Land)Mine or Nuke
            boolean blockRemoved = ((Block)obj).die((this instanceof Mine || this instanceof Nuke));
            if(owner != null && obj instanceof SemidestructibleBlock)
                owner.setScore(owner.getScore()+5);
            else if(owner != null && obj instanceof DestructibleBlock)
                owner.setScore(owner.getScore()+10);
            else if(owner != null && obj instanceof GateBlock)
                owner.setScore(owner.getScore()+15);
            else if(owner != null && obj instanceof IndestructibleBlock)
                owner.setScore(owner.getScore()+20);
            if(blockRemoved && Math.random() < .4) // 40% chance of Powerup appearing
                grid.addRandomPowerupTo(newX,newY);
        }else if(obj instanceof Explosive && obj != this){ // dealing with another Explosive
            if(!(obj instanceof ProximityMine) && !(obj instanceof RemoteMine))
                ((Explosive)obj).explode(); // explode an Explosive in the explosion path
            else if(obj instanceof ProximityMine && grid.containsProximityMine((ProximityMine)obj))
                ((ProximityMine)obj).trigger(true); // fast trigger of LandMine
            else if(obj instanceof RemoteMine && grid.containsRemoteMine((RemoteMine)obj))
                ((RemoteMine)obj).trigger(true); // fast trigger of RemoteMine  
        }
        if(obj == this || grid.getObjectAt(newX,newY) == null){ // dealing with empty space or this Explosive
            if(obj == this) // remove self
                grid.removeObject(this);
            if(this instanceof Nuke){ // add DeathBooster in place of Nuke
                DeathBooster deathBooster = new DeathBooster(newX,newY);
                deathBooster.setGrid(grid);
                deathBoosters.add(deathBooster);
                grid.addObject(deathBooster);
            }else{ // add Fire in place of other Explosives or empty space
                Fire fire = new Fire(newX,newY,newDir);
                fire.setGrid(grid);
                fires.add(fire);
                grid.addObject(fire);
            }
        }
    }
    public void cleanup(){
        // remove all Fire from grid and fires array
        while(!fires.isEmpty()){
            if(grid.getObjectAt(fires.get(0).getX(),fires.get(0).getY()) instanceof Fire)
                grid.removeObjectAt(fires.get(0).getX(),fires.get(0).getY());
            fires.remove(fires.get(0));
        }
        grid.removeExplosive(this);            
    } 
    public Image getImage(){
        return grid.getImage(imageCode);
    }
    public void setImage(int newImageCode){
        imageCode = newImageCode;   
    }
    public void setPlayer1Dead(boolean newDead){
        player1Dead = newDead;   
    }
    public void setPlayer2Dead(boolean newDead){
        player2Dead = newDead;   
    }
    public void setExplosiveDuration(int newExplosiveDuration){
        explosiveDuration = newExplosiveDuration;   
    }
}
