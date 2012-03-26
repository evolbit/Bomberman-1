/**
 * class Bomberman
 * 
 * @version 2007-11-07
 */
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
public class Bomberman {
    public static void main(String[] args) {
      JFrame window = new JFrame("Bomberman");
      
      JPanel content = new JPanel();
      content.setLayout(new BorderLayout(1,1));      
      
      JPanel bottomPanel = new JPanel();
      bottomPanel.setLayout(new BorderLayout());
      
      JPanel statsPanel = new JPanel();
      statsPanel.setLayout(new GridLayout(8,2));
      statsPanel.setIgnoreRepaint(true);
      
      JPanel infoPanel = new JPanel();
      infoPanel.setBackground(Color.GRAY);
      
      JLabel infoLabel = new JLabel();
      infoLabel.setForeground(Color.WHITE);
      infoPanel.add(infoLabel);
      
      // create labels for statsPanel
      ArrayList<JLabel> statsLabels = new ArrayList<JLabel>();
      for(int ind = 0; ind < 16; ind++){
         JLabel temp = new JLabel();
         if(ind % 2 == 0)
            temp.setHorizontalAlignment(SwingConstants.LEFT);
         else
            temp.setHorizontalAlignment(SwingConstants.RIGHT);
         statsPanel.add(temp);
         statsLabels.add(temp);
      } 
      
      // load level based on parameter
      String levelPath = BombermanPanel.levelPath + "level1.txt";
      if(args != null && args.length > 0)
        levelPath = args[0];
     
      // pass content, bottomPanel, and statsPanel so that stats can be turned on and off
      // pass labels so they can be updated, levelName to choose a starting level
      BombermanPanel bombermanPanel = new BombermanPanel(window,content,bottomPanel,statsPanel,statsLabels,infoLabel,levelPath);
      
      bottomPanel.add(statsPanel, BorderLayout.CENTER);
      bottomPanel.add(infoPanel, BorderLayout.SOUTH);
      content.add(bombermanPanel, BorderLayout.CENTER);
      content.add(bottomPanel, BorderLayout.SOUTH);
      window.setContentPane(content);
      window.setSize(500, 800);
      window.setLocation(100,100);
      //window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      window.setResizable(true);
      window.setVisible(true);
    }
}