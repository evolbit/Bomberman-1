/**
 * class Mine
 * 
 * @version 2007-11-10
 */
import java.util.ArrayList;
public class Mine extends Explosive
{
    public Mine(int newX, int newY,int newRadius, Player newOwner){
        x = newX;
        y = newY;
        player1Dead = false;
        player2Dead = false;
        owner = newOwner;
        // radius
        radius = (int)(newRadius / 1.5);
        if(radius < 2)
            radius = 2;
        // countDown
        if(owner != null)
            countDown = owner.getExplosiveDuration()*33;
        imageCode = 37;
        fires = new ArrayList<Fire>();
    }
    public void explode(){
        countDown = 0;
        
        explodeAt(x,y,0);
        
        // add a square of fire around the Mine
        for(int row = y - radius + 1; row < y + radius; row++){
            for(int col = x - radius + 1; col < x + radius; col++){
                if(grid.isValidCoordinate(col,row))
                    explodeAt(col,row,0);
            }
        }
    }
    public String toString(){
        return "Mine";   
    }
}
