package mkp;

import javax.swing.JPanel;
import java.awt.Graphics;

public interface MKPGlassUI {

	void install(JPanel panel);
	void uninstall(JPanel panel);

	void paintComponent(Graphics g);

	void clear();

}
