import java.awt.Font;
import java.io.InputStream;

public class FontUtil {
    public static Font loadLobsterFont(float size) {
        try {
            InputStream fontStream = FontUtil.class.getResourceAsStream("/fonts/Lobster-Regular.ttf");
            if (fontStream == null) {
                throw new RuntimeException("Font file not found in resources!");
            }
            return Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Font("Serif", Font.ITALIC, (int) size); // fallback
        }
    }
}
