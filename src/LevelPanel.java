/**
 * class LevelPanel
 * 
 * @version 2008-01-03
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
public class LevelPanel extends JPanel
{
    // instance variables: components
    private LobbyPanelHandler lph;
    // other instance variables
    private ArrayList<Level>levels;
    
    // constructor
    public LevelPanel(LobbyPanelHandler newLPH){
        // initialize instance variables (components)
        lph = newLPH;
        
        // initialize other instance variables
        levels = new ArrayList<Level>();
        
        // set background color
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(300,500));
    }
    
    // methods for levels array
    public ArrayList<Level>getLevels(){
        return levels;   
    }
    public Level getLevel(int lid){
        for(int ind = 0; ind < levels.size(); ind++){
            if(levels.get(ind).getLID() == lid)
                return levels.get(ind);
        }
        return new Level();
    }
    
    // ActionListener for JButtons
    private class ButtonActionListener implements ActionListener{
        public void actionPerformed(ActionEvent evt){
            String command = evt.getActionCommand();
            if(command.equals("Refresh"))
                refresh();
            else if(command.contains("Use")){
                String [] parts = command.split(" ");
                lph.getServerPanel().setNewLevel(Integer.parseInt(parts[1]));
                lph.showServerPanel();
            }
        }
    }
    
    // update header methods
    public void updateHeaders(){
        updateHeaders(levels.size());   
    }
    public void updateHeaders(int nbrLevels){
        // display the header labels
        lph.getHeaderPanel().removeAll();
        lph.getHeaderPanel().setLayout(new GridLayout(1,3));
        
        JLabel l1 = new JLabel("Levels ("+nbrLevels+")");
        l1.setFont(new Font(null,Font.BOLD,16));
        l1.setOpaque(true);
        l1.setForeground(Color.WHITE);
        l1.setBackground(Color.BLACK);
        lph.getHeaderPanel().add(l1);
        
        JLabel l2 = new JLabel("Author");
        l2.setFont(new Font(null,Font.BOLD,16));
        l2.setOpaque(true);
        l2.setForeground(Color.WHITE);
        l2.setBackground(Color.BLACK);        
        lph.getHeaderPanel().add(l2);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ButtonActionListener());
        lph.getHeaderPanel().add(refreshButton);   
    }
    
    // refresh level list method
    public void refresh(){
        // get the number of levels and the line for each one
        ArrayList<String>lines = BombermanPanel.getLineArrayFromAddr(BombermanPanel.webPath+"query.php?action=list_levels");
        int nbrLevels = 0;
        if(lines != null && lines.size() > 0)
            nbrLevels = Integer.parseInt(lines.get(0));
            
        updateHeaders(nbrLevels);
        
        // remove all previous components and choose the layout dimensions
        removeAll();
        if(nbrLevels > 0)
            setLayout(new GridLayout(nbrLevels,3));
        else
            setLayout(new GridLayout(1,3));
            
        // remove all old Levels from array
        levels.clear();
            
        // go through the line for each level
        for(int ind = 1; ind < nbrLevels+1; ind++){
            final Level l = new Level(lines.get(ind));
            levels.add(l);
            
            // display clickable (level name and) level filename
            JLabel label = new JLabel();
            if(l.getName().equals("")) // no name
                label.setText(l.getFile());
            else
                label.setText(l.getName()+" ("+l.getFile()+")");
            label.addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent e){
                    lph.showLevelDetailPanel(l);
                }
                public void mouseReleased(MouseEvent e){}
            });
            label.setForeground(Color.BLUE);
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            add(label);
                
            // display author (and email) or if blank then anonymous
            if(!l.getAuthor().equals("")){
                if(l.getEmail().equals(""))
                    add(new JLabel(l.getAuthor()));
                else
                    add(new JLabel(l.getAuthor()+" ("+l.getEmail()+")"));
            }else
                add(new JLabel("anonymous"));
                
            // display "Use ID" button
            JButton b = new JButton("Use "+l.getLID());
            b.addActionListener(new ButtonActionListener());
            add(b);
        }
        
        // if no levels found then show a label for that
        if(nbrLevels == 0){
            add(new JLabel("None found"));
            add(new JLabel());add(new JLabel());
        }
            
        // update components
        validate();
        lph.getWindow().validate();
    }
    
}