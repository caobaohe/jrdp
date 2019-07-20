import com.cbh.jrdp.Server;

import java.io.File;
import java.io.IOException;

/**
 * FIleTest class
 *
 * @author cbh
 * @date 2019/7/20 09:08
 */
public class FileTest {
    public static void main(String[] args) throws IOException {
        File file = new File("tmp/fileTest.txt");
        boolean newFile = file.createNewFile();
        System.out.println(newFile);
        System.out.println(file.getAbsolutePath());

        String cursorPath = Server.class.getResource("/").getPath();
        System.out.println(cursorPath);
    }
}
