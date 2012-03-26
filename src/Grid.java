/**
 * class Grid
 * 
 * @version 2007-11-06
 */
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.awt.*; 
import java.net.URL;
import javax.swing.*;
public class Grid
{
   private ArrayList<ArrayList<Locatable>> objects;
   private ArrayList<Explosive> explosives;
   private ArrayList<ProximityMine> proxMines;
   private ArrayList<RemoteMine> remoteMines;
   private ArrayList<RemoteMineTrigger> remoteMineTriggers;
   private ArrayList<GateBlock> gateBlocks;
   private ArrayList<Warp> warps;
   private ArrayList<Image> images;
   private int nbrObjects,nbrBoosters,nbrDestructibleBlocks;
   private Player player1,player2;
   // Constructors
   public Grid(int rows,int cols){ 
        preloadImages();
        // initialize arrays
        objects = getEmptyGrid(rows,cols);
        explosives = new ArrayList<Explosive>();
        proxMines = new ArrayList<ProximityMine>();
        remoteMines = new ArrayList<RemoteMine>();
        remoteMineTriggers = new ArrayList<RemoteMineTrigger>();
        gateBlocks = new ArrayList<GateBlock>();
        warps = new ArrayList<Warp>();
        // initialize counter vars
        nbrObjects = 0;
        nbrBoosters = 0;
        nbrDestructibleBlocks = 0;
        // create players
        player1 = new Player(1);
        player2 = new Player(2);
   }
   public Grid(File file){ // load in grid from data file
        this(0,0);      
        int row = 0;
        Scanner sc = getScannerFromFile(file);
        while(sc.hasNextLine()){
            // split the current row apart at each space
            String [] colsArr;
            try{
                colsArr = sc.nextLine().split(" ");
            }catch(Exception e){
                System.out.println("Bad syntax in level file: "+file.getPath()+" at row "+row+".");
                return;
            }
            // loop through each column
            ArrayList<Locatable> cols = new ArrayList<Locatable>(colsArr.length);
            for(int col = 0; col < colsArr.length; col++){
                // get number of object at this location
                int obj = 0;
                int pos = colsArr[col].indexOf("-");
                if(pos == -1)
                    pos = colsArr[col].length();
                try{
                    obj = Integer.parseInt(colsArr[col].substring(0,pos));
                }catch(Exception e){
                    System.out.println("Bad syntax in data file: "+file.getPath()+" at ("+col+","+row+").");
                    return;
                }
                // get array of attributes for the object at this location
                String [] strAttrs = null;
                if(colsArr[col].contains("-")){
                    try{
                        strAttrs = colsArr[col].substring(colsArr[col].indexOf("-")+1,colsArr[col].length()).split(",");
                    }catch(Exception e){
                        System.out.println("Bad syntax in level file: "+file.getPath()+" at ("+col+","+row+") in attribute list.");
                    }
                }
                // convert array of attributes to ArrayList
                ArrayList<Integer> attrs = convertToInts(strAttrs);
                // depending on number, add an object
                switch(obj){
                    case 1: // Player 1
                        player1.setX(col);
                        player1.setY(row);
                        player1.setStartingX(col);
                        player1.setStartingY(row);
                        cols.add(player1);
                        nbrObjects++;
                        break;
                    case 2: // Player 2
                        player2.setX(col);
                        player2.setY(row);
                        player2.setStartingX(col);
                        player2.setStartingY(row);
                        cols.add(player2);
                        nbrObjects++;
                        break;
                        
                    // Blocks
                    case 100: // DestructibleBlock
                        cols.add(new DestructibleBlock(col,row));
                        nbrObjects++;
                        nbrDestructibleBlocks++;
                        break;
                    case 101: // SemidestructibleBlock
                        cols.add(new SemidestructibleBlock(col,row));
                        nbrObjects++;
                        nbrDestructibleBlocks++;
                        break;
                    case 102: // ReinforcedBlock
                        cols.add(new ReinforcedBlock(col,row));
                        nbrObjects++;
                        break;
                    case 103: // IndestructibleBlock
                        cols.add(new IndestructibleBlock(col,row));
                        nbrObjects++;
                        break;
                        
                    // 110-119 reserved for Player 1 objects
                    case 110: // Player 1 GateBlock
                        GateBlock gb1 = new GateBlock(col,row,player1);
                        gateBlocks.add(gb1);
                        cols.add(gb1);
                        nbrObjects++;
                        break;
                        
                    // 120-129 reserved for Player 2 objects
                    case 120: // Player 2 GateBlock
                        GateBlock gb2 = new GateBlock(col,row,player2);
                        gateBlocks.add(gb2);
                        cols.add(gb2);
                        nbrObjects++;
                        break;
                        
                    // Explosives
                    case 200: // ProximityMine (attribute is radius)
                        if(attrs.size() == 0)
                            attrs.add(2);
                        ProximityMine pm = new ProximityMine(col,row,attrs.get(0));   
                        proxMines.add(pm);
                        cols.add(pm);
                        nbrObjects++;
                        break;
                    case 201: // RemoteMine (attributes are set and radius)
                        if(attrs.size() == 0)
                            attrs.add(0);
                        if(attrs.size() < 2)
                            attrs.add(2);
                        RemoteMine rm = new RemoteMine(col,row,attrs.get(1),attrs.get(0));
                        remoteMines.add(rm);
                        cols.add(rm);
                        nbrObjects++;
                        break;
                        
                    // Powerups
                    case 300: // Center Fire
                        Fire f0 = new Fire(col,row,0);
                        cols.add(f0);
                        nbrObjects++;
                        break;
                     case 301: // Horizontal Fire
                        Fire f1 = new Fire(col,row,1);
                        cols.add(f1);
                        nbrObjects++;
                        break;
                     case 302: // Vertical Fire
                        Fire f2 = new Fire(col,row,2);
                        cols.add(f2);
                        nbrObjects++;
                        break;
                    case 303: // RemoteMineTrigger (attributes are sets)
                        if(attrs.size() == 0)
                            attrs.add(0);
                        RemoteMineTrigger rmt = new RemoteMineTrigger(col,row,attrs);
                        remoteMineTriggers.add(rmt);
                        cols.add(rmt);
                        nbrObjects++;
                        break;
                    case 304: // Warp (attributes are target x and target y coordinate)
                        Warp w = new Warp(col,row,attrs.get(0),attrs.get(1));
                        warps.add(w);
                        cols.add(w);
                        nbrObjects++;
                        break;
                        
                    // 310-319 reserved for Player 1 objects
                    // 320-329 reserved for Player 2 objects
                    
                    // Powerup Boosters
                    case 330: // RadiusBooster
                        RadiusBooster rb = new RadiusBooster(col,row);
                        cols.add(rb);
                        nbrObjects++;
                        break;
                    case 331: // MaxLiveExplosivesBooster
                        MaxLiveExplosivesBooster mleb = new MaxLiveExplosivesBooster(col,row);
                        cols.add(mleb);
                        nbrObjects++;
                        break;
                    case 332: // LivesBooster
                        LivesBooster lb = new LivesBooster(col,row);
                        cols.add(lb);
                        nbrObjects++;
                        break;
                    case 333: // DeathBooster
                        DeathBooster db = new DeathBooster(col,row);
                        cols.add(db);
                        nbrObjects++;
                        break;
                    case 334: // UnstoppableBombBooster
                        UnstoppableBombBooster ubb = new UnstoppableBombBooster(col,row);
                        cols.add(ubb);
                        nbrObjects++;
                        break;
                    case 335: // MineBooster
                        MineBooster mb = new MineBooster(col,row);
                        cols.add(mb);
                        nbrObjects++;
                        break;
                    case 336: // NukeBooster
                        NukeBooster nb = new NukeBooster(col,row);
                        cols.add(nb);
                        nbrObjects++;
                        break;
                        
                    default: // empty space (0)
                        cols.add(null);
                 }
            }
            // add row
            objects.add(cols); 
            row++;
        }
        // return an empty Grid if nothing was added to it
        if(objects.size() == 0)
            objects = getEmptyGrid(10,10);
        // display error message if player(s) are missing
        if(player1.getStartingX() == -1 || player2.getStartingX() == -1)             
            System.out.println("Player 1 and/or player 2 missing from level file: "+file.getPath());
   }
   // Helper methods
   private ArrayList<ArrayList<Locatable>> getEmptyGrid(int rows,int cols,boolean fillNull){
        // return an empty ArrayList with each space initialized to null of rows x cols
        ArrayList <ArrayList<Locatable>> objs = new ArrayList<ArrayList<Locatable>>(rows);
        for(int row = 0; row < rows; row++){
            ArrayList<Locatable> colArr = new ArrayList<Locatable>(cols);
            if(fillNull){
                for(int col = 0; col < cols; col++)
                    colArr.add(null);   
            }
            objs.add(colArr);
        }
        return objs;
   }
   private ArrayList<ArrayList<Locatable>> getEmptyGrid(int rows,int cols){
        return getEmptyGrid(rows,cols,true);
   }
   private ArrayList<Integer> convertToInts(String [] arr){
        // convert a String array to an ArrayList of Integers
        if(arr == null || arr.length == 0)
            return new ArrayList<Integer>();
        ArrayList<Integer>newArr = new ArrayList<Integer>(arr.length);
        for(int ind = 0; ind < arr.length; ind++){
            newArr.add(Integer.parseInt(arr[ind]));
        }
        return newArr;
   }
   private void updateCoordinates(){
        for(int row = 0; row < getHeight(); row++){
          for(int col = 0; col < getWidth(); col++){
            if(getObjectAt(col,row) != null){
                getObjectAt(col,row).setX(col);
                getObjectAt(col,row).setY(row);
            }
          }
        }
    }
   private int getHighestRemoteMineSet(){
        int highest = 0;
        for(int ind = 0; ind < remoteMineTriggers.size(); ind++){
               int thisHighest = remoteMineTriggers.get(ind).getSets().get(remoteMineTriggers.get(ind).getSets().size()-1);
               if(thisHighest > highest)
                    highest = thisHighest;
        }
        return highest;
   }
   // Get methods
   public Player getPlayer1(){
        return player1;  
   }
   public Player getPlayer2(){
        return player2;    
   }
   // Size methods
   public int getWidth(){ // get width of grid
        return objects.get(0).size();  
   }
   public int getHeight(){ // get height of grid
        return objects.size();
   }
   // is methods
   public boolean isMovableTo(int x,int y){ // check if space is valid and is empty or a powerup
        return isValidCoordinate(x,y) && (isEmpty(x,y) || isPowerup(x,y));
   }
   public boolean isEmpty(int x,int y){ // check if space is empty
        return objects.get(y).get(x) == null;
   }
   public boolean isPowerup(int x,int y){ // check if space is a Powerup
        return objects.get(y).get(x) instanceof Powerup;
   }
   public boolean isBooster(int x,int y){ // check if space is a Booster
        return objects.get(y).get(x) instanceof Booster;
   }
   public boolean isValidCoordinate(int x,int y){ // check if space is valid
        return x >= 0 && y >= 0 && y < getHeight() && x < getWidth();   
   }
   // Object methods
   public Locatable getObjectAt(int x,int y){ // return object at coordinate
        if(isValidCoordinate(x,y))
            return objects.get(y).get(x);
        return null;
   }
   public void addObject(Locatable obj){ // add an object to the grid
        addObjectTo(obj,obj.getX(),obj.getY());
   }
   public void addObjectTo(Locatable obj,int x,int y){ // add an object to the grid
        if(getObjectAt(x,y) != null) // must be adding to an empty space
            return;
        nbrObjects++;
        if(obj instanceof Booster)
            nbrBoosters++;
        else if(obj instanceof Block && !(obj instanceof IndestructibleBlock) && !(obj instanceof ReinforcedBlock))
            nbrDestructibleBlocks++;
        obj.setX(x);
        obj.setY(y);
        objects.get(obj.getY()).set(obj.getX(),obj);
   }
   public void moveObjectTo(Locatable obj,int newX, int newY){ // removes and then adds to new location in array
        // move to the space (unless a Powerup there wasn't used up and thus it must have moved Player on its own)
        if((isPowerup(newX,newY) && obj instanceof Player && ((Powerup)(getObjectAt(newX,newY))).usePowerup((Player)obj)) || !isPowerup(newX,newY)){ 
            removeObject(obj); // remove object at old space
            removeObjectAt(newX,newY); // remove object (powerup) at new space
            addObjectTo(obj,newX,newY); // add object to new space
        }
   }
   public boolean removeObject(Locatable obj){ // remove an object from the grid
        return removeObjectAt(obj.getX(),obj.getY());
   }
   public boolean removeObjectAt(int x, int y){ // remove the object at coordinate
        Locatable obj = getObjectAt(x,y);
        if(obj == null) // must be removing an existing object
            return false;
        // decrements
        nbrObjects--;
        if(obj instanceof Booster)
            nbrBoosters--;
        if(obj instanceof Block && !(obj instanceof IndestructibleBlock) && !(obj instanceof ReinforcedBlock))
            nbrDestructibleBlocks--;
        objects.get(y).set(x,null); // add null in removed object's place
        return true;
   }
   // Explosives array methods
   public Explosive getExplosiveAt(int x,int y){ // look through array of explosives for a explosive at a certain location
       for(int ind = 0; ind < explosives.size(); ind++){
            if(explosives.get(ind).getX() == x && explosives.get(ind).getY() == y)
                return explosives.get(ind);
        }
        return null;
   }
   public int getLiveExplosiveCount(Player player){
        int count = 0;
        for(int ind = 0; ind < explosives.size(); ind++){
                if(explosives.get(ind).getCountDown() > 0 && explosives.get(ind).getOwner() == player)
                    count++;
        }
        return count;
   }
   // Add to array methods
   public void addExplosive(Explosive newExplosive){
        explosives.add(newExplosive);  
   }
   public void addWarp(Warp w){
        warps.add(w);  
   }
   public void addRemoteMineTrigger(RemoteMineTrigger rmt){
        remoteMineTriggers.add(rmt);
   }
   // Array contains methods
   public boolean containsObject(Locatable obj){
        return getObjectAt(obj.getX(),obj.getY()) == obj;
   }
   public boolean containsProximityMine(ProximityMine pm){
        return proxMines.contains(pm);
   }
   public boolean containsRemoteMine(RemoteMine rm){
        return remoteMines.contains(rm);
   }
   // Array remove methods
   public void removeExplosive(Explosive removeExplosive){
        explosives.remove(explosives.indexOf(removeExplosive));  
   }
   public void removeProximityMine(ProximityMine removepm){
        proxMines.remove(proxMines.indexOf(removepm));  
   }
   public void removeRemoteMine(RemoteMine removerm){
        remoteMines.remove(remoteMines.indexOf(removerm));  
   }
   public void removeRemoteMineTrigger(RemoteMineTrigger removerm){
        remoteMineTriggers.remove(remoteMineTriggers.indexOf(removerm)); 
   }
   public void removeGateBlock(GateBlock removeGateBlock){
        gateBlocks.remove(gateBlocks.indexOf(removeGateBlock));  
   }
   // Array get methods
   public ArrayList<ArrayList<Locatable>> getObjectArray(){
        return objects;  
   }
   public ArrayList<Explosive> getExplosiveArray(){
        return explosives;  
   }
   public ArrayList<ProximityMine> getProxMineArray(){
        return proxMines;  
   }
   public ArrayList<RemoteMine> getRemoteMineArray(){
        return remoteMines;  
   }
   public ArrayList<RemoteMineTrigger> getRemoteMineTriggerArray(){
        return remoteMineTriggers;  
   }
   public ArrayList<GateBlock> getGateBlockArray(){
        return gateBlocks;  
   }
   public ArrayList<Warp> getWarpArray(){
        return warps;  
   }
   // Get counter methods
   public int getNbrObjects(){
        return nbrObjects;
   }
   public int getNbrBoosters(){
        return nbrBoosters;   
   }
   public int getNbrDestructibleBlocks(){
        return nbrDestructibleBlocks;   
   }
   // Scanner methods
   public Scanner getScannerFromFile(File file){
       Scanner sc = null;
       try{ // InputStreamReader works for: inside .jar and BlueJ package
            //                     not for: outside .jar and BlueJ package
    	   sc = new Scanner(file);
           //sc = new Scanner(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file.getName()))));
       }catch(Exception e1){
            /*try{ // FileReader works for: inside and outside BlueJ package and outside .jar
                 //              not for: inside .jar
                sc = new Scanner(new BufferedReader(new FileReader(file)));
            }catch(Exception e){
                //System.out.println("Error reading input stream and level file: "+file.getPath());
                return null;
            }
            //System.out.println("Error reading input stream: "+file.getPath());   */       
       }
       // bug: if a level file exists inside and outside of the jar/package,
       // the one inside will be used even if the outside one was chosen.
       return sc;
   }
   // Image methods
   private Image getImageFromFile(String fileName,boolean create){
      URL url = this.getClass().getResource(fileName);
      if(url == null)
        return null;
      if(create) // createImage() will not share image with any other requests of this URL
        return Toolkit.getDefaultToolkit().createImage(url);
      // getImage() will share the image with multiple requests of the same URL
      return Toolkit.getDefaultToolkit().getImage(url);
   }
   private Image getImageFromFile(String fileName){
      return getImageFromFile(fileName,false);
   }
   private void preloadImages(){
      images = new ArrayList<Image>(60);
      images.add(null);//getImageFromFile("player1.jpg"));
      images.add(null);//getImageFromFile("player2.jpg")); // 1
      images.add(null);//getImageFromFile("player1onexplosive.jpg"));
      images.add(null);//getImageFromFile("player2onexplosive.jpg")); // 3
      images.add(getImageFromFile("/resources/images/blocks/destructibleblock.jpg"));
      images.add(getImageFromFile("/resources/images/blocks/reinforcedblock.jpg")); // 5
      images.add(getImageFromFile("/resources/images/bombs/bomb.gif"));
      images.add(null);//getImageFromFile("fire.jpg")); // 7
      images.add(getImageFromFile("/resources/images/boosters/radiusbooster.png"));
      images.add(getImageFromFile("/resources/images/boosters/maxlivebombsbooster.png")); // 9
      images.add(getImageFromFile("/resources/images/boosters/lifesbooster.gif"));
      images.add(getImageFromFile("/resources/images/players/p1u.gif"));//11
      images.add(getImageFromFile("/resources/images/players/p1us.png"));
      images.add(getImageFromFile("/resources/images/players/p1d.gif"));//13
      images.add(getImageFromFile("/resources/images/players/p1ds.png"));
      images.add(getImageFromFile("/resources/images/players/p1l.gif"));//15
      images.add(getImageFromFile("/resources/images/players/p1ls.png"));
      images.add(getImageFromFile("/resources/images/players/p1r.gif"));//17
      images.add(getImageFromFile("/resources/images/players/p1rs.png"));
      images.add(null);//19 bomb2.gif
      images.add(getImageFromFile("/resources/images/bombs/centerx.gif"));
      images.add(getImageFromFile("/resources/images/bombs/horizx.gif")); // 21
      images.add(getImageFromFile("/resources/images/bombs/vertx.gif"));
      images.add(null); // 23
      images.add(null);
      images.add(getImageFromFile("/resources/images/players/p2u.gif"));//25
      images.add(getImageFromFile("/resources/images/players/p2us.png"));
      images.add(getImageFromFile("/resources/images/players/p2d.gif"));//27
      images.add(getImageFromFile("/resources/images/players/p2ds.png"));
      images.add(getImageFromFile("/resources/images/players/p2l.gif"));//29
      images.add(getImageFromFile("/resources/images/players/p2ls.png"));
      images.add(getImageFromFile("/resources/images/players/p2r.gif"));//31
      images.add(getImageFromFile("/resources/images/players/p2rs.png"));
      images.add(getImageFromFile("/resources/images/bombs/unstoppablebomb.png")); // 33
      images.add(getImageFromFile("/resources/images/bombs/ubomb.gif"));
      images.add(null); // 35 ubomb2.gif
      images.add(getImageFromFile("/resources/images/bombs/mine.png"));
      images.add(getImageFromFile("/resources/images/bombs/mine.gif")); // 37
      images.add(getImageFromFile("/resources/images/bombs/remotemine_red.gif"));
      images.add(getImageFromFile("/resources/images/boosters/deathbooster.gif")); // 39
      images.add(getImageFromFile("/resources/images/bombs/nuke.gif"));
      images.add(getImageFromFile("/resources/images/boosters/nukebooster.png")); // 41
      images.add(null);//getImageFromFile("destructibleblock1.jpg"));
      images.add(null);//getImageFromFile("reinforcedblock1.jpg")); // 43
      images.add(null);//getImageFromFile("destructibleblock2.jpg"));
      images.add(getImageFromFile("/resources/images/blocks/semidestructibleblock.jpg")); // 45
      images.add(getImageFromFile("/resources/images/blocks/semidestructibleblock_damaged1.jpg")); 
      images.add(getImageFromFile("/resources/images/blocks/semidestructibleblock_damaged2.jpg")); // 47
      images.add(getImageFromFile("/resources/images/bombs/proximitymine.gif"));
      images.add(null);//getImageFromFile("reinforcedblock2.jpg")); // 49
      images.add(null);//getImageFromFile("semidestructibleblock-1.jpg")); 
      images.add(null);//getImageFromFile("semidestructibleblock_damaged1-1.jpg")); // 51
      images.add(null);//getImageFromFile("semidestructibleblock_damaged2-1.jpg"));
      images.add(getImageFromFile("/resources/images/blocks/gateblockp1.gif")); // 53
      images.add(getImageFromFile("/resources/images/blocks/gateblockp2.gif"));
      images.add(getImageFromFile("/resources/images/bombs/remoteminetrigger_covered.gif")); // 55
      images.add(getImageFromFile("/resources/images/bombs/remoteminetrigger_uncovered.gif"));
      images.add(getImageFromFile("/resources/images/blocks/warp.jpg")); // 57
      images.add(getImageFromFile("/resources/images/bombs/remotemine_blue.gif"));
      images.add(getImageFromFile("/resources/images/blocks/indestructibleblock.jpg")); // 59
   }
   public void reloadBombAnimations(){
       images.remove(6);
       images.add(6,getImageFromFile("/resources/images/bombs/bomb.gif",true));
       images.remove(34);
       images.add(34,getImageFromFile("/resources/images/bombs/ubomb.gif",true));
       images.remove(37);
       images.add(37,getImageFromFile("/resources/images/bombs/mine.gif",true));
   }
    public Image getImage(int index){
        return images.get(index);   
    }
    public void clear(){
        objects.clear();
        nbrObjects = 0;
        nbrBoosters = 0;
        nbrDestructibleBlocks = 0;
        explosives.clear();
        proxMines.clear();
        remoteMines.clear();
        gateBlocks.clear();
        warps.clear();
    }
    public void addRandomPowerupTo(int newX,int newY){
        // decide which Powerup to add based on probability
        removeObjectAt(newX,newY);
        Powerup p = null;
        double rand = Math.random();
        if(rand < .4)
            p = new RadiusBooster(newX,newY);
        else if(rand < .8)
            p = new MaxLiveExplosivesBooster(newX,newY);
        else if(rand < .84)
            p = new LivesBooster(newX,newY);
        else if(rand < .88)
            p = new DeathBooster(newX,newY);
        else if(rand < .90)
            p = new UnstoppableBombBooster(newX,newY);
        else if(rand < .97)
            p = new MineBooster(newX,newY);
        else
            p = new NukeBooster(newX,newY);
        p.setGrid(this);
        addObject(p);
    }
    public void setAllGrids(){
      // set the grid of each object
      for(int row = 0; row < getHeight(); row++){
          for(int col = 0; col < getWidth(); col++){
             if(getObjectAt(col,row) != null)
                 getObjectAt(col,row).setGrid(this);
          }
      }
      // in case a player is not in the Grid yet (was not loaded from file)
      player1.setGrid(this);
      player2.setGrid(this);
    }
    public void setBlockImages(int imageSet){
       // update the images to use all Blocks to a new set
       for(int row = 0; row < getHeight(); row++){
          for(int col = 0; col < getWidth(); col++){
              if(getObjectAt(col,row) instanceof Block)
                setBlockImage((Block)getObjectAt(col,row),imageSet);
          }
      }
    }
    public void setBlockImage(Block block,int imageSet){
         // set the image of a single Block to a new set
         if(block instanceof DestructibleBlock){
                switch(imageSet){
                    case 0:
                        block.setImage(4);
                        break;
                    case 1:
                        block.setImage(42);
                        break;
                    default: // 2
                        block.setImage(44);
                        break;
                 }
         }else if(block instanceof GateBlock)
                block.setImage(52+((GateBlock)block).getOwner().getNumber());
         else if (block instanceof ReinforcedBlock){
                switch(imageSet){
                    case 0:
                        block.setImage(5);
                        break;
                    case 1:
                        block.setImage(43);
                        break;
                    default: // 2
                        block.setImage(49);
                        break;
                 }   
         }else if (block instanceof SemidestructibleBlock){
                switch(imageSet){
                    case 0:
                        switch(((SemidestructibleBlock)block).getDeathsLeft()){
                            case 3:
                                block.setImage(45);
                                break;
                            case 2:
                                block.setImage(46);
                                break;
                            default: // 1
                                block.setImage(47);
                                break;
                        }  
                        break;
                    case 1:
                        switch(((SemidestructibleBlock)block).getDeathsLeft()){
                            case 3:
                                block.setImage(50);
                                break;
                            case 2:
                                block.setImage(51);
                                break;
                            default: // 1
                                block.setImage(52);
                                break;
                        }  
                        break;
                    default: // 2
                        switch(((SemidestructibleBlock)block).getDeathsLeft()){
                            case 3:
                                block.setImage(45);
                                break;
                            case 2:
                                block.setImage(46);
                                break;
                            default: // 1
                                block.setImage(47);
                                break;
                        }  
                        break;
                 }   
         }
    }
    // Methods for manipulating rows and columns
    public void setCols(int newCols){
        insertCols(getWidth(),newCols-getWidth());   
    }
    public void setRows(int newRows){
        insertRows(getHeight(),newRows-getHeight());   
    }
    public void shiftCols(int colShift){
        if(colShift > 0)
            insertCols(0,colShift);
        else if(colShift < 0)
            insertCols(colShift*-1,colShift);
    }
    public void shiftRows(int rowShift){
        if(rowShift > 0)
            insertRows(0,rowShift);
        else if(rowShift < 0)
            insertRows(rowShift*-1,rowShift);
    }
    public void insertRows(int insertAt,int count){
        if(count < 0){
            for(int row = insertAt - 1; row >= insertAt + count; row--){
                // go through each column and remove it before removing the entire row
                for(int col = 0; col < getWidth(); col++){
                    removeObjectAt(col,row);
                }
                objects.remove(row);     
            }
        }else if(count > 0){
            for(int row = insertAt; row < insertAt + count; row++){
                ArrayList<Locatable> cols = new ArrayList<Locatable>(getWidth());
                for(int col = 0; col < getWidth(); col++){
                    cols.add(null);
                }
                objects.add(row,cols);     
            }
        }
        // Update Warp coordinates
        for(int ind = 0; ind < warps.size(); ind++){
            if(warps.get(ind).getTargetY() >= insertAt)
                warps.get(ind).setTargetY(warps.get(ind).getTargetY()+count);
        }
        updateCoordinates();
    }
    public void insertCols(int insertAt,int count){
        if(count < 0){
            for(int row = 0; row < getHeight(); row++){
                for(int col = insertAt - 1; col >= insertAt + count; col--){
                    removeObjectAt(col,row);
                    objects.get(row).remove(col);
                }
            }
        }else if(count > 0){
            for(int row = 0; row < getHeight(); row++){
                for(int col = insertAt; col < insertAt + count; col++){
                    objects.get(row).add(col,null);
                }
            } 
        }
        // Update Warp coordinates
        for(int ind = 0; ind < warps.size(); ind++){
            if(warps.get(ind).getTargetX() >= insertAt )
                warps.get(ind).setTargetX(warps.get(ind).getTargetX()+count);
        }
        updateCoordinates();
    }
    public void surround(int padding){
        setRows(getHeight()+padding);
        setCols(getWidth()+padding);
        shiftRows(padding);
        shiftCols(padding);
    }
    public void fillWith(int obj){
        int prevWidth = getWidth();
        int prevHeight = getHeight();
        switch(obj){
            case 0: // empty space
                clear();
                objects = getEmptyGrid(prevHeight,prevWidth);
                break;
            case 1: // Player 1
                JOptionPane.showMessageDialog(null,"Can't fill grid with player 1.","Error",0);
                break;
            case 2: // Player 2
                JOptionPane.showMessageDialog(null,"Can't fill grid with player 2.","Error",0);
                break;
            case 201: // RemoteMine
                JOptionPane.showMessageDialog(null,"Can't fill grid with remote mines.","Error",0);
                break;
            case 303: // RemoteMineTrigger
                JOptionPane.showMessageDialog(null,"Can't fill grid with remote mine triggers.","Error",0);
                break;
            case 304: // Warp
                JOptionPane.showMessageDialog(null,"Can't fill grid with warps.","Error",0);
                break;
            default:
                clear();
                objects = getEmptyGrid(prevHeight,prevWidth);
                Block b = null;
                for(int row = 0; row < prevHeight; row++){
                    for(int col = 0; col < prevWidth; col++){
                        switch(obj){
                            case 100: // DestructibleBlock
                                b = new DestructibleBlock(col,row);
                                b.setGrid(this);
                                addObject(b);
                                nbrDestructibleBlocks = getHeight()*getWidth();
                                break;
                            case 101: // SemidestructibleBlock
                                b = new SemidestructibleBlock(col,row);
                                b.setGrid(this);
                                addObject(b);
                                nbrDestructibleBlocks = getHeight()*getWidth();
                                break;
                            case 102: // ReinforcedBlock
                                b = new ReinforcedBlock(col,row);
                                b.setGrid(this);
                                addObject(b);
                                break;
                            case 103: // IndestructibleBlock
                                b = new IndestructibleBlock(col,row);
                                b.setGrid(this);
                                addObject(b);
                                break;
                            case 110: // Player 1 GateBlock
                                b = new GateBlock(col,row,player1);
                                b.setGrid(this);
                                addObject(b);
                                break;
                            case 120: // Player 2 GateBlock
                                b = new GateBlock(col,row,player2);
                                b.setGrid(this);
                                addObject(b);
                                break;
                            case 200: // ProximityMine
                                ProximityMine pm = new ProximityMine(col,row,2);
                                pm.setGrid(this);
                                proxMines.add(pm);
                                addObject(pm);
                                break;
                            case 300: // Center Fire
                                Fire f0 = new Fire(col,row,0);
                                f0.setGrid(this);
                                addObject(f0);
                                break;
                            case 301: // Horizontal Fire
                                Fire f1 = new Fire(col,row,1);
                                f1.setGrid(this);
                                addObject(f1);
                                break;
                            case 302: // Vertical Fire
                                Fire f2 = new Fire(col,row,2);
                                f2.setGrid(this);
                                addObject(f2);
                                break;
                            case 330: // Radius Booster
                                RadiusBooster rb = new RadiusBooster(col,row);
                                rb.setGrid(this);
                                addObject(rb);
                                break;
                            case 331: // Max Live Explosives Booster
                                MaxLiveExplosivesBooster mleb = new MaxLiveExplosivesBooster(col,row);
                                mleb.setGrid(this);
                                addObject(mleb);
                                break;
                            case 332: // Lives Booster
                                LivesBooster lb = new LivesBooster(col,row);
                                lb.setGrid(this);
                                addObject(lb);
                                break;
                            case 333: // Death Booster
                                DeathBooster db = new DeathBooster(col,row);
                                db.setGrid(this);
                                addObject(db);
                                break;
                            case 334: // Unstoppable Bomb Booster
                                UnstoppableBombBooster ubb = new UnstoppableBombBooster(col,row);
                                ubb.setGrid(this);
                                addObject(ubb);
                                break;
                            case 335: // Mine Booster
                                MineBooster mb = new MineBooster(col,row);
                                mb.setGrid(this);
                                addObject(mb);
                                break;
                            case 336: // Nuke Booster
                                NukeBooster nb = new NukeBooster(col,row);
                                nb.setGrid(this);
                                addObject(nb);
                                break;
                        }
                   }
                }
                nbrObjects = getHeight()*getWidth();
                updateCoordinates();
       }
    }
    public void invert(){
        ArrayList<ArrayList<Locatable>> newGrid = getEmptyGrid(getWidth(),getHeight(),false);
        for(int row = 0; row < getHeight(); row++){
          for(int col = 0; col < getWidth(); col++){
                Locatable obj = getObjectAt(col,row);
                newGrid.get(col).add(row,obj);
                if(obj != null){
                    obj.setX(row);
                    obj.setY(col);
                    if(obj instanceof Warp){ // Update Warp coordinates
                        int tempX = ((Warp)obj).getTargetX();
                        ((Warp)obj).setTargetX(((Warp)obj).getTargetY());
                        ((Warp)obj).setTargetY(tempX);
                    }
                }
          }
        }
        objects = newGrid;
    }
    public void flipVert(){
        ArrayList<ArrayList<Locatable>> newGrid = getEmptyGrid(getHeight(),getWidth(),false);
        for(int row = 0; row < getHeight(); row++){
            newGrid.set(row,objects.get(getHeight()-row-1));
            for(int col = 0; col < getWidth(); col++){
                Locatable obj = getObjectAt(col,row);
                if(obj != null){
                    obj.setX(col);
                    obj.setY(row);
                    if(obj instanceof Warp) // Update Warp coordinates
                        ((Warp)obj).setTargetY(getHeight()-((Warp)obj).getTargetY()-1);
                }      
            }
        }
        objects = newGrid;
    }
    public void flipHoriz(){
        ArrayList<ArrayList<Locatable>> newGrid = getEmptyGrid(getHeight(),getWidth(),false);
        for(int row = 0; row < getHeight(); row++){
          for(int col = 0; col < getWidth(); col++){
                Locatable obj = getObjectAt(getWidth()-col-1,row);
                if(obj != null){
                    obj.setX(col);
                    obj.setY(row);
                    if(obj instanceof Warp) // Update Warp coordinates
                        ((Warp)obj).setTargetX(getWidth()-((Warp)obj).getTargetX()-1);
                }
                newGrid.get(row).add(col,obj);
            }
        }
        objects = newGrid;
    }
    public void mirVert(){
        int highestSet = getHighestRemoteMineSet();
        int height = getHeight();
        for(int row = height - 1; row >= 0; row--){
            ArrayList<Locatable>colArr = new ArrayList<Locatable>();
            for(int col = 0; col < getWidth(); col++){
                Locatable obj = getObjectAt(col,row);
                int y = height * 2 - row - 1;
                if(obj != null){
                    obj.setX(col);
                    obj.setY(y);
                    if(obj instanceof Warp){ // Update Warp coordinates
                         Locatable w = new Warp(col,y,((Warp)obj).getTargetX(),height * 2 - ((Warp)obj).getTargetY() - 1);
                         w.setGrid(this);
                         colArr.add(w);
                    }else if(obj instanceof RemoteMineTrigger){ // New RemoteMineTrigger with new sets
                         ArrayList<Integer>oldSets = ((RemoteMineTrigger)obj).getSets();
                         ArrayList<Integer>newSets = new ArrayList<Integer>(oldSets.size());
                         for(int ind = 0; ind < oldSets.size(); ind++){
                            newSets.add(oldSets.get(ind)+highestSet);    
                         }
                         Locatable rmt = new RemoteMineTrigger(col,y,newSets);
                         rmt.setGrid(this);
                         addRemoteMineTrigger((RemoteMineTrigger)rmt);
                         colArr.add(rmt);
                    }else if(obj instanceof RemoteMine){ // New RemoteMine with new set
                         Locatable rm = new RemoteMine(col,y,((RemoteMine)obj).getRadius(),highestSet+((RemoteMine)obj).getSet());
                         rm.setGrid(this);
                         colArr.add(rm);
                    }else if(obj instanceof ProximityMine){
                         Locatable pm = new ProximityMine(col,y,((ProximityMine)obj).getRadius());  
                         pm.setGrid(this);
                         colArr.add(pm);
                    // add object but not a duplicate Player
                    }else if(!(obj instanceof Player))
                         colArr.add(obj);
                    else
                         colArr.add(null);
                }else
                    colArr.add(null);
            }
            objects.add(colArr);
        }
    }
    public void mirHoriz(){
        int highestSet = getHighestRemoteMineSet();
        int width = getWidth();
        for(int row = 0; row < getHeight(); row++){
          for(int col = width - 1; col >= 0; col--){
                Locatable obj = getObjectAt(col,row);
                int x = width * 2 - col - 1;
                if(obj != null){
                    obj.setX(x);
                    obj.setY(row);
                    if(obj instanceof Warp){ // Update Warp coordinates
                         Locatable w = new Warp(x,row,width * 2 - ((Warp)obj).getTargetX() - 1,((Warp)obj).getTargetY());
                         w.setGrid(this);
                         objects.get(row).add(w);
                    }else if(obj instanceof RemoteMineTrigger){ // New RemoteMineTrigger with new sets
                         ArrayList<Integer>oldSets = ((RemoteMineTrigger)obj).getSets();
                         ArrayList<Integer>newSets = new ArrayList<Integer>(oldSets.size());
                         for(int ind = 0; ind < oldSets.size(); ind++){
                            newSets.add(oldSets.get(ind)+highestSet);    
                         }
                         Locatable rmt = new RemoteMineTrigger(x,row,newSets);
                         rmt.setGrid(this);
                         addRemoteMineTrigger((RemoteMineTrigger)rmt);
                         objects.get(row).add(rmt);
                    }else if(obj instanceof RemoteMine){ // New RemoteMine with new set
                         Locatable rm = new RemoteMine(x,row,((RemoteMine)obj).getRadius(),highestSet+((RemoteMine)obj).getSet());
                         rm.setGrid(this);
                         objects.get(row).add(rm);
                    }else if(obj instanceof ProximityMine){
                         Locatable pm = new ProximityMine(x,row,((ProximityMine)obj).getRadius());  
                         pm.setGrid(this);
                         objects.get(row).add(pm);
                    // add object but not a duplicate Player
                    }else if(!(obj instanceof Player))  
                         objects.get(row).add(obj);
                    else
                         objects.get(row).add(null);
                }else
                    objects.get(row).add(null);
            }
        }
    }
}
