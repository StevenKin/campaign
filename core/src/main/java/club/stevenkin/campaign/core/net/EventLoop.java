package club.stevenkin.campaign.core.net;

import club.stevenkin.campaign.core.Lifecycle;
import club.stevenkin.campaign.core.net.task.AcceptTask;
import club.stevenkin.campaign.core.net.task.ReadTask;
import club.stevenkin.campaign.core.net.task.WriteTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class EventLoop implements Runnable, Lifecycle {
    private static final Logger logger = LoggerFactory.getLogger(EventLoop.class);

    private Selector selector;

    private Queue<Task> queue;

    private Thread thread;

    private volatile boolean stopped;

    public EventLoop() throws Exception {
        selector = Selector.open();
        queue = new LinkedList<>();
        thread = new Thread(this);
        stopped = false;
    }

    @Override
    public void run() {
        while (!stopped) {
            try {
                int n = selector.select();
                if(n<=0)
                    continue;
            } catch (IOException e) {
                logger.error("select error", e);
                continue;
            }
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if(!key.isValid())
                    continue;
                try {
                    if (key.isAcceptable()) {
                        queue.add(new AcceptTask(this, key));
                    }
                    if (key.isReadable()) {
                        queue.add(new ReadTask(this, key));
                    }
                    if (key.isWritable()) {
                        queue.add(new WriteTask(this, key));
                    }
                    Task task = queue.poll();
                    if (task != null) {
                        try {
                            task.run();
                        } catch (Exception e) {
                            logger.error("run task error", e);
                        }
                    }
                }catch (Exception e){
                    if(key!=null&&key.isValid()){
                        key.cancel();
                        try {
                            key.channel().close();
                        } catch (IOException e1) {
                            logger.error("close channel error", e1);
                        }
                    }
                }
            }
        }
    }

    private void stop() {
        stopped = true;
    }

    @Override
    public void init() {
        thread.start();
    }

    @Override
    public void destroy() {
        stop();
    }
}
