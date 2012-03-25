/**
 * class BombermanPanel
 * 
 * @version 2007-11-07
 */
import java.awt.*;        
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLDecoder;
public class BombermanPanel extends JPanel {
   
   public static final String cver = "2008-01-13";
   public static final String webPath = ""; 

   private static long time;
   private static long resett;
   private static long missedt;
   private static long mstart;
   private static long gameStartTime;
   private static boolean resettime;
   private static boolean stopTimers;
   private static boolean firstTime;
   private Timer timer;
   private Timer timer1;
   private Timer timer2;
   private int timerCount,powerupInterval,explosiveDuration;

   private Grid grid;
   private File levelFile;
   private int blockImageSet;
   
   private ArrayList<JLabel>statsLabels;
   private JLabel infoLabel;
    
   private boolean showStats;
   private boolean alwaysDropPowerups;
   
   private JFrame window;
   private JPanel content,bottomPanel,statsPanel;
    
   /**
    * The constructor sets the background color of the panel, creates the
    * timer, and adds a KeyListener, FocusListener, and MouseListener to the
    * panel.  These listeners, as well as the ActionListener for the timer
    * are defined by anonymous inner classes.  The timer will run only
    * when the panel has the input focus.
    */
   public BombermanPanel(JFrame newWindow,JPanel newContent,JPanel newBottomPanel,JPanel newStatsPanel, ArrayList<JLabel> newStatsLabels,JLabel newInfoLabel,String levelName) {
      // initialize timer variables
      resettime=false;
      stopTimers=false;
      firstTime = true;
      time=System.currentTimeMillis();
      resett=System.currentTimeMillis();
      gameStartTime = 0;
      timerCount = 0;
      
      // initialize windows and panels
      window = newWindow;
      content = newContent;
      bottomPanel = newBottomPanel;
      statsPanel = newStatsPanel;
      
      // instance variables
      showStats = true;
      alwaysDropPowerups = false;
      blockImageSet = 0;
      statsLabels = newStatsLabels;
      
      // initial infoLabel text
      infoLabel = newInfoLabel;
      infoLabel.setText("Click Grid To Start");
      infoLabel.setFont(new Font(null,Font.BOLD,16));
      
      // initialize Player/Explosive labels
      
      statsLabels.get(0).setText("Player 1:");
      statsLabels.get(0).setFont(new Font(null,Font.BOLD,16));
      statsLabels.get(1).setText("Player 2:");
      statsLabels.get(1).setFont(new Font(null,Font.BOLD,16));
      
      statsLabels.get(6).setText("Explosive:");
      statsLabels.get(6).setFont(new Font(null,Font.BOLD,16));
      statsLabels.get(7).setText("Explosive:");
      statsLabels.get(7).setFont(new Font(null,Font.BOLD,16));
      
      // set bg color
      setBackground(Color.GRAY);
      
      // load the starting level and create new game
      levelFile = new File(levelName);
      doNewGame(levelFile);
      
      ActionListener p1animation = new ActionListener() {
         // Defines the action taken each time the timer fires.
         public void actionPerformed(ActionEvent evt) {
            if (grid.getPlayer1().getImage().equals(grid.getImage(11)))
                grid.getPlayer1().setImage(12);
            else if (grid.getPlayer1().getImage().equals(grid.getImage(13)))
                grid.getPlayer1().setImage(14);
            else if (grid.getPlayer1().getImage().equals(grid.getImage(15)))
                grid.getPlayer1().setImage(16);
            else if (grid.getPlayer1().getImage().equals(grid.getImage(17)))
                grid.getPlayer1().setImage(18);
            timer1.stop();
         }
      };
      ActionListener p2animation = new ActionListener() {
         // Defines the action taken each time the timer fires.
         public void actionPerformed(ActionEvent evt) {
            if (grid.getPlayer2().getImage().equals(grid.getImage(25)))
                grid.getPlayer2().setImage(26);
            else if (grid.getPlayer2().getImage().equals(grid.getImage(27)))
                grid.getPlayer2().setImage(28);
            else if (grid.getPlayer2().getImage().equals(grid.getImage(29)))
                grid.getPlayer2().setImage(30);
            else if (grid.getPlayer2().getImage().equals(grid.getImage(31)))
                grid.getPlayer2().setImage(32);
            timer2.stop();
         }
      };
      ActionListener action = new ActionListener() {
         // Defines the action taken each time the timer fires.
         public void actionPerformed(ActionEvent evt) {
            timerCount++;
            
            // Count down every Explosive in the grid
            ArrayList<Explosive> explosives = grid.getExplosiveArray();
            for(int ind = 0; ind < explosives.size(); ind++){
                if(explosives.get(ind).countDown()) // if exploded
                    updateStats();
            }
            
            // Add random Powerup if no DestructibleBlocks left
            if((grid.getNbrDestructibleBlocks() == 0 || alwaysDropPowerups) && timerCount % powerupInterval == 0){
                if(powerupInterval > 10)
                    powerupInterval -=2; // speed up random Powerups
                int x,y;
                do{ // find an empty location or the location of a Powerup to add a new random Powerup in
                    x = (int)(Math.random()*grid.getWidth());
                    y = (int)(Math.random()*grid.getHeight());
                }while(!grid.isEmpty(x,y) && !grid.isBooster(x,y) && grid.getNbrObjects() - grid.getNbrBoosters() < grid.getWidth() * grid.getHeight());
                if(grid.getNbrObjects() - grid.getNbrBoosters() < grid.getWidth() * grid.getHeight())
                    grid.addRandomPowerupTo(x,y); // add Powerup if there was space for it
            }
                       
            // Stop timers and clear Grid if game is over
            if(grid.getPlayer1().getLivesLeft() <=0 || grid.getPlayer2().getLivesLeft() <= 0){
                timer.stop();
                timer1.stop();
                timer2.stop();
                stopTimers = true;
                grid.clear();
            }

            // Update infoLabel with time or winner
            int seconds = (int)((System.currentTimeMillis()-gameStartTime)/1000);
            if(grid.getPlayer1().getLivesLeft() <= 0)
                infoLabel.setText("Player 2 Wins After "+seconds+" Seconds");
            else if(grid.getPlayer2().getLivesLeft() <= 0)
                infoLabel.setText("Player 1 Wins After "+seconds+" Seconds");
            else if(!infoLabel.getText().equals("Game Length: "+seconds+" Seconds"))
                infoLabel.setText("Game Length: "+seconds+" Seconds");

            // Show or Hide the statsPanel (only does something if "Show Stats" was toggled)
            if(showStats && bottomPanel.getComponentCount() == 1){
                bottomPanel.add(statsPanel,BorderLayout.CENTER);
                bottomPanel.validate(); // resizes the grid to allow room for statsPanel 
            }else if(!showStats && bottomPanel.getComponentCount() == 2){
                bottomPanel.remove(statsPanel);
                bottomPanel.validate(); // resizes the grid to take up the whole window 
            }
            repaint();
         }
      }; 
      timer1 = new Timer( 300, p1animation );
      timer2 = new Timer( 300, p2animation );
      timer = new Timer( 30, action );
      
      addMouseListener( new MouseAdapter() {
              // The mouse listener simply requests focus when the user
              // clicks the panel.
         public void mousePressed(MouseEvent evt) {
            if(firstTime){
                firstTime = false;
                gameStartTime = System.currentTimeMillis();
            }
            requestFocus();
         }
      } );
      
      addFocusListener( new FocusListener() {
             // The focus listener starts the timer when the panel gains
             // the input focus and stops the timer when the panel loses
             // the focus.  It also calls repaint() when these events occur.
         public void focusGained(FocusEvent evt) {
            if (resettime){
                resett=System.currentTimeMillis();
                resettime=false;
                missedt=0;
            }else if (mstart!=0){
                missedt+=(System.currentTimeMillis()-mstart);
                gameStartTime+=(System.currentTimeMillis()-mstart);
                mstart=0;
            }
            if(!stopTimers){
                timer.start();
                timer1.start();
                timer2.start();
            }
            repaint();
         }
         public void focusLost(FocusEvent evt) {
            mstart=System.currentTimeMillis();
            timer.stop();
            timer1.stop();
            timer2.stop();
            repaint();
         }
      } );
      
      addKeyListener( new KeyAdapter() {
         public void keyPressed(KeyEvent evt) {
            int code = evt.getKeyCode();  // which key was pressed.
            Player player1 = grid.getPlayer1();
            Player player2 = grid.getPlayer2();
            // player 1 movement
            if (code == KeyEvent.VK_A) {
                if(player1.getLivesLeft() > 0 && grid.isMovableTo(player1.getX()-1,player1.getY())){
                    player1.moveTo(player1.getX()-1,player1.getY());
                    player1.setImage(15);
                    timer1.restart();
                }
            }else if (code == KeyEvent.VK_D) {
                if(player1.getLivesLeft() > 0 && grid.isMovableTo(player1.getX()+1,player1.getY())){
                    player1.moveTo(player1.getX()+1,player1.getY());
                    player1.setImage(17);
                    timer1.restart();
                }
            }else if (code == KeyEvent.VK_W) {
                if(player1.getLivesLeft() > 0 && grid.isMovableTo(player1.getX(),player1.getY()-1)){
                    player1.moveTo(player1.getX(),player1.getY()-1);
                    player1.setImage(11);
                    timer1.restart();
                }
            }else if (code == KeyEvent.VK_S) {
                if(player1.getLivesLeft() > 0 && grid.isMovableTo(player1.getX(),player1.getY()+1)){
                    player1.moveTo(player1.getX(),player1.getY()+1);
                    player1.setImage(13);
                    timer1.restart();
                }
            // player 2 movement
            }else if (code == KeyEvent.VK_LEFT) {
                if(player2.getLivesLeft() > 0 && grid.isMovableTo(player2.getX()-1,player2.getY())){   
                    player2.moveTo(player2.getX()-1,player2.getY());
                    player2.setImage(29);
                    timer2.restart();
                }
            }else if (code == KeyEvent.VK_RIGHT) {
                if(player2.getLivesLeft() > 0 && grid.isMovableTo(player2.getX()+1,player2.getY())){
                    player2.moveTo(player2.getX()+1,player2.getY());
                    player2.setImage(31);
                    timer2.restart();
                }
            }else if (code == KeyEvent.VK_UP) {
                if(player2.getLivesLeft() > 0 && grid.isMovableTo(player2.getX(),player2.getY()-1)){
                    player2.moveTo(player2.getX(),player2.getY()-1);
                    player2.setImage(25);
                    timer2.restart();
                }
            }else if (code == KeyEvent.VK_DOWN) {
                if(player2.getLivesLeft() > 0 && grid.isMovableTo(player2.getX(),player2.getY()+1)){
                    player2.moveTo(player2.getX(),player2.getY()+1);
                    player2.setImage(27);
                    timer2.restart();
                }
            // player 1 dropped an Explosive
            }else if (code == KeyEvent.VK_SPACE && player1.getLivesLeft() > 0 && grid.getLiveExplosiveCount(player1) < player1.getMaxLiveExplosives() && grid.isValidCoordinate(player1.getX(),player1.getY()))
               player1.plantExplosive();
            // player 2 dropped an Explosive
            else if ((code == KeyEvent.VK_0 || code == KeyEvent.VK_NUMPAD0) && player2.getLivesLeft() > 0 && grid.getLiveExplosiveCount(player2) < player2.getMaxLiveExplosives() && grid.isValidCoordinate(player2.getX(),player2.getY()))
               player2.plantExplosive();
            updateStats();
         }
      } );
      window.setJMenuBar( getMenuBar() );    // Use the menu bar from the controller.
   }
   private static void reset(){
       resettime=true;
   }
   private void updateStats(){
        if(showStats){
            Player player1 = grid.getPlayer1();
            Player player2 = grid.getPlayer2();
            statsLabels.get(2).setText(" Score: "+player1.getScore());
            statsLabels.get(3).setText("Score: "+player2.getScore()+" ");
            statsLabels.get(4).setText(" Lives: "+player1.getLivesLeft());
            statsLabels.get(5).setText("Lives: "+player2.getLivesLeft()+" ");
            statsLabels.get(8).setText(" Radius: "+player1.getExplosiveRadius());
            statsLabels.get(9).setText("Radius: "+player2.getExplosiveRadius()+" ");
            statsLabels.get(10).setText(" Count: "+player1.getMaxLiveExplosives());
            statsLabels.get(11).setText("Count: "+player2.getMaxLiveExplosives()+" ");
            statsLabels.get(12).setText(" Duration: "+player1.getExplosiveDuration());
            statsLabels.get(13).setText("Duration: "+player2.getExplosiveDuration()+" ");
            String player1ExplosiveType = "";
            String player2ExplosiveType = "";
            if(player1.getExplosiveType() == 0)
                player1ExplosiveType = "Bomb";
            else if(player1.getExplosiveType() == 1)
                player1ExplosiveType = "Unstoppable Bomb";
            else if(player1.getExplosiveType() == 2)
                player1ExplosiveType = "Mine";
            else if(player1.getExplosiveType() == 3)
                player1ExplosiveType = "Nuke";
            if(player2.getExplosiveType() == 0)
                player2ExplosiveType = "Bomb";
            else if(player2.getExplosiveType() == 1)
                player2ExplosiveType = "Unstoppable Bomb";
            else if(player2.getExplosiveType() == 2)
                player2ExplosiveType = "Mine";
            else if(player2.getExplosiveType() == 3)
                player2ExplosiveType = "Nuke";
            statsLabels.get(14).setText(" Type: "+player1ExplosiveType);
            statsLabels.get(15).setText("Type: "+player2ExplosiveType+" ");
        }
   }
   private void doNewGame(File newFile){
      // create the grid from the file
      grid = new Grid(newFile);
      powerupInterval = 100;
      // set the grid of each object
      grid.setAllGrids();
      // update images for Blocks
      grid.setBlockImages(blockImageSet);
      // update stats to defaults
      updateStats();
      // prepare timers
      gameStartTime = System.currentTimeMillis();
      stopTimers = false;
      // update window title
      window.setTitle("Bomberman: "+levelFile.getName());
    }
   private JMenuBar getMenuBar() {
      JMenuBar menuBar = new JMenuBar();
      MenuHandler listener = new MenuHandler();
      
      JMenu gameMenu = new JMenu("Game");
      JMenuItem menuItemN = new JMenuItem("New Local Game");
      JMenuItem menuItemW = new JMenuItem("Join Web Game...");
      JMenuItem menuItemL = new JMenuItem("Level Maker");
      JCheckBoxMenuItem menuItemS = new JCheckBoxMenuItem("Show Stats");
      JCheckBoxMenuItem menuItemD = new JCheckBoxMenuItem("Always Drop Powerups");
      menuItemN.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
      menuItemW.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
      menuItemL.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
      menuItemS.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
      menuItemD.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
      menuItemS.setSelected(true);
      menuItemD.setSelected(false);
      addMenuItem(menuItemN,gameMenu,listener);
      //addMenuItem(menuItemW,gameMenu,listener);
      gameMenu.addSeparator();
      addMenuItem(menuItemL,gameMenu,listener);
      gameMenu.addSeparator();
      addMenuItem(menuItemS,gameMenu,listener);
      addMenuItem(menuItemD,gameMenu,listener);
      menuBar.add(gameMenu);
      
      // Count how many levelx.txt there are starting at level1.txt
      int levelCount = 0;
      File file = new File("level"+(levelCount+1)+".txt");
      while(grid.getScannerFromFile(file) != null){
          levelCount++;
          file = new File("level"+(levelCount+1)+".txt");
      }
      
      // Only show Level menu if there is at least one level
      JMenu levelMenu = new JMenu("Level");
      ButtonGroup group = new ButtonGroup();    
      //if(levelCount > 0)
            menuBar.add(levelMenu);   
      
      // Add level x MenuItems
      for(int ind = 1; ind <= levelCount; ind++){
          JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem("Level "+ind);
          if(ind < 10)
            menuItem.setAccelerator(KeyStroke.getKeyStroke(48+ind, ActionEvent.CTRL_MASK)); // 48 = VK_0
          else if(ind < 22)
            menuItem.setAccelerator(KeyStroke.getKeyStroke(112+ind-10, ActionEvent.CTRL_MASK)); // 112 = VK_F1
          group.add(menuItem);
          if(ind == 1)
            menuItem.setSelected(true);
          addRMenuItem(menuItem,levelMenu,listener);
      }
      // Custom... MenuItem
      JRadioButtonMenuItem menuItemLC = new JRadioButtonMenuItem("Custom...");
      menuItemLC.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
      group.add(menuItemLC);
      addRMenuItem(menuItemLC,levelMenu,listener);
      // Edit... MenuItem
      levelMenu.addSeparator();
      JMenuItem menuItemED = new JMenuItem("Edit");
      menuItemED.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
      addMenuItem(menuItemED,levelMenu,listener);
      
      JMenu explosivesMenu = new JMenu("Explosives");
      JMenuItem bombMenuItem = new JMenuItem("Bomb");
      JMenuItem unstoppableBombMenuItem = new JMenuItem("Unstoppable Bomb");
      JMenuItem mineMenuItem = new JMenuItem("Mine");
      JMenuItem nukeMenuItem = new JMenuItem("Nuke");
      addMenuItem(bombMenuItem,explosivesMenu,listener);
      addMenuItem(unstoppableBombMenuItem,explosivesMenu,listener);
      addMenuItem(mineMenuItem,explosivesMenu,listener);
      addMenuItem(nukeMenuItem,explosivesMenu,listener);
      menuBar.add(explosivesMenu);
      
      JMenu powerupsMenu = new JMenu("Powerups");
      JMenuItem livesMenuItem = new JMenuItem("Change Lives...");
      JMenuItem radiusMenuItem = new JMenuItem("Change Radius...");
      JMenuItem countMenuItem = new JMenuItem("Change Count...");
      JMenuItem durationMenuItem = new JMenuItem("Change Duration...");
      JMenuItem dropRateMenuItem = new JMenuItem("Change Drop Rate...");
      addMenuItem(livesMenuItem,powerupsMenu,listener);
      addMenuItem(radiusMenuItem,powerupsMenu,listener);
      addMenuItem(countMenuItem,powerupsMenu,listener);
      addMenuItem(durationMenuItem,powerupsMenu,listener);
      addMenuItem(dropRateMenuItem,powerupsMenu,listener);
      menuBar.add(powerupsMenu);    
      
      JMenu blocksMenu = new JMenu("Blocks");
      JRadioButtonMenuItem menuItemNormal = new JRadioButtonMenuItem("Normal");
      JRadioButtonMenuItem menuItemShiny = new JRadioButtonMenuItem("Shiny");
      JRadioButtonMenuItem menuItemLegacy = new JRadioButtonMenuItem("Legacy");
      group = new ButtonGroup();
      group.add(menuItemNormal);
      group.add(menuItemShiny);
      group.add(menuItemLegacy);
      addRMenuItem(menuItemNormal,blocksMenu,listener);
      addRMenuItem(menuItemShiny,blocksMenu,listener);
      addRMenuItem(menuItemLegacy,blocksMenu,listener);
      menuItemNormal.setSelected(true);
      menuBar.add(blocksMenu);
      
      JMenu aboutMenu = new JMenu("About");
      JMenuItem howToMenuItem = new JMenuItem("How To Play");
      JMenuItem aboutMenuItem = new JMenuItem("About This Game");
      JMenuItem updatesMenuItem = new JMenuItem("Check For Updates");
      JMenuItem clMenuItem = new JMenuItem("View Changelog");
      addMenuItem(howToMenuItem,aboutMenu,listener);
      addMenuItem(aboutMenuItem,aboutMenu,listener);
      addMenuItem(updatesMenuItem,aboutMenu,listener);
      addMenuItem(clMenuItem,aboutMenu,listener);
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
   
   // static methods
   public static File getFileFromOpenDialog(Component c,File def){
        //Create a file chooser
        final JFileChooser fc = new JFileChooser(def);
        fc.setSelectedFile(def);
        //In response to a button click:
        if (fc.showOpenDialog(c) == JFileChooser.APPROVE_OPTION)
             return fc.getSelectedFile();
        return null;
    }
    public static File getFileFromSaveDialog(Component c,File def){
        //Create a file chooser
        final JFileChooser fc = new JFileChooser(def);
        fc.setSelectedFile(def);
        //In response to a button click:
        if (fc.showSaveDialog(c) == JFileChooser.APPROVE_OPTION)
             return fc.getSelectedFile();
        return null;
    }
    public static ArrayList<String> getLineArrayFromAddr(String addr){
        ArrayList<String>arr = new ArrayList<String>();
        try{
            URL url = new URL(addr);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
               arr.add(inputLine);
            in.close();
            arr = decAL(arr);
        }catch(Exception e){
            arr = null;
        }
        return arr;
    }
    public static String encURL(String s){
        String n = new String();
        try{
            n = URLEncoder.encode(s,"UTF-8");
        }catch(Exception e){}
        return n;
    }
    public static String decURL(String s){
        String n = new String();
        try{
            n = URLDecoder.decode(s,"UTF-8");
        }catch(Exception e){}
        return n;
    }
    public static ArrayList<String> decAL(ArrayList<String>al){
        for(int ind = 0; ind < al.size(); ind++){
            al.set(ind,decURL(al.get(ind)));   
        }
        return al;
    }
    
   private class MenuHandler implements ActionListener {
      public void actionPerformed(ActionEvent evt) { 
         Player player1 = grid.getPlayer1();
         Player player2 = grid.getPlayer2();         
         String command = evt.getActionCommand();
         if (command.equals("New Local Game"))
            doNewGame(levelFile);
         else if (command.equals("Join Web Game..."))
            Lobby.main(null);
         else if (command.equals("Level Maker"))
            LevelMaker.main(null);
         else if (command.contains("Level ")){
            levelFile = new File("level"+command.substring(6)+".txt");
            doNewGame(levelFile);
         }else if (command.equals("Custom...")){
            //Create a file chooser
            File file = getFileFromOpenDialog(window,levelFile);
            if (file != null){
                 levelFile = file;
                 doNewGame(levelFile);
            }
         }else if (command.equals("Edit")){
            String [] args = new String [1];
            args[0] = levelFile.getName();
            LevelMaker.main(args);
         }else if (command.equals("Bomb")){
            player1.setExplosiveType(0);
            player1.setDefaultExplosiveType(0);
            player2.setExplosiveType(0);
            player2.setDefaultExplosiveType(0);
         }else if (command.equals("Unstoppable Bomb")){
            player1.setExplosiveType(1);
            player1.setDefaultExplosiveType(1);
            player2.setExplosiveType(1);
            player2.setDefaultExplosiveType(1);
         }else if (command.equals("Mine")){
            player1.setExplosiveType(2);
            player1.setDefaultExplosiveType(2);
            player2.setExplosiveType(2);
            player2.setDefaultExplosiveType(2);
         }else if (command.equals("Nuke")){
            player1.setExplosiveType(3);
            player1.setDefaultExplosiveType(3);
            player2.setExplosiveType(3);
            player2.setDefaultExplosiveType(3);
         }else if (command.equals("Change Lives...")){
            Integer resp = getRespFromDiag("Enter a new amount of lives for both players:","Change Lives",null,null,1,Integer.MAX_VALUE);
            if(resp != null){
                player1.setLivesLeft(resp);
                player2.setLivesLeft(resp);
            }
         }else if (command.equals("Change Radius...")){
            Integer resp = getRespFromDiag("Enter a new explosive radius for both players:","Change Radius",null,null,1,Integer.MAX_VALUE);
            if(resp != null){
                player1.setExplosiveRadius(resp);
                player1.setDefaultExplosiveRadius(resp);
                player2.setExplosiveRadius(resp);
                player2.setDefaultExplosiveRadius(resp);
            }
         }else if (command.equals("Change Count...")){
            Integer resp = getRespFromDiag("Enter a new explosive count for both players:","Change Count",null,null,1,Integer.MAX_VALUE);
            if(resp != null){
                player1.setDefaultMaxLiveExplosives(resp);
                player1.setMaxLiveExplosives(resp);
                player2.setDefaultMaxLiveExplosives(resp);
                player2.setMaxLiveExplosives(resp);
            }
         }else if (command.equals("Change Duration...")){
            Integer resp = getRespFromDiag("Enter a new explosive duration in seconds for both players:","Change Duration",null,null,1,Integer.MAX_VALUE);
            if(resp != null){
                player1.setExplosiveDuration(resp);
                player1.setDefaultExplosiveDuration(resp);
                player2.setExplosiveDuration(resp);
                player2.setDefaultExplosiveDuration(resp);
            }
         }else if (command.equals("Change Drop Rate...")){
            powerupInterval = getRespFromDiag("Enter a new drop rate for random powerups when all non-indestructible Blocks are gone:","Change Drop Rate",powerupInterval,powerupInterval,1,Integer.MAX_VALUE);
         }else if (command.equals("How To Play")){
            JOptionPane.showMessageDialog(null,"Player 1 use WASD for movement. Player 2 use arrow keys.\nPlayer 1 drop explosive with space bar. Player 2 with (numpad) 0.","How To",1);   
         }else if (command.equals("About This Game")){    
            JOptionPane.showMessageDialog(null,"Bomberman was created by Bryan Mishkin and David Wickland in November 2007.\nVersion "+cver+".","About",1);
         }else if (command.equals("Check For Updates")){
            ArrayList<String>lines = getLineArrayFromAddr(webPath+"query.php?action=latest_version");
            String mess = "\n"+webPath;
            String lver = "Unknown"; // latest version
            if(lines != null && lines.size() > 0)
               lver = lines.get(0).trim();
            if(cver.equals(lver))
               mess = "\nYour version is up-to-date."+mess;
            else if(!lver.equals("Unknown"))
               mess = "\nYour version is outdated."+mess;
            JOptionPane.showMessageDialog(null,"This Version: "+cver+". Latest Version: "+lver+"."+mess,"Check For Updates",1);   
         }else if (command.equals("View Changelog")){
            Changelog.main(null);
         }else if (command.equals("Show Stats")){
            showStats = ((JCheckBoxMenuItem)(evt.getSource())).getState();
         }else if (command.equals("Always Drop Powerups")){
            alwaysDropPowerups = ((JCheckBoxMenuItem)(evt.getSource())).getState();
         }else if (command.equals("Normal")){
            blockImageSet = 0;
            grid.setBlockImages(blockImageSet);
         }else if (command.equals("Shiny")){
            blockImageSet = 1;
            grid.setBlockImages(blockImageSet);  
         }else if (command.equals("Legacy")){
            blockImageSet = 2;
            grid.setBlockImages(blockImageSet);
         }
         updateStats();
      }
   }
   public static Integer getRespFromDiag(String msg,String title,Integer cur,Integer def,int min,int max){
       // shows an input box dialog requiring an integer. Parameters: message above the input box, title of the message window, 
       // current value to return if user cancels input, default value of input box, minimum value allowed, maximum value allowed.
       int resp = 0;
       boolean fail1,fail2;
       String input;
       do{
           resp = 0;
           fail1 = false;
           fail2 = false;
           input = (String)JOptionPane.showInputDialog(null,msg,title,1,null,null,def);
           try{
               if(input == null)
                   return cur;
               resp = Integer.parseInt(input);
           }catch(NumberFormatException e){
               JOptionPane.showMessageDialog(null,"Enter an integer next time.","Bad Input",0);
               fail1 = true;
           }
           if(!fail1 && (resp < min || resp > max)){
               if(min == Integer.MIN_VALUE && max != Integer.MAX_VALUE)
                   JOptionPane.showMessageDialog(null,"Must be less than or equal to "+max+".","Bad Input",0);
               else if(min != Integer.MIN_VALUE && max == Integer.MAX_VALUE)
                   JOptionPane.showMessageDialog(null,"Must be greater than or equal to "+min+".","Bad Input",0);
               else
                   JOptionPane.showMessageDialog(null,"Must be greater than or equal to "+min+" and less than or equal to "+max+".","Bad Input",0);
               fail2 = true;
           }
       }while(fail1 || fail2);
       return resp;
  }
  public void paintComponent(Graphics g) {
      super.paintComponent(g);  // fill panel with background color
      time = System.currentTimeMillis();
      Player player1 = grid.getPlayer1();
      Player player2 = grid.getPlayer2();
      // draw any Explosives that the players are on because the Explosives aren't in the grid then
      if(player1.getIsOnExplosive() && grid.getExplosiveAt(player1.getX(),player1.getY()) != null)
            g.drawImage(grid.getExplosiveAt(player1.getX(),player1.getY()).getImage(),
                    (player1.getX())*getWidth()/grid.getWidth(),player1.getY()*getHeight()/grid.getHeight(),
                    getWidth()/grid.getWidth(),getHeight()/grid.getHeight(),this);
      if(player2.getIsOnExplosive() && grid.getExplosiveAt(player2.getX(),player2.getY()) != null)
            g.drawImage(grid.getExplosiveAt(player2.getX(),player2.getY()).getImage(),
                    (player2.getX())*getWidth()/grid.getWidth(),player2.getY()*getHeight()/grid.getHeight(),
                    getWidth()/grid.getWidth(),getHeight()/grid.getHeight(),this);
      // draw all non-null spaces in the grid.
      for(int row = 0; row < grid.getHeight(); row++){
          for(int col = 0; col < grid.getWidth(); col++){
             if (grid.getObjectAt(col,row) != null) // draw the object
                g.drawImage(grid.getObjectAt(col,row).getImage(),
                    col*getWidth()/grid.getWidth(),row*getHeight()/grid.getHeight(),
                    getWidth()/grid.getWidth(),getHeight()/grid.getHeight(),this);
         }
      }
   }
} // end class BombermanPanel