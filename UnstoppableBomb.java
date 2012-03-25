/**
 * class UnstoppableBomb
 * 
 * @version 2007-11-10
 */
import java.util.ArrayList;
public class UnstoppableBomb extends Explosive
{
    public UnstoppableBomb(int newX, int newY,int newRadius, Player newOwner){
        x = newX;
        y = newY;
        player1Dead = false;
        player2Dead = false;
        owner = newOwner;
        radius = newRadius;
        countDown = owner.getExplosiveDuration()*33;
        imageCode = 34;
        fires = new ArrayList<Fire>();
    }
    public void explode(){
        countDown = 0;
        
        explodeAt(x,y,0);
        
        // find which way is longest so the fire goes for the whole grid in each direction
        int longestDirection = grid.getHeight();
        if(grid.getWidth() > longestDirection)
            longestDirection = grid.getWidth();
            
        // add fire in each direction (to every side of the Grid)
        for(int ind = 1; ind < longestDirection; ind++){
            if(grid.isValidCoordinate(x,y-ind) && !(grid.getObjectAt(x,y-ind) instanceof ReinforcedBlock) && !(grid.getObjectAt(x,y-ind) instanceof Warp))   
                explodeAt(x,y-ind,2);
            if(grid.isValidCoordinate(x,y+ind) && !(grid.getObjectAt(x,y+ind) instanceof ReinforcedBlock) && !(grid.getObjectAt(x,y+ind) instanceof Warp))    
                explodeAt(x,y+ind,2);
            if(grid.isValidCoordinate(x-ind,y) && !(grid.getObjectAt(x-ind,y) instanceof ReinforcedBlock) && !(grid.getObjectAt(x-ind,y) instanceof Warp))   
                explodeAt(x-ind,y,1);
            if(grid.isValidCoordinate(x+ind,y) && !(grid.getObjectAt(x+ind,y) instanceof ReinforcedBlock) && !(grid.getObjectAt(x+ind,y) instanceof Warp))    
                explodeAt(x+ind,y,1);
        }
    }
    public String toString(){
        return "Unstoppable Bomb";   
    }
}
