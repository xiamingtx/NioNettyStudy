package cn.itcast.nio.c3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author 夏明
 * @version 1.0
 */
public class TestFilesCopy {

    public static void main(String[] args) throws IOException {
        String source = "E:\\workspace";
        String target = "E:\\workspace123";

        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targetName = path.toString().replace(source, target);
                // 是目录
                if (Files.isDirectory(path)) {
                    // E:\workspace\123
                    // E:\workspace123\123
                    Files.createDirectory(Paths.get(targetName));
                }
                // 是普通文件
                else if (Files.isRegularFile(path)) {
                    Files.copy(path, Paths.get(targetName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
