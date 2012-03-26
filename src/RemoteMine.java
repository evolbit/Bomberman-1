/**
 * class RemoteMine
 * 
 * @version 2007-11-07
 */
public class RemoteMine extends Mine
{
    private int set;
    public RemoteMine(int newX, int newY,int newRadius,int newSet){
        super(newX,newY,newRadius,null);
        imageCode = 38;
        countDown = 33;
        radius = newRadius;
        set = newSet;
    }
    public int getSet(){
        return set;   
    }
    public void setSet(int newSet){
        set = newSet;
    }
    public void trigger(boolean fast){
        if(fast)
            countDown = 12;
        grid.removeRemoteMine(this);
        grid.addExplosive(this);
        imageCode = 37;
    }
    public String toString(){
        if(radius != 2)
            return "Remote Mine Set "+set+" Radius "+radius;
        return "Remote Mine Set "+set;
    }
}
