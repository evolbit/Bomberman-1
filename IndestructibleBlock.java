/**
 * class IndestructibleBlock
 * 
 * @version 2007-11-06
 */
public class IndestructibleBlock extends Block
{
    public IndestructibleBlock(int newX, int newY){
        x = newX;
        y = newY;
        imageCode = 59;
    }
    public boolean die(boolean force){
        return false;
    }
    public String toString(){
        return "Indestructible Block";   
    }
}
