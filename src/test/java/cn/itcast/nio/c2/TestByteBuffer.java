package cn.itcast.nio.c2;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author 夏明
 * @version 1.0
 */
@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {
        // FileChannel
        // 1. 输入输出流  2. RandomAccessFile
        // .twr  --> try with resource
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                // 从 channel 读取数据, 向buffer写入
                int len = channel.read(buffer);
                log.debug("读取到的字节数 {}", len);
                if (len == -1) { // 没有内容了
                    break;
                }
                // 打印 buffer 的内容
                buffer.flip(); // 切换至读模式
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    log.debug("实际字节 {}", (char)b);
                }
                buffer.clear(); // 切换为写模式 clear()或compact()
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
