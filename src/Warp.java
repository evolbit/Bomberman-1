/**
 * class Warp
 * 
 * @version 2007-12-08
 */
public class Warp extends Powerup
{
    int targetX,targetY;
    public Warp(int newX, int newY,int newTargetX,int newTargetY){
        x = newX;
        y = newY;
        targetX = newTargetX;
        targetY = newTargetY;
        imageCode = 57;
    }
    public int getTargetX(){
        return targetX;   
    }
    public int getTargetY(){
        return targetY;   
    }
    public void setTargetX(int newTargetX){
        targetX = newTargetX;
    }
    public void setTargetY(int newTargetY){
        targetY = newTargetY;
    }
    public boolean usePowerup(Player p){
        if(grid.getObjectAt(targetX,targetY) instanceof GateBlock)
            ((GateBlock)grid.getObjectAt(targetX,targetY)).open();
        p.moveTo(targetX,targetY);
        return false;
    }
    public String toString(){
        return "Warp to ("+targetX+","+targetY+")";   
    }
}
