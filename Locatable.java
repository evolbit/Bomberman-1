/**
 * interface Locatable
 * 
 * @version 2007-11-06
 */
import java.awt.Image;
public interface Locatable
{
    int getX();
    int getY();
    void setX(int newX);
    void setY(int newY);
    Image getImage();
    void setImage(int imageCode);
    void setGrid(Grid g);
    String toString();
}
