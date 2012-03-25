/**
 * class LobbyPanelHandler
 * 
 * @version 2008-01-13
 */
import javax.swing.*;
import java.awt.*;
public class LobbyPanelHandler
{
    // window component instance variables
    private JFrame window;
    private JScrollPane jsp;
    private JPanel headerPanel;
    private JButton chooseLevelButton;
    private ServerPanel serverPanel;
    private LevelPanel levelPanel;
    public LobbyPanelHandler(JFrame newWindow,JScrollPane newJSP,JPanel newHeaderPanel,JButton newChooseLevelButton)
    {
        window = newWindow;
        jsp = newJSP;
        headerPanel = newHeaderPanel;
        chooseLevelButton = newChooseLevelButton;
    }
    public void setServerPanel(ServerPanel sp){
        serverPanel = sp;   
    }
    public void setLevelPanel(LevelPanel lp){
        levelPanel = lp;   
    }
    public ServerPanel getServerPanel(){
        return serverPanel;   
    }
    public LevelPanel getLevelPanel(){
        return levelPanel;   
    }
    public JPanel getHeaderPanel(){
        return headerPanel;   
    }
    public JFrame getWindow(){
        return window;   
    }
    public void showServerPanel(){
        serverPanel.updateHeaders();
        jsp.setViewportView(serverPanel);
        chooseLevelButton.setText("Choose Level...");
        window.validate();
    }
    public void showLevelPanel(){
        levelPanel.updateHeaders();
        jsp.setViewportView(levelPanel);
        chooseLevelButton.setText("Back to Servers");
        window.validate();
    }
    public void showLevelDetailPanel(Level l){
        LevelDetailPanel ldp = new LevelDetailPanel(this,l);
        ldp.updateHeaders();
        ldp.setPreferredSize(new Dimension(100,100));
        ldp.refresh();
        jsp.setViewportView(ldp);
        chooseLevelButton.setText("Back to Levels");
        window.validate();
    }
}