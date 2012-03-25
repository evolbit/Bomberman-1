/**
 * class DestructibleBlock
 * 
 * @version 2007-11-06
 */
public class DestructibleBlock extends Block
{
    public DestructibleBlock(int newX, int newY){
        x = newX;
        y = newY;
        imageCode = 4;
    }
    public boolean die(boolean force){
        grid.removeObject(this);
        return true;
    }
    public String toString(){
        return "Destructible Block";   
    }
}
