/**
 * class LivesBooster
 * 
 * @version 2007-11-09
 */
public class LivesBooster extends Booster
{
    public LivesBooster(int newX, int newY){
        x = newX;
        y = newY;
        imageCode = 10;
    }
    public boolean usePowerup(Player player){
        player.setLivesLeft(player.getLivesLeft()+1);
        return true;
    }
    public String toString(){
        return "Lives Booster";   
    }
}
