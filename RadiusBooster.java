/**
 * class MaxLiveBombsBooster
 * 
 * @version 2007-11-08
 */
public class RadiusBooster extends Booster
{
    public RadiusBooster(int newX, int newY){
        x = newX;
        y = newY;
        imageCode = 8;
    }
    public boolean usePowerup(Player player){
        player.setExplosiveRadius(player.getExplosiveRadius()+1);
        return true;
    }
    public String toString(){
        return "Radius Booster";   
    }
}
