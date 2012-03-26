/**
 * class LobbyPanel
 * 
 * @version 2007-12-30
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
public class ServerPanel extends JPanel
{
    // window component instance variables
    private LobbyPanelHandler lph;
    private JTextField aliasTextField;
    private JTextField levelTextField;
    private JButton chooseLevelButton;
    private JButton newGameButton;    
    
    // other instance variables
    private String localAlias;
    private int newServerLID,cServerSID;
    private ArrayList<Server>servers;
   
    // random code
    public static final int localCode = (int)(Math.random()*9999999+1000000); // choose a random code: x,xxx,xxx
    
    // constructor
    public ServerPanel(LobbyPanelHandler newLPH,JTextField newAliasTextField, JTextField newLevelTextField, JButton newChooseLevelButton, JButton newNewGameButton)
    {
        // initialize instance variables for window components
        lph = newLPH;
        aliasTextField = newAliasTextField;
        levelTextField = newLevelTextField;
        chooseLevelButton = newChooseLevelButton;
        newGameButton = newNewGameButton;
        
        // initialize other instance variables
        localAlias = "";
        newServerLID = 0;
        servers = new ArrayList<Server>();
        cServerSID = 0;
        
        // set Background color
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(300,500));
        
        // set FocusListeners for JTextFields
        aliasTextField.addFocusListener(new TextFieldFocusListener());
        levelTextField.addFocusListener(new TextFieldFocusListener());
        
        // set ActionListeners for JButton
        newGameButton.addActionListener(new ButtonActionListener());
        chooseLevelButton.addActionListener(new ButtonActionListener());
    }
    
    // new server level update method
    public void setNewLevel(int lid){
       levelTextField.setText(lid+"");
       newServerLID = lid;
    }
    
    // methods for servers array
    public ArrayList<Server>getServers(){
        return servers;   
    }
    public Server getServer(int sid){
        for(int ind = 0; ind < servers.size(); ind++){
            if(servers.get(ind).getSID() == sid)
                return servers.get(ind);
        }
        return new Server();
    }
    
    // ActionListener for JButtons
    private class ButtonActionListener implements ActionListener{
        public void actionPerformed(ActionEvent evt){
            String command = evt.getActionCommand();
            String [] parts = command.split(" ");
            if(command.contains("Join"))
                joinServer(Integer.parseInt(parts[1].replaceAll(":","")));
            else if(command.contains("Leave"))
                leaveServer();
            else if(command.contains("Observe")){
                //observeServer();
            }else if(command.equals("Create Server"))
                createServer();
            else if(command.equals("Refresh"))
                refresh();
            else if(command.equals("Choose Level..."))
                lph.showLevelPanel();
            else if(command.equals("Back to Servers"))
                lph.showServerPanel();
            else if(command.equals("Back to Levels"))
                lph.showLevelPanel();
        }
    }
    
    // FocusListener for JTextFields
    private class TextFieldFocusListener implements FocusListener{
        public void focusLost(FocusEvent e){
            // replace illegal characters in JTextFields when either loses focuses
            if(e.getComponent() == aliasTextField){
                //aliasTextField.setText(aliasTextField.getText().replaceAll("[^A-Za-z0-9\\ \\-\\_]",""));
                if(aliasTextField.getText().length()>20)
                    aliasTextField.setText(aliasTextField.getText().substring(0,20));
                localAlias = aliasTextField.getText();
                if(!localAlias.trim().equals("")){
                    aliasTextField.setEditable(false);
                    refresh();
                }                
            }else if(e.getComponent() == levelTextField)
                newServerLID = Integer.parseInt(levelTextField.getText());
        }
        public void focusGained(FocusEvent e){}
    }
    
    // server methods
    private void createServer(){
        // ensure a level and alias has been chosen
        if(newServerLID == 0 || localAlias.length() == 0){
            JOptionPane.showMessageDialog(null,"Failed to create server. Check your alias.","Create Failed",1);
            if(localAlias.length() == 0)
                aliasTextField.requestFocus();
            else if(newServerLID == 0)
                lph.showLevelPanel();
            return;
        }
        Server n = new Server(newServerLID,"",localAlias,"",localAlias);
        if(n.getSID() > 0){
            leaveServer();
            cServerSID = n.getSID();
            refresh();
            lph.showServerPanel();
        }
    }
    private void joinServer(int sid){
        Server s = new Server();
        if(localAlias.length() > 0){
            s = getServer(sid);
            s.join();
        }
        if(s.getSID() > 0 && !s.getRemoteAlias().equals("")){
            leaveServer();
            cServerSID = s.getSID();
            refresh();
        }else{
            JOptionPane.showMessageDialog(null,"Failed to join server "+sid+". Check your alias.","Join Failed",1);
            if(localAlias.length() == 0)
                aliasTextField.requestFocus();
        }
    }
    private void leaveServer(){
        if(cServerSID == 0)
            return;
        int code = getServer(cServerSID).leave();
        if(code == 2){
            servers.remove(servers.indexOf(getServer(cServerSID)));
            cServerSID = 0;
            refresh();
        }else if(code == 1){
            cServerSID = 0;
            refresh();
        }else
            JOptionPane.showMessageDialog(null,"Failed to leave server "+cServerSID+".","Leaving Server Failed",1);
    }
    
    // update header methods
    public void updateHeaders(){
        updateHeaders(servers.size()); 
    }
    public void updateHeaders(int nbrServers){
        // display the header labels
        lph.getHeaderPanel().removeAll();
        lph.getHeaderPanel().setLayout(new GridLayout(1,4));
        
        JLabel serverCountLabel = new JLabel("Servers ("+nbrServers+")");
        serverCountLabel.setFont(new Font(null,Font.BOLD,16));
        serverCountLabel.setForeground(Color.WHITE);
        serverCountLabel.setBackground(Color.BLACK);
        serverCountLabel.setOpaque(true);
        lph.getHeaderPanel().add(serverCountLabel);
        
        JLabel l2 = new JLabel("Host");
        l2.setFont(new Font(null,Font.BOLD,16));
        l2.setForeground(Color.WHITE);
        l2.setBackground(Color.BLACK);
        l2.setOpaque(true);
        lph.getHeaderPanel().add(l2);
        
        JLabel l3 = new JLabel("Remote");
        l3.setFont(new Font(null,Font.BOLD,16));
        l3.setForeground(Color.WHITE);
        l3.setBackground(Color.BLACK);
        l3.setOpaque(true);
        lph.getHeaderPanel().add(l3);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ButtonActionListener());
        lph.getHeaderPanel().add(refreshButton);    
    }
    
    // refresh server list method
    public void refresh(){
        // get the number of servers and the line for each one
        ArrayList<String>lines = BombermanPanel.getLineArrayFromAddr(BombermanPanel.webPath+"query.php?action=list_servers");
        int nbrServers = 0;
        if(lines != null && lines.size() > 0)
            nbrServers = Integer.parseInt(lines.get(0));
            
        // update the column headers
        updateHeaders(nbrServers);
            
        // remove all previous components and choose the layout dimensions
        removeAll();
        if(nbrServers > 0)
            setLayout(new GridLayout(nbrServers,4));
        else
            setLayout(new GridLayout(1,4));
            
        // remove all old Servers from the array
        servers.clear();
        
        // go through the line for each server
        for(int ind = 1; ind < nbrServers+1; ind++){
            Server s = new Server(lines.get(ind),localAlias);
            servers.add(s);
            final Level l = lph.getLevelPanel().getLevel(s.getLID());
            
            /*// display level title or if blank then level filename
            if(l.getName().equals(""))
                add(new JLabel(l.getFile()));
            else
                add(new JLabel(l.getName()+" ("+l.getFile()+")"));*/
                
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
                
            // display host
            add(new JLabel(s.getHostAlias()));
            
            // display button
            JButton b;
            if(s.getRemoteAlias().equals("")){
                // waiting on remote
                add(new JLabel("(waiting)"));
                // local user is hosting this server
                if(s.getSID() == cServerSID)
                    b = new JButton("Leave "+s.getSID());
                else // local user can join this server
                    b = new JButton("Join "+s.getSID());
            }else{
                // server is full
                add(new JLabel(s.getRemoteAlias()));
                // local user is hosting this server
                if(s.getSID() == cServerSID)
                    b = new JButton("Leave "+s.getSID());
                else // local user can observe this server
                    b = new JButton("Observe "+s.getSID());      
            }
            b.addActionListener(new ButtonActionListener());
            add(b);
        }
        
        // if no servers found then show a label for that
        if(nbrServers == 0){
            add(new JLabel("None found."));
            add(new JLabel());add(new JLabel());add(new JLabel());
        }
        
        // update components
        validate();
        lph.getWindow().validate();
    }
    
    public void paintComponent(Graphics g){
       super.paintComponent(g);
    }
}
