/**
 * class UnstoppableBombBooster
 * 
 * @version 2007-11-11
 */
public class UnstoppableBombBooster extends Booster
{
    public UnstoppableBombBooster(int newX, int newY){
        x = newX;
        y = newY;
        imageCode = 33;
    }
    public boolean usePowerup(Player player){
        player.setExplosiveType(1);
        return true;
    }
    public String toString(){
        return "Unstoppable Bomb Upgrade";   
    }
}
