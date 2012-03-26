/**
 * class LevelMaker
 * 
 * @version 2007-11-21
 */
import javax.swing.*;
import java.awt.*;
public class LevelMaker
{
    public static void main(String [] args){
        JFrame window = new JFrame("Level Maker");
  
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout(1,1));
        
        String levelPath = "";
        if(args != null && args.length > 0)
            levelPath = args[0];
        
        LevelMakerPanel lvlMakerPanel = new LevelMakerPanel(window,levelPath);
       
        content.add(lvlMakerPanel,BorderLayout.CENTER);
        window.setContentPane(content);
        window.setSize(500, 500);
        window.setLocation(400,400);
        //window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        window.setResizable(true);
        window.setVisible(true);
    }
}
