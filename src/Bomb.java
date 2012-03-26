/**
 * class Bomb
 * 
 * @version 2007-11-10
 */
import java.util.ArrayList;
public class Bomb extends Explosive
{
    public Bomb(int newX, int newY,int newRadius, Player newOwner){
        x = newX;
        y = newY;
        player1Dead = false;
        player2Dead = false;
        owner = newOwner;
        radius = newRadius;
        countDown = owner.getExplosiveDuration()*33;
        imageCode = 6;
        fires = new ArrayList<Fire>();
    }
    public void explode(){
        boolean endUpperCol = false;
        boolean endLowerCol = false;
        boolean endLeftRow = false;
        boolean endRightRow = false;
        countDown = 0;

        explodeAt(x,y,0);
        // add fire in each direction (stop when an object is reached)
        for(int ind = 1; ind < radius; ind++){
            if(!endUpperCol && grid.isValidCoordinate(x,y-ind)){
                if(grid.getObjectAt(x,y-ind) != null)
                    endUpperCol = true; // encountered an object so stop going this direction
                if(!(grid.getObjectAt(x,y-ind) instanceof ReinforcedBlock) && !(grid.getObjectAt(x,y-ind) instanceof Warp))
                    explodeAt(x,y-ind,2); // explode if not at ReinforcedBlock
            }
            if(!endLowerCol && grid.isValidCoordinate(x,y+ind)){
                if(grid.getObjectAt(x,y+ind) != null)
                    endLowerCol = true;
                if(!(grid.getObjectAt(x,y+ind) instanceof ReinforcedBlock) && !(grid.getObjectAt(x,y+ind) instanceof Warp))
                    explodeAt(x,y+ind,2);        
            }
            if(!endLeftRow && grid.isValidCoordinate(x-ind,y)){
                if(grid.getObjectAt(x-ind,y) != null)
                    endLeftRow = true;
                if(!(grid.getObjectAt(x-ind,y) instanceof ReinforcedBlock) && !(grid.getObjectAt(x-ind,y) instanceof Warp))
                    explodeAt(x-ind,y,1);
            }
            if(!endRightRow && grid.isValidCoordinate(x+ind,y)){
                if(grid.getObjectAt(x+ind,y) != null)
                    endRightRow = true;
                if(!(grid.getObjectAt(x+ind,y) instanceof ReinforcedBlock) && !(grid.getObjectAt(x+ind,y) instanceof Warp))
                    explodeAt(x+ind,y,1); 
            }
        }
    }
    public String toString(){
        return "Bomb";   
    }
}
