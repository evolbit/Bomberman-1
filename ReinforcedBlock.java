/**
 * class ReinforcedBlock
 * 
 * @version 2007-12-18
 */
public class ReinforcedBlock extends Block
{
    public ReinforcedBlock(int newX, int newY){
        x = newX;
        y = newY;
        imageCode = 5;
    }
    public boolean die(boolean force){
        grid.removeObject(this);
        return true;
    }
    public String toString(){
        return "Reinforced Block";   
    }
}
