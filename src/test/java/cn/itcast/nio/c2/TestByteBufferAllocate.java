package cn.itcast.nio.c2;

import java.nio.ByteBuffer;

/**
 * @author 夏明
 * @version 1.0
 */
public class TestByteBufferAllocate {
    public static void main(String[] args) {
        // class java.nio.HeapByteBuffer 堆内存 读写效率较低 受到GC影响
        System.out.println(ByteBuffer.allocate(16).getClass());
        // class java.nio.DirectByteBuffer 直接内存 读写效率高(少一次拷贝) 不会受GC影响 分配效率低
        System.out.println(ByteBuffer.allocateDirect(16).getClass());

    }
}
