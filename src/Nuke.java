/**
 * class Nuke
 * 
 * @version 2007-11-14
 */
import java.util.ArrayList;
public class Nuke extends Explosive
{
    public Nuke(int newX, int newY,int newRadius, Player newOwner){
        x = newX;
        y = newY;
        player1Dead = false;
        player2Dead = false;
        owner = newOwner;
        // radius
        radius = newRadius / 2;
        if(radius < 2)
            radius = 2;
        // countDown
        countDown = owner.getExplosiveDuration()*33;
        imageCode = 40;
        deathBoosters = new ArrayList<DeathBooster>();
    }
    public void explode(){
        countDown = 0;

        explodeAt(x,y,0);
        
        // add a square of DeathBoosters around the nuke
        for(int row = y - radius + 1; row < y + radius; row++){
            for(int col = x - radius + 1; col < x + radius; col++){
                if(grid.isValidCoordinate(col,row))
                    explodeAt(col,row,0);
            }
        }
    }
    public void cleanup(){
        // if there are no more DeathBoosters to remove then stop keeping track of this Nuke
        if(deathBoosters.isEmpty())
            grid.removeExplosive(this);
        else{ // remove a random DeathBooster from this Nuke's explosion
            int rand = (int)(Math.random()*deathBoosters.size());
            while(!deathBoosters.isEmpty() && !(grid.getObjectAt(deathBoosters.get(rand).getX(),deathBoosters.get(rand).getY()) instanceof DeathBooster)){
                deathBoosters.remove(rand);
                rand = (int)(Math.random()*deathBoosters.size());
            }
            if(!deathBoosters.isEmpty()){
                grid.removeObjectAt(deathBoosters.get(rand).getX(),deathBoosters.get(rand).getY());
                deathBoosters.remove(rand);
            }
        }
        
    }
    public String toString(){
        return "Nuke";   
    }
}
