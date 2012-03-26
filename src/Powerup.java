/**
 * abstract class Powerup
 * 
 * @version 2007-11-08
 */
import java.awt.Image;
public abstract class Powerup implements Locatable
{
    protected Grid grid;
    protected int imageCode;
    protected int x,y;
    // usePowerup() returns true if Powerup gets used up,
    // (if the player moves on to it actually)
    public abstract boolean usePowerup(Player p);
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
    public Image getImage(){
        return grid.getImage(imageCode);
    }
    public void setImage(int newImageCode){
        imageCode = newImageCode;   
    }
    public void setGrid(Grid newGrid){
        grid = newGrid;   
    }
}
