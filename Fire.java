/**
 * class Fire 
 * 
 * @version 2007-11-06
 */
import java.awt.Image;
public class Fire extends Powerup
{
    private int dir;
    public Fire(int newX, int newY,int newDir){
        dir = newDir;
        x = newX;
        y = newY;
        if (dir==1)
            imageCode = 21;
        else if (dir==2)
            imageCode = 22;
        else
            imageCode = 20;
    }
    public int getDir(){
        return dir;   
    }
    public boolean usePowerup(Player p){
        p.die();
        return false;
    }
    public String toString(){
        if(dir == 1)
            return "Horizontal Fire";
        else if(dir == 2)
            return "Vertical Fire";
        else // dir == 0
            return "Center Fire";
    }
}
