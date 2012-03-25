/**
 * class SemidestructibleBlock
 * 
 * @version 2007-11-06
 */
public class SemidestructibleBlock extends Block
{
    private int deathsLeft;
    public SemidestructibleBlock(int newX, int newY){
        x = newX;
        y = newY;
        imageCode = 45;
        deathsLeft = 3;
    }
    public boolean die(boolean force){
        deathsLeft -= 1;
        imageCode++;
        if(deathsLeft == 0 || force){
            grid.removeObject(this);
            return true;
        }
        return false;
    }
    public int getDeathsLeft(){
        return deathsLeft;   
    }
    public String toString(){
        if(deathsLeft < 3)
            return "Damaged Semidestructable Block";
        return "Semidestructible Block";   
    }
}
