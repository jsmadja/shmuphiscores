package drawer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Images {

    public static byte[] toBytes(BufferedImage image) throws IOException {
        byte[] bytes;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", stream);
        bytes = stream.toByteArray();
        return bytes;
    }

}
