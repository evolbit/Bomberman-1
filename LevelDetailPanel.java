/**
 * class LevelDetailPanel
 *  
 * @version 2008-01-13
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
public class LevelDetailPanel extends JPanel
{
    // instance variables: components
    private LobbyPanelHandler lph;
    private Level level;
    
    // constructor
    public LevelDetailPanel(LobbyPanelHandler newLPH,Level l){
        // initialize instance variables (components)
        lph = newLPH;
        level = l;
        
        // set background color
        setBackground(Color.LIGHT_GRAY);
    }
    
    // update header method
    public void updateHeaders(){
        // display the header labels
        lph.getHeaderPanel().removeAll();
        lph.getHeaderPanel().setLayout(new GridLayout(1,2));
        
        JLabel l1 = new JLabel("Level #"+level.getLID());
        l1.setFont(new Font(null,Font.BOLD,16));
        l1.setOpaque(true);
        l1.setForeground(Color.WHITE);
        l1.setBackground(Color.BLACK);
        lph.getHeaderPanel().add(l1);
        
        JLabel l2 = new JLabel("Details");
        l2.setFont(new Font(null,Font.BOLD,16));
        l2.setOpaque(true);
        l2.setForeground(Color.WHITE);
        l2.setBackground(Color.BLACK);
        lph.getHeaderPanel().add(l2);
    }
    
    // refresh level list method
    public void refresh(){            
        updateHeaders();
        
        // remove all previous components and choose the layout dimensions
        removeAll();
        setLayout(new GridLayout(5,2));
            
        // display file
        add(new JLabel("File"));
        JTextField t1 = new JTextField(level.getFile());
        t1.setEditable(false);
        add(t1);
        
        // display name
        add(new JLabel("Name"));
        JTextField t2 = new JTextField(level.getName());
        t2.setEditable(false);
        add(t2);
        
        // display author
        add(new JLabel("Author"));
        JTextField t3 = new JTextField(level.getAuthor());
        t3.setEditable(false);
        add(t3);
        
        // display email
        add(new JLabel("Email"));
        JTextField t4 = new JTextField(level.getEmail());
        t4.setEditable(false);
        add(t4);
        
        // display description
        add(new JLabel("Description"));
        JEditorPane ta = new JEditorPane("text/plain",level.getDesc());
        ta.setEditable(false);
        add(ta);
 
        // update components
        validate();
        lph.getWindow().validate();
    }
}