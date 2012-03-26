/**
 * class LevelMakerPanel
 * 
 * @version 2007-11-21
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Iterator;
public class LevelMakerPanel extends JPanel
{
    private JFrame window;
    private Grid grid;
    private File dataFile;
    private int curObjInd,curRemoteMineSet,curRemoteMineRadius,curProxMineRadius;
    private int blockImageSet,draggingButton;
    private int curWarpTargetX,curWarpTargetY;
    private ArrayList<Integer>curRemoteMineTriggerSets;
    private boolean showGridLines,saved;
    public LevelMakerPanel(JFrame newWindow,String levelPath)
    {
        // initialize instance variables
        window = newWindow;
        curObjInd = 1;
        curRemoteMineSet = 0;
        curRemoteMineRadius = 2;
        curRemoteMineTriggerSets = new ArrayList<Integer>();
        curProxMineRadius = 2;
        blockImageSet = 0;
        draggingButton = 0;
        showGridLines = true;
        saved = true;
                
        // set bg color
        setBackground(Color.GRAY);
        
        // create new level (Grid) either empty or by loading a level in
        if(levelPath == "")
            doNewLevel(null);
        else{
            dataFile = new File(levelPath);
            doNewLevel(dataFile);
        }
        
        ActionListener action = new ActionListener() {
        // Defines the action taken each time the timer fires.
            public void actionPerformed(ActionEvent evt) {
                String title = "Level Maker";
                if(dataFile != null)
                    title += ": "+dataFile.getName();
                if(!saved)
                    title += "*";
                window.setTitle(title);                
                repaint();
            }
        };
        
        Timer timer = new Timer( 30, action );
        timer.start();
        
        // mousePressed and MouseDragged events
        final MML mml = new MML();
        addMouseMotionListener(mml);
        addMouseListener(new MouseAdapter(){
           public void mousePressed(MouseEvent e){
               mml.mouseDragged(e);
           }
           public void mouseReleased(MouseEvent e){
               draggingButton = 0;
           }
        });
        window.setJMenuBar( getMenuBar() );
    }
    private class MML implements MouseMotionListener{
        public void mouseDragged(MouseEvent e) {
            int row = getRow(e.getY());
            int col = getCol(e.getX());
            if(grid.isValidCoordinate(col,row)){
                saved = false;
                // If right-click then delete object at mouse location
                if(e.getButton() == MouseEvent.BUTTON3 || draggingButton == MouseEvent.BUTTON3){
                    // keep track of the button that started the dragging
                    if(draggingButton == 0)
                        draggingButton = e.getButton();
                    // Player coordinates need to be updated so that
                    // a future object in this place does not get removed
                    // when the Player is added to a new space.
                    if(grid.getObjectAt(col,row) instanceof Player){
                        grid.getObjectAt(col,row).setX(-1);
                        grid.getObjectAt(col,row).setY(-1);
                    }
                    // remove object
                    grid.removeObjectAt(col,row);
                    return;
                }
                if(grid.isValidCoordinate(col,row) && (grid.isEmpty(col,row) || curObjInd == 0)){
                    // Depending on where mouse was clicked and current object from menu, add new object
                    Locatable obj = null;
                    switch(curObjInd){
                        case 0: // create empty space here
                            if(grid.getObjectAt(col,row) instanceof Player){
                                grid.getObjectAt(col,row).setX(-1);
                                grid.getObjectAt(col,row).setY(-1);
                            }
                            grid.removeObjectAt(col,row);
                            break;
                        case 1: // move Player 1 here
                            grid.moveObjectTo(grid.getPlayer1(),col,row);
                            break;
                        case 2: // move Player 2 here
                            grid.moveObjectTo(grid.getPlayer2(),col,row);
                            break;
                        case 100: // create DestructibleBlock here
                            obj = new DestructibleBlock(col,row);
                            break;
                        case 101: // create SemidestructibleBlock here
                            obj = new SemidestructibleBlock(col,row);
                            break;
                        case 102: // create ReinforcedBlock here
                            obj = new ReinforcedBlock(col,row);
                            break;
                        case 103: // create IndestructibleBlock here
                            obj = new IndestructibleBlock(col,row);
                            break;
                        case 110: // create Player 1 GateBlock here
                            obj = new GateBlock(col,row,grid.getPlayer1());
                            break; 
                        case 120: // create Player 2 GateBlock here
                            obj = new GateBlock(col,row,grid.getPlayer2());
                            break; 
                        case 200: // create ProximityMine here
                            obj = new ProximityMine(col,row,curProxMineRadius);
                            break; 
                        case 201: // create RemoteMine here
                            obj = new RemoteMine(col,row,curRemoteMineRadius,curRemoteMineSet);
                            break;
                        case 300: // create center Fire here
                            obj = new Fire(col,row,0);
                            break;
                        case 301: // create horizontal Fire here
                            obj = new Fire(col,row,1);
                            break; 
                        case 302: // create vertical Fire here
                            obj = new Fire(col,row,2);
                            break; 
                        case 303: // create RemoteMineTrigger here
                            obj = new RemoteMineTrigger(col,row,curRemoteMineTriggerSets);
                            // need to add RMT to array so we can keep track of the highest set in the Grid for mirroring
                            grid.addRemoteMineTrigger((RemoteMineTrigger)obj);
                            break; 
                        case 304: // create Warp here
                            obj = new Warp(col,row,curWarpTargetX,curWarpTargetY);
                            // need to add Warp to array so we can update its target coordinates easily when necessary
                            grid.addWarp((Warp)obj);
                            break; 
                        case 330: // RadiusBooster
                            obj = new RadiusBooster(col,row);
                            break;
                        case 331: // MaxLiveExplosivesBooster
                            obj = new MaxLiveExplosivesBooster(col,row);
                            break;
                        case 332: // LivesBooster
                            obj = new LivesBooster(col,row);
                            break;
                        case 333: // DeathBooster
                            obj = new DeathBooster(col,row);
                            break;
                        case 334: // UnstoppableBombBooster
                            obj = new UnstoppableBombBooster(col,row);
                            break;
                        case 335: // MineBooster
                            obj = new MineBooster(col,row);
                            break;
                        case 336: // NukeBooster
                            obj = new NukeBooster(col,row);
                            break;
                     }
                     if(obj != null){
                        obj.setGrid(grid);
                        grid.addObject(obj);
                        if(obj instanceof Block)
                            grid.setBlockImage((Block)obj,blockImageSet);
                     }
                 }
             }
        }
        public void mouseMoved(MouseEvent e){
            // Show tool tip with coordinates and object name if not null
            int col = getCol(e.getX());
            int row = getRow(e.getY());
            if(grid.getObjectAt(col,row) != null)
                setToolTipText(grid.getObjectAt(col,row)+" at ("+col+","+row+")");      
            else
                setToolTipText("("+col+","+row+")");   
        }
    }
    private void doNewLevel(File newFile){
        if(newFile == null){ // create a blank Grid
            dataFile = null;
            // create a blank new Grid
            grid = new Grid(10,10);
            // set grid of players
            grid.getPlayer1().setGrid(grid);
            grid.getPlayer2().setGrid(grid);
        }else if(newFile.getName() == ""){ // show OpenDialog for loading in a Grid from a file
            File tempFile = BombermanPanel.getFileFromOpenDialog(this,dataFile);
            if(tempFile != null){ // only create new Grid and Players if file selection works
                dataFile = tempFile;
                // create the grid from the file
                grid = new Grid(dataFile);
                // set the grid of each object
                grid.setAllGrids();
                // update images for Blocks
                grid.setBlockImages(blockImageSet);
            }else
                return;
        }else{ // load in the current file for the Grid
            // create the grid from the file
            grid = new Grid(dataFile);
            // set the grid of each object
            grid.setAllGrids();
            // update images for Blocks
            grid.setBlockImages(blockImageSet);
        }  
    }
    private String al2csv(ArrayList<Integer>arr){ // ArrayList to comma separated values
        String s = "";
        for(int ind = 0; ind < arr.size(); ind++){
            s += arr.get(ind);
            if(ind < arr.size()-1)
                s += ",";
        }
        return s;
   }
    private int getRow(int y){
        // get the row of the y cooridinate from the mouse
        for(int row = 0; row < grid.getHeight(); row++){
            if(y >= row*getHeight()/grid.getHeight() &&
              y < (row+1)*getHeight()/grid.getHeight())
                return row;   
        }
        return -1;
    }
    private int getCol(int x){
        // get the column of the x cooridinate from the mouse
        for(int col = 0; col < grid.getWidth(); col++){
            if(x >= col*getWidth()/grid.getWidth() &&
              x < (col+1)*getWidth()/grid.getWidth())
                return col;  
        }
        return -1;
    }
    public void paintComponent(Graphics g) {
      super.paintComponent(g); // fill panel with background color
      // draw all non-null spaces in the grid.
      for(int row = 0; row < grid.getHeight(); row++){
          for(int col = 0; col < grid.getWidth(); col++){
            if(showGridLines){  // draw grid lines
                  g.setColor(Color.BLACK);
                  if(col == 0)
                        g.drawLine(0,row*getHeight()/grid.getHeight(),
                         getWidth(),row*getHeight()/grid.getHeight());
                  if(row == 0)
                        g.drawLine(col*getWidth()/grid.getWidth(),0,
                         col*getWidth()/grid.getWidth(),getHeight());
             }
             if (grid.getObjectAt(col,row) != null){ // draw the object
                 g.drawImage(grid.getObjectAt(col,row).getImage(),
                    col*getWidth()/grid.getWidth(),row*getHeight()/grid.getHeight(),
                    getWidth()/grid.getWidth(),getHeight()/grid.getHeight(),this);
             }
         }
      }
   } 
   private JMenuBar getMenuBar() {
      JMenuBar menuBar = new JMenuBar();
      MenuHandler listener = new MenuHandler();
      
      JMenu levelMenu = new JMenu("Level");
      JMenuItem menuItemN = new JMenuItem("New");
      JMenuItem menuItemS = new JMenuItem("Save");
      JMenuItem menuItemSA = new JMenuItem("Save As...");
      JMenuItem menuItemL = new JMenuItem("Load...");
      JMenuItem menuItemP = new JMenuItem("Play...");
      JMenuItem menuItemW = new JMenuItem("Close");
      menuItemN.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
      menuItemS.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
      menuItemSA.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
      menuItemL.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
      menuItemP.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
      menuItemW.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
      addMenuItem(menuItemN,levelMenu,listener);
      addMenuItem(menuItemS,levelMenu,listener);
      addMenuItem(menuItemSA,levelMenu,listener);
      addMenuItem(menuItemL,levelMenu,listener);
      addMenuItem(menuItemP,levelMenu,listener);
      addMenuItem(menuItemW,levelMenu,listener);
      menuBar.add(levelMenu);
      
      JMenu gridMenu = new JMenu("Grid");
      JMenuItem menuItemR = new JMenuItem("Change Rows...");
      JMenuItem menuItemC = new JMenuItem("Change Columns...");
      JMenuItem menuItemSR = new JMenuItem("Shift Rows...");
      JMenuItem menuItemSC = new JMenuItem("Shift Columns...");
      JMenuItem menuItemIR = new JMenuItem("Insert Rows...");
      JMenuItem menuItemIC = new JMenuItem("Insert Columns...");
      JMenuItem menuItemSUR = new JMenuItem("Surround...");
      JMenuItem menuItemINV = new JMenuItem("Invert");
      JMenuItem menuItemFV = new JMenuItem("Flip Vertically");
      JMenuItem menuItemFH = new JMenuItem("Flip Horizontally");
      JMenuItem menuItemMV = new JMenuItem("Mirror Vertically");
      JMenuItem menuItemMH = new JMenuItem("Mirror Horizontally");
      JMenuItem menuItemFILL = new JMenuItem("Fill Grid With Current Object");
      JCheckBoxMenuItem menuItemSL = new JCheckBoxMenuItem("Show Lines");
      menuItemR.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
      menuItemC.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
      menuItemSR.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
      menuItemSC.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
      menuItemSUR.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
      menuItemIR.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
      menuItemIC.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
      menuItemSL.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
      menuItemINV.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
      menuItemFV.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
      menuItemFH.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
      menuItemMV.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
      menuItemMH.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
      menuItemFILL.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
      addMenuItem(menuItemR,gridMenu,listener);
      addMenuItem(menuItemC,gridMenu,listener);
      gridMenu.addSeparator();
      addMenuItem(menuItemSR,gridMenu,listener);
      addMenuItem(menuItemSC,gridMenu,listener);
      gridMenu.addSeparator();
      addMenuItem(menuItemIR,gridMenu,listener);
      addMenuItem(menuItemIC,gridMenu,listener);
      gridMenu.addSeparator();
      addMenuItem(menuItemSUR,gridMenu,listener);
      gridMenu.addSeparator();
      addMenuItem(menuItemINV,gridMenu,listener);
      gridMenu.addSeparator();
      addMenuItem(menuItemFV,gridMenu,listener);
      addMenuItem(menuItemFH,gridMenu,listener);
      gridMenu.addSeparator();
      addMenuItem(menuItemMV,gridMenu,listener);
      addMenuItem(menuItemMH,gridMenu,listener);
      gridMenu.addSeparator();
      addMenuItem(menuItemFILL,gridMenu,listener);
      gridMenu.addSeparator();
      addMenuItem(menuItemSL,gridMenu,listener);
      menuItemSL.setSelected(true);
      menuBar.add(gridMenu);
      
      JMenu objMenu = new JMenu("Object");
      ButtonGroup group = new ButtonGroup();
      
      // Create a TreeMap of the object number and name and use an empty string for menu separators
      TreeMap<Integer,String>objNames = new TreeMap<Integer,String>();
      objNames.put(0,"Empty");
      objNames.put(1,"Player 1");
      objNames.put(2,"Player 2");
      objNames.put(99,"");
      objNames.put(100,"Destructible Block");
      objNames.put(101,"Semidestructible Block");
      objNames.put(102,"Reinforced Block");
      objNames.put(103,"Indestructible Block");
      objNames.put(109,"");
      objNames.put(110,"Player 1 Gate");
      objNames.put(120,"Player 2 Gate");
      objNames.put(199,"");
      objNames.put(200,"Proximity Mine...");
      objNames.put(201,"Remote Mine...");
      objNames.put(299,"");
      objNames.put(300,"Center Fire");
      objNames.put(301,"Horizontal Fire");
      objNames.put(302,"Vertical Fire");
      objNames.put(303,"Remote Mine Trigger...");
      objNames.put(304,"Warp...");
      objNames.put(329,"");
      objNames.put(330,"Radius Booster");
      objNames.put(331,"Max Live Explosives Booster");
      objNames.put(332,"Lives Booster");
      objNames.put(333,"Death Booster");
      objNames.put(334,"Unstoppable Bomb Booster");
      objNames.put(335,"Mine Booster");
      objNames.put(336,"Nuke Booster");
      // Add each item to the menu
      int ind = 0;
      Iterator<Integer> i = objNames.keySet().iterator();
      while(i.hasNext()){
        int key = i.next();
        if(objNames.get(key).equals("")){
            objMenu.addSeparator();
            key = i.next();
        }
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(objNames.get(key));
          if(ind < 10)
            item.setAccelerator(KeyStroke.getKeyStroke(48+ind, ActionEvent.CTRL_MASK)); // 48 = VK_0
          else if(ind < 22)
            item.setAccelerator(KeyStroke.getKeyStroke(112+ind-10, ActionEvent.CTRL_MASK)); // 112 = VK_F1
          ind++;
          group.add(item);
          addRMenuItem(item,objMenu,listener);
          if(key == 1)
            item.setSelected(true);
      }
      menuBar.add(objMenu);
 
      JMenu blocksMenu = new JMenu("Blocks");
      JRadioButtonMenuItem menuItemL1 = new JRadioButtonMenuItem("Normal");
      JRadioButtonMenuItem menuItemL2 = new JRadioButtonMenuItem("Shiny");
      JRadioButtonMenuItem menuItemL3 = new JRadioButtonMenuItem("Legacy");
      group = new ButtonGroup();
      group.add(menuItemL1);
      group.add(menuItemL2);
      group.add(menuItemL3);
      addRMenuItem(menuItemL1,blocksMenu,listener);
      addRMenuItem(menuItemL2,blocksMenu,listener);
      addRMenuItem(menuItemL3,blocksMenu,listener);
      menuItemL1.setSelected(true);
      menuBar.add(blocksMenu);
      
      JMenu aboutMenu = new JMenu("About");
      JMenuItem howToMenuItem = new JMenuItem("How To Create");
      JMenuItem aboutMenuItem = new JMenuItem("About This Tool");
      addMenuItem(howToMenuItem,aboutMenu,listener);
      addMenuItem(aboutMenuItem,aboutMenu,listener);
      menuBar.add(aboutMenu);
 
      return menuBar;
   }
   private void addMenuItem(JMenuItem menuItem, JMenu menu, ActionListener listener) {
      menuItem.addActionListener(listener);
      menu.add(menuItem);
   }
   private void addRMenuItem(JRadioButtonMenuItem menuItem, JMenu menu, ActionListener listener) {
      menuItem.setSelected(false);
      menuItem.addActionListener(listener);
      menu.add(menuItem);
   }
   private void save(String command){
        // generate data file
        FileOutputStream out;
        PrintStream p;
        try{ // show save dialog if Save As.. or no file chosen yet
            if(command.equals("Save As...") || dataFile == null){
                File tempDataFile = BombermanPanel.getFileFromSaveDialog(window,dataFile);
                if(tempDataFile == null) // choosing file failed
                    return;
                else
                    dataFile = tempDataFile;
            }
            out = new FileOutputStream(dataFile);
            p = new PrintStream(out);
            // write numbers to data file
            for(int row = 0; row < grid.getHeight(); row++){
                for(int col = 0; col < grid.getWidth(); col++){
                     Locatable obj = grid.getObjectAt(col,row);
                     if(obj instanceof Player)
                        p.print(((Player)obj).getNumber());
                     else if(obj instanceof DestructibleBlock)
                        p.print("100");
                     else if(obj instanceof GateBlock && ((GateBlock)obj).getOwner().getNumber() == 1)
                        p.print("110");
                     else if(obj instanceof GateBlock && ((GateBlock)obj).getOwner().getNumber() == 2)
                        p.print("120");
                     else if(obj instanceof SemidestructibleBlock)
                        p.print("101");
                     else if(obj instanceof ReinforcedBlock)
                        p.print("102");
                     else if(obj instanceof IndestructibleBlock)
                        p.print("103");
                     else if(obj instanceof ProximityMine)
                        p.print("200-"+((ProximityMine)obj).getRadius());
                     else if(obj instanceof RemoteMine)
                        p.print("201-"+((RemoteMine)obj).getSet()+","+((RemoteMine)obj).getRadius());
                     else if(obj instanceof Fire)
                        p.print("30"+((Fire)obj).getDir());
                     else if(obj instanceof RemoteMineTrigger)
                        p.print("303-"+al2csv(((RemoteMineTrigger)obj).getSets()));
                     else if(obj instanceof Warp)
                        p.print("304-"+((Warp)obj).getTargetX()+","+((Warp)obj).getTargetY());
                     else if(obj instanceof RadiusBooster)
                        p.print("330");
                     else if(obj instanceof MaxLiveExplosivesBooster)
                        p.print("331");
                     else if(obj instanceof LivesBooster)
                        p.print("332");
                     else if(obj instanceof DeathBooster)
                        p.print("333");
                     else if(obj instanceof UnstoppableBombBooster)
                        p.print("334");
                     else if(obj instanceof MineBooster)
                        p.print("335");
                     else if(obj instanceof NukeBooster)
                        p.print("336");
                     else
                        p.print("0");
                     if(col < grid.getWidth() - 1)
                        p.print(" ");
                }
                p.println();
            }
            saved = true;
        }catch(Exception e){
            System.out.println("Error writing to data file: "+dataFile.getPath());   
        }  
   }
   private class MenuHandler implements ActionListener {
      public void actionPerformed(ActionEvent evt) {
         String command = evt.getActionCommand();
         if(command.equals("New")){ // new level
            doNewLevel(null);
            saved = true;
         }else if(command.contains("Save")){ // save or save as
            save(command);
        }else if(command.equals("Load...")){ // load level
            doNewLevel(new File(""));
            saved = true;
        }else if(command.equals("Play...")){ // load level
            save("Save");
            if(dataFile == null)
                return;
            String [] args = new String [1];
            args[0] = dataFile.getPath();
            Bomberman.main(args);
        }else if(command.equals("Close")) // close level maker
            window.dispose();
        else if(command.equals("Change Rows...")){ // add or remove row(s) from the bottom
            grid.setRows(BombermanPanel.getRespFromDiag("Enter a new number of rows for the grid:","Change Rows",grid.getHeight(),grid.getHeight(),1,Integer.MAX_VALUE));
            saved = false;
        }else if(command.equals("Change Columns...")){ // add or remove col(s) from the bottom
            grid.setCols(BombermanPanel.getRespFromDiag("Enter a new number of columns for the grid:","Change Columns",grid.getWidth(),grid.getWidth(),1,Integer.MAX_VALUE));
            saved = false;
        }else if(command.equals("Shift Rows...")){ // add or remove row(s) from the top
            grid.shiftRows(BombermanPanel.getRespFromDiag("Enter a number to shift rows up or down by for the grid:","Shift Rows",0,null,grid.getHeight() * -1+1,Integer.MAX_VALUE));
            saved = false;
        }else if(command.equals("Shift Columns...")){ // add or remove col(s) from the left
            grid.shiftCols(BombermanPanel.getRespFromDiag("Enter a number to shift columns left or right by for the grid:","Shift Columns",0,null,grid.getWidth() * -1+1,Integer.MAX_VALUE));
            saved = false;
        }else if(command.equals("Insert Rows...")){ // insert row(s) in the middle of the Grid
            Integer insertAt = BombermanPanel.getRespFromDiag("Enter a row number to insert more rows before:","Insert At",null,null,0,grid.getHeight());
            if(insertAt != null){
                Integer count = BombermanPanel.getRespFromDiag("Enter the number of rows to insert:","Insert Count",null,null,insertAt * -1,Integer.MAX_VALUE);
                if(count != null)
                    grid.insertRows(insertAt,count);
            }
            saved = false;
        }else if(command.equals("Insert Columns...")){ // insert col(s) in the middle of the Grid
            Integer insertAt = BombermanPanel.getRespFromDiag("Enter a column number to insert more columns before:","Insert At",null,null,0,grid.getWidth());
            if(insertAt != null){
                Integer count = BombermanPanel.getRespFromDiag("Enter the number of columns to insert:","Insert Count",null,null,insertAt * -1,Integer.MAX_VALUE);
                if(count != null)
                    grid.insertCols(insertAt,count);
            }
            saved = false;
        }else if(command.equals("Surround...")){ // add row(s) and col(s) around the entire grid
            int smaller = grid.getWidth()/2;
            if(grid.getHeight()/2 < smaller)
                smaller = grid.getHeight()/2;
            grid.surround(BombermanPanel.getRespFromDiag("Enter an amount of padding to surround the grid with:","Surround Grid",0,null,smaller*-1+1,Integer.MAX_VALUE));
            saved = false;
        }else if(command.equals("Fill Grid With Current Object")){
            grid.fillWith(curObjInd);
            grid.setBlockImages(blockImageSet);
            saved = false;
        }else if(command.equals("Invert")){
            grid.invert();
            saved = false;
        }else if(command.equals("Flip Vertically")){
            grid.flipVert();
            saved = false;
        }else if(command.equals("Flip Horizontally")){
            grid.flipHoriz();
            saved = false;
        }else if(command.equals("Mirror Vertically")){
            grid.mirVert();
            saved = false;
        }else if(command.equals("Mirror Horizontally")){
            grid.mirHoriz();
            saved = false;
        }else if(command.equals("Show Lines"))
            showGridLines = ((JCheckBoxMenuItem)(evt.getSource())).getState();
        else if(command.equals("Empty"))
            curObjInd = 0;
        else if(command.equals("Player 1"))
            curObjInd = 1;
        else if(command.equals("Player 2"))
            curObjInd = 2;
        else if(command.equals("Destructible Block"))
            curObjInd = 100;
        else if(command.equals("Semidestructible Block"))
            curObjInd = 101;
        else if(command.equals("Reinforced Block"))
            curObjInd = 102;
        else if(command.equals("Indestructible Block"))
            curObjInd = 103;
        else if(command.equals("Player 1 Gate"))
            curObjInd = 110;
        else if(command.equals("Player 2 Gate"))
            curObjInd = 120;
        else if(command.equals("Proximity Mine...")){
            curObjInd = 200;
            curProxMineRadius = BombermanPanel.getRespFromDiag("Enter a radius for these proximity mines (2 = default):","Proximity Mine Radius",curProxMineRadius,curProxMineRadius,2,Integer.MAX_VALUE);
        }else if(command.equals("Remote Mine...")){
            curObjInd = 201;
            curRemoteMineSet = BombermanPanel.getRespFromDiag("Enter a set number for these remote mines (0 = no set):","Remote Mine Set",curRemoteMineSet,curRemoteMineSet,0,Integer.MAX_VALUE);
            curRemoteMineRadius = BombermanPanel.getRespFromDiag("Enter a radius for these remote mines (2 = default):","Remote Mine Radius",curRemoteMineRadius,curRemoteMineRadius,2,Integer.MAX_VALUE);
        }else if(command.equals("Center Fire"))
            curObjInd = 300;
        else if(command.equals("Horizontal Fire"))
            curObjInd = 301;
        else if(command.equals("Vertical Fire"))
            curObjInd = 302;
        else if(command.equals("Remote Mine Trigger...")){
            curObjInd = 303;
            ArrayList<Integer>tempSets = new ArrayList<Integer>();
            int temp = 0;
            do{
                temp = BombermanPanel.getRespFromDiag("Enter a remote mine set for these remote mine triggers to correspond with:\nCurrent set(s): "+tempSets+" (0 = done adding sets, -X to remove set X)","Remote Mine Trigger Set",0,0,Integer.MIN_VALUE,Integer.MAX_VALUE);
                if(temp > 0)
                    tempSets.add(temp);
                if(temp < 0 && tempSets.contains(temp * -1))
                    tempSets.remove(tempSets.indexOf(temp * -1));
                // order and remove duplicates by putting ArrayList in and out of a TreeSet
                TreeSet<Integer> ts = new TreeSet<Integer>(tempSets);
                tempSets = new ArrayList<Integer>(ts);
            }while(temp != 0);
            // if nothing is in sets then use old sets
            if(tempSets.size() == 0)
                 tempSets = curRemoteMineTriggerSets;
            else
                 curRemoteMineTriggerSets = tempSets;
        }else if(command.equals("Warp...")){
            curObjInd = 304;
            curWarpTargetX = BombermanPanel.getRespFromDiag("Enter the target X coordinate for this warp:","Warp Target X",curWarpTargetX,curWarpTargetX,0,grid.getWidth()-1);
            curWarpTargetY = BombermanPanel.getRespFromDiag("Enter the target Y coordinate for this warp:","Warp Target Y",curWarpTargetY,curWarpTargetY,0,grid.getHeight()-1);
        }else if(command.equals("Radius Booster"))
            curObjInd = 330;
        else if(command.equals("Max Live Explosives Booster"))
            curObjInd = 331;
        else if(command.equals("Lives Booster"))
            curObjInd = 332;
        else if(command.equals("Death Booster"))
            curObjInd = 333;
        else if(command.equals("Unstoppable Bomb Booster"))
            curObjInd = 334;
        else if(command.equals("Mine Booster"))
            curObjInd = 335;
        else if(command.equals("Nuke Booster"))
            curObjInd = 336;
        else if (command.equals("Normal")){
            blockImageSet = 0;
            grid.setBlockImages(0);
        }else if (command.equals("Shiny")){
            blockImageSet = 1;
            grid.setBlockImages(1);  
        }else if (command.equals("Legacy")){
            blockImageSet = 2;
            grid.setBlockImages(2);
        }else if (command.equals("How To Create"))
            JOptionPane.showMessageDialog(null,"Create a level or edit an existing one by placing objects on the grid. Save as plaintext.\nRight-click to delete objects. Coordinate grid starts at (0,0) at the top left.","How To",1);   
        else if (command.equals("About This Tool"))
            JOptionPane.showMessageDialog(null,"Level Maker was created by Bryan Mishkin in November 2007 for Bomberman.","About",1);
       }
   }
}
