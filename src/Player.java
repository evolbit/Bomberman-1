/**
 * class Player
 * 
 * @version 2007-11-06
 */
import java.awt.Image;
import java.util.ArrayList;
public class Player implements Locatable
{
    private Grid grid;
    private int number,x,y;
    private int startingX,startingY;
    private int score,explosiveRadius,explosiveType,maxLiveExplosives,livesLeft,explosiveDuration;
    private int defaultExplosiveDuration,defaultExplosiveType,defaultExplosiveRadius,defaultMaxLiveExplosives;
    private boolean isOnExplosive;
    private Image image;
    private int imageCode;
    public Player (int newNumber){
        x=-1;y=-1;
        startingX=-1;startingY=-1;
        number = newNumber;
        imageCode = number * 14; // player1 = 14; player2 = 28
        livesLeft = 5;
        // set defaults
        defaultExplosiveType = 0;
        defaultExplosiveRadius = 2;
        defaultExplosiveDuration = 3;
        defaultMaxLiveExplosives = 1;
        // use defaults
        useDefaults();
    }
    private void useDefaults(){
        explosiveType = defaultExplosiveType;
        explosiveRadius = defaultExplosiveRadius;
        explosiveDuration = defaultExplosiveDuration;
        maxLiveExplosives = defaultMaxLiveExplosives;
        isOnExplosive = false;
    }
    public void setGrid(Grid newGrid){
        grid = newGrid;   
    }
    public int getNumber(){
        return number;   
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;   
    }
    public int getScore(){
        return score;   
    }
    public void setScore(int newScore){
        score = newScore;   
    }
    public void setX(int newX){
        x = newX;        
    }
    public void setY(int newY){
        y = newY;
    }
    public void setStartingX(int newStartingX){
        startingX = newStartingX;
    }
    public void setStartingY(int newStartingY){
        startingY = newStartingY;
    }
    public int getStartingX(){
        return startingX;
    }
    public int getStartingY(){
        return startingY;
    }
    public void plantExplosive(){
        grid.reloadBombAnimations();
        // create Explosive at player's current location
        Explosive explosive = null;
        if(explosiveType == 0)
            explosive = new Bomb(x,y,explosiveRadius,this);
        else if(explosiveType == 1)
            explosive = new UnstoppableBomb(x,y,explosiveRadius,this);
        else if(explosiveType == 2)
            explosive = new Mine(x,y,explosiveRadius,this);
        else if(explosiveType == 3)
            explosive = new Nuke(x,y,explosiveRadius,this);
        explosive.setGrid(grid);
        explosive.setExplosiveDuration(explosiveDuration);
        grid.addExplosive(explosive);
        isOnExplosive = true;
    }
    public void setExplosiveType(int newExplosiveType){
        explosiveType = newExplosiveType;
    }
    public int getExplosiveType(){
        return explosiveType;   
    }
    public int getExplosiveDuration(){
        return explosiveDuration;   
    }
    public int getMaxLiveExplosives(){
        return maxLiveExplosives;   
    }
    public void setMaxLiveExplosives(int newMaxLiveExplosives){
        maxLiveExplosives = newMaxLiveExplosives;   
    }
    public boolean getIsOnExplosive(){
        return isOnExplosive;   
    }
    public void setIsOnExplosive(boolean newIsOnExplosive){
        isOnExplosive = newIsOnExplosive;   
    }
    public int getLivesLeft(){
        return livesLeft;   
    }
    public void setLivesLeft(int newLivesLeft){
        livesLeft = newLivesLeft;   
    }
    public int getExplosiveRadius(){
        return explosiveRadius;   
    }
    public void setExplosiveRadius(int newExplosiveRadius){
        explosiveRadius = newExplosiveRadius;   
    }
    public void setExplosiveDuration(int newExplosiveDuration){
        explosiveDuration = newExplosiveDuration;   
    }
    public void die(){
        // decrement lives and exit if none left
        livesLeft--;
        if(livesLeft == 0)
            return;
        // restore default image
        if(number == 1)
            setImage(14);
        else if(number == 2)
            setImage(28);
        // set powerups back to default
        useDefaults();
        // remove any powerup at the starting location
        if(grid.getObjectAt(startingX,startingY) instanceof Powerup)
            grid.removeObjectAt(startingX,startingY);
        // move to starting location
        moveTo(startingX,startingY);
    }
    public void moveTo(int newX,int newY){
        int oldX = x;
        int oldY = y;
        grid.moveObjectTo(this,newX,newY);
        // player moved off an Explosive that he/she just planted so add the Explosive now
        if(isOnExplosive){
            grid.addObject(grid.getExplosiveAt(oldX,oldY));
            isOnExplosive = false;
        }
        // deal with any ProximityMines in the vicinity (set them off)
        ArrayList <ProximityMine> proxMines = grid.getProxMineArray();
        for(int ind = 0; ind < proxMines.size(); ind++){
            ProximityMine pm = proxMines.get(ind);
            int pmx = pm.getX();
            int pmy = pm.getY();
            int pmr = pm.getRadius();
            // check for any ProximityMines that he/she moved into the radius of and trigger them
            if(x > pmx - pmr && x < pmx + pmr && y > pmy - pmr && y < pmy + pmr){
                pm.trigger(false);
                ind--; // necessary because trigger() removes the ProximityMine from proxMines
                       // and thus will miss the next ProximityMine in the proxMines unless
                       // you decrement ind to start at the same location again.
            }
        }
        // deal with RemoteMineTriggers in the vicinity (cover or uncover them)
        ArrayList <RemoteMineTrigger> rmts = grid.getRemoteMineTriggerArray();
        for(int ind = 0; ind < rmts.size(); ind++){
            RemoteMineTrigger rmt = rmts.get(ind);
            if(rmt.playerInRadius(this)){
                rmt.uncover();
                rmt.highlight();
            }else if(rmt.radiusEmpty()){
                if(rmt.cover())
                    rmt.unhighlight();
            }
            // remove rmt if it is no longer in the Grid (it was destroyed by an Explosive)
            if(grid.getObjectAt(rmt.getX(),rmt.getY()) != rmt)
                grid.removeRemoteMineTrigger(rmt);
        }
        // deal with GateBlocks in the vicinity (open or close them)
        ArrayList <GateBlock> gateBlocks = grid.getGateBlockArray();
        for(int ind = 0; ind < gateBlocks.size(); ind++){
            GateBlock gb = gateBlocks.get(ind);
            // check for any GateBlocks that he/she moved into the radius of and open them
            if((gb.getOwner() == this || gb.getOwner() == null) && gb.playerInRadius(this))
                gb.open();
            // check the neighboring spots of this GateBlock and close it if there are no Players there
            else if(gb.radiusEmpty())
                gb.close();
        }
    }
    public Image getImage(){
        return grid.getImage(imageCode);
    }
    public void setImage(int newImageCode){
        imageCode = newImageCode;   
    }
    public void setDefaultExplosiveRadius(int newDefaultExplosiveRadius){
        defaultExplosiveRadius = newDefaultExplosiveRadius;   
    }
    public void setDefaultMaxLiveExplosives(int newDefaultMaxLiveExplosives){
        defaultMaxLiveExplosives = newDefaultMaxLiveExplosives;
    }
    public void setDefaultExplosiveType(int newDefaultExplosiveType){
        defaultExplosiveType = newDefaultExplosiveType;   
    }
    public void setDefaultExplosiveDuration(int newDefaultExplosiveDuration){
        defaultExplosiveDuration = newDefaultExplosiveDuration;   
    }
    public String toString(){
        return "Player "+number;   
    }
    
}