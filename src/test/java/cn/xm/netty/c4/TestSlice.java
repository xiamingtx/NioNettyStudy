package cn.xm.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

import static cn.xm.netty.c4.TestByteBuf.log;

/**
 * @author 夏明
 * @version 1.0
 */
@Slf4j
public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        log(buf);

        // 在切片过程中没有发生数据复制
        ByteBuf f1 = buf.slice(0, 5);
        f1.retain(); // retain可以使得对内存的引用 + 1 只release一次无法释放内存
        ByteBuf f2 = buf.slice(5, 5);
        log(f1);
        log(f2);
//        f1.writeByte('x'); // 切片以后无法再write

        log.debug("释放原有byteBuf内存");
        buf.release();
        log(f1);

        f1.setByte(0, 'b');
        log(f1);
        log(buf);
    }
}
