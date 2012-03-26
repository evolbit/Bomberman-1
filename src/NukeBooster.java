/**
 * class NukeBooster
 * 
 * @version 2007-11-14
 */
public class NukeBooster extends Booster
{
    public NukeBooster(int newX, int newY){
        x = newX;
        y = newY;
        imageCode = 41;
    }
    public boolean usePowerup(Player player){
        player.setExplosiveType(3);
        return true;
    }
    public String toString(){
        return "Nuke Upgrade";   
    }
}
