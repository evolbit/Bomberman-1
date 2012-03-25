/**
 * abstract class Block
 * 
 * @version 2007-11-28
 */
import java.awt.Image;
public abstract class Block implements Locatable
{
    protected Grid grid;
    protected int imageCode;
    protected int x,y;
    public abstract boolean die(boolean force); // returns true if Block was removed from Grid
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
