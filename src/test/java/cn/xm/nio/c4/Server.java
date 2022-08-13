package cn.xm.nio.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static cn.xm.nio.c2.ByteBufferUtil.debugAll;

/**
 * @author 夏明
 * @version 1.0
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        // 1. 创建selector 管理多个channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 2. 建立 selector 和 channel之间的联系 (注册)
        // SelectionKey 就是将来事件发生后 通过它可以知道事件和哪个channel的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // sscKey 只对 accept 事件感兴趣
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register Key: {} ", sscKey);

        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            // 3. selector 的 select 方法  没有事件发生, 线程阻塞, 有事件, 线程才会恢复运行
            // select 在事件未处理时 它不会阻塞 事件发生后要么处理 要么取消 不能置之不理
            selector.select();

            // 4. 处理事件, selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator(); // accept, read
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                // 处理 key 时, 要从 selectedKeys 中删除, 否则下一轮循环处理时候就会出问题(空指针)
                iter.remove();
                log.debug("key: {}", key);
                // 5. 区分事件类型
                if (key.isAcceptable()) { // 如果是accept
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();// 拿到触发事件的channel
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16); // attachment 附件
                    // 将一个 byteBuffer 作为附件关联到 selectionKey 上
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("{}", sc);
                } else if (key.isReadable()) { // 如果是read
                    try {
                        SocketChannel channel = (SocketChannel) key.channel(); // 拿到触发事件的channel
                        // 获取 selectionKey 上关联的附件
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        int read = channel.read(buffer); // 如果是正常断开 read方法的返回值是 -1
                        if (read == -1) {
                            key.cancel();
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel(); // 因为客户端断开了, 因此需要将 key 从 selectedKeys 中删除
                    }
                }

//                key.cancel();
            }
        }
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读，向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }
}
