/**
 * class Changelog
 * 
 * @version 2007-12-28
 */
import java.awt.*;
import javax.swing.*;
import java.net.URL;
import java.io.*;
import java.util.ArrayList;
public class Changelog {
    public static void main(String[] args) {
      JFrame window = new JFrame("Changelog");

      // create URL of changelog address
      URL url = null;
      try{
        url = new URL(BombermanPanel.webPath+"query.php?action=changelog");
      }catch(Exception e){}
      
      // create box for changelog and put it in a JScrollPane
      JEditorPane box = new JEditorPane();
      try{
        box.setPage(url);    
      }catch(Exception e){
        box.setText("Could not retrieve changelog.\n"+BombermanPanel.webPath);    
      }
      box.setEditable(false);
      JScrollPane sp = new JScrollPane(box);
     
      window.setContentPane(sp);
      window.setSize(500, 500);
      window.setLocation(100,100);
      window.setResizable(true);
      window.setVisible(true);
    }
}