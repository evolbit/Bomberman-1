/**
 * class Lobby
 * 
 * @version 2007-12-30
 */
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
public class Lobby
{
    public static void main(String [] args){
        JFrame window = new JFrame("Bomberman Lobby");
        
        // title panel
        JLabel lobbyTitleLabel = new JLabel("Bomberman Lobby",JLabel.CENTER);
        lobbyTitleLabel.setFont(new Font(null,Font.BOLD,16));
        lobbyTitleLabel.setOpaque(true);
        lobbyTitleLabel.setForeground(Color.WHITE);
        lobbyTitleLabel.setBackground(Color.BLACK);
        
        // my alias / my ip panel
        JPanel myPanel = new JPanel(new GridLayout(2,2));
        myPanel.add(new JLabel("My Alias ",JLabel.RIGHT));
        JTextField aliasTextField = new JTextField();
        myPanel.add(aliasTextField);
        myPanel.add(new JLabel("My IP ",JLabel.RIGHT));
        // determine ip
        String ip = "Cannot determine.";
        ArrayList<String>lines = BombermanPanel.getLineArrayFromAddr(BombermanPanel.webPath+"query.php?action=ip");
        if(lines != null && lines.size() > 0)
            ip = lines.get(0);
        JTextField ipTextField = new JTextField(ip);
        ipTextField.setEditable(false);
        // add ip to panel
        myPanel.add(ipTextField);
        myPanel.setBackground(Color.LIGHT_GRAY);

        // new server title label
        JLabel newServerTitleLabel = new JLabel("New Server",JLabel.CENTER);
        newServerTitleLabel.setFont(new Font(null,Font.BOLD,16));
        newServerTitleLabel.setOpaque(true);
        newServerTitleLabel.setForeground(Color.WHITE);
        newServerTitleLabel.setBackground(Color.BLACK);
          
        // new server row
        JPanel newServerPanel = new JPanel(new GridLayout(1,3));  
        JButton chooseLevelButton = new JButton("Choose Level...");
        newServerPanel.add(chooseLevelButton);
        newServerPanel.setBackground(Color.BLACK);
        JTextField levelTextField = new JTextField();
        levelTextField.setEditable(false);
        levelTextField.setHorizontalAlignment(JTextField.CENTER);
        newServerPanel.add(levelTextField);
        JButton newServerButton = new JButton("Create Server");
        newServerPanel.add(newServerButton);
         
        // header panel for interchangeable panels
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.GRAY);
        
        // scroll pane for interchangeable panels
        JScrollPane jsp = new JScrollPane();
        jsp.setBackground(Color.BLACK);
        jsp.setColumnHeaderView(headerPanel);
        
        LobbyPanelHandler lph = new LobbyPanelHandler(window,jsp,headerPanel,chooseLevelButton);
        
        // interchangeable panels
        ServerPanel serverPanel = new ServerPanel(lph,aliasTextField,levelTextField,chooseLevelButton,newServerButton);
        LevelPanel levelPanel = new LevelPanel(lph);
        serverPanel.setNewLevel(1);
        lph.setLevelPanel(levelPanel);
        lph.setServerPanel(serverPanel);
        levelPanel.refresh();
        serverPanel.refresh();
        
        // set the viewport for the scroll pane  
        jsp.setViewportView(serverPanel);
        
        // top panel has lobby title and my alias / my ip
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(lobbyTitleLabel,BorderLayout.NORTH);
        topPanel.add(myPanel,BorderLayout.SOUTH);
        
        // bottomPanel has new server title and new server row
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(newServerTitleLabel,BorderLayout.NORTH);
        bottomPanel.add(newServerPanel,BorderLayout.SOUTH);
        
        // content
        JPanel content = new JPanel(new BorderLayout());
        content.add(topPanel,BorderLayout.NORTH);
        content.add(jsp,BorderLayout.CENTER);
        content.add(bottomPanel,BorderLayout.SOUTH);
        
        // window
        window.setContentPane(content);
        window.setSize(500,500);
        window.setLocation(100,100);
        window.setResizable(true);
        window.setVisible(true);
    }
}