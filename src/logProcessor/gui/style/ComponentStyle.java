package logProcessor.gui.style;

import java.awt.*;

public class ComponentStyle {
  public static void fontStyle(Component component) {
    fontStyle(component, new Font("宋体", Font.PLAIN, 14));
  }

  public static void fontStyle(Component component, Font font) {
    component.setFont(font);
    component.setForeground(Color.BLACK);
  }
}
