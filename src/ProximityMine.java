/**
 * class ProximityMine
 * 
 * @version 2007-11-10
 */
public class ProximityMine extends Mine
{ 
    public ProximityMine(int newX, int newY,int newRadius){
        super(newX,newY,newRadius,null);
        imageCode = 48;
        countDown = 33;
        radius = newRadius;
    }
    public void trigger(boolean fast){
        if(fast)
            countDown = 12;
        grid.removeProximityMine(this);
        grid.addExplosive(this);
        imageCode = 37;
    }
    public String toString(){
        if(radius != 2)
            return "Proximity Mine Radius "+radius;
        return "Proximity Mine";
    }
}
