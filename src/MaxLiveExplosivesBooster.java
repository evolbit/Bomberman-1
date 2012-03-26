/**
 * class MaxLiveExplosivesBooster
 * 
 * @version 2007-11-08
 */
public class MaxLiveExplosivesBooster extends Booster
{
    public MaxLiveExplosivesBooster(int newX, int newY){
        x = newX;
        y = newY;
        imageCode = 9;
    }
    public boolean usePowerup(Player player){
        player.setMaxLiveExplosives(player.getMaxLiveExplosives()+1);
        return true;
    }
    public String toString(){
        return "Explosive Count Booster";   
    }
}
