/**
 * class MineBooster
 * 
 * @version 2007-11-11
 */
public class MineBooster extends Booster
{
    public MineBooster(int newX, int newY){
        x = newX;
        y = newY;
        imageCode = 36;
    }
    public boolean usePowerup(Player player){
        player.setExplosiveType(2);
        return true;
    }
    public String toString(){
        return "Mine Upgrade";   
    }
}
