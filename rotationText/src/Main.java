import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Main extends Panel {
    public static void main(String[] args){
        Frame f = new Frame("Circle Text");
        f.add(new Main());
        f.setSize(750, 750);
        f.setVisible(true);
    }

    private int[] getPointXY(int dist, double rad){
        int[] coord = new int[2];
        coord[0] = (int) (dist * cos(rad) + dist);
        coord[1] = (int) (-dist * sin(rad) + dist);
        return coord;
    }

    @Override
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Hard-coded for now, using 12 characters for 30 degrees angles (like  a clock)
        String text = "ㄱ ㄲ ㅋ ㆁ ㄷ ㄸ ㅌ ㄴ ㅥ ㅂ ㅃ ㅍ ㅁ ㅈ ㅉ ㅊ ㅅ ㅆ ㆆ ㅎ ㆅ ㅇ ㄹ ㅿㆍ ㅡ ㅣ ㅗ ㅏ ㅜ ㅓ ㅛ ㅑ ㅠ ㅕ";

        Font font = new Font("Serif", 0, 25);
        FontRenderContext frc = g2.getFontRenderContext();
        g2.translate(200, 200); // Starting position of the text

        GlyphVector gv = font.createGlyphVector(frc, text);
        int length = gv.getNumGlyphs(); // Same as text.length()
        final double toRad = Math.PI / 180;
        for(int i = 0; i < length; i++){
            //Point2D p = gv.getGlyphPosition(i);
            int r = 50;
            int[] coords = getPointXY(r, -360 / length * i * toRad + Math.PI / 2);
            gv.setGlyphPosition(i, new Point(coords[0], coords[1]));
            final AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
            at.rotate(-2 * Math.PI * i / length);
            at.translate(r * cos(Math.PI / 2 - 2 * Math.PI * i / length),
                    r * sin(Math.PI / 2 - 2 * Math.PI * i / length));
            Shape glyph = gv.getGlyphOutline(i);
            Shape transformedGlyph = at.createTransformedShape(glyph);
            g2.fill(transformedGlyph);
        }
    }
}