/**
 * class DeathBooster
 * 
 * @version 2007-11-11
 */
public class DeathBooster extends Booster
{
    public DeathBooster(int newX, int newY){
        x = newX;
        y = newY;
        imageCode = 39;
    }
    public boolean usePowerup(Player player){
        player.setLivesLeft(player.getLivesLeft()-1);
        return true;
    }
    public String toString(){
        return "Death Booster";   
    }
}
