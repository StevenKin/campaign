package club.stevenkin.campaign.core.net.task;

import club.stevenkin.campaign.core.net.EventLoop;
import club.stevenkin.campaign.core.net.Task;

import java.nio.channels.SelectionKey;

public abstract class IOTask implements Task {
    private EventLoop eventLoop;

    private SelectionKey selectionKey;

    public IOTask(EventLoop eventLoop, SelectionKey selectionKey) {
        this.eventLoop = eventLoop;
        this.selectionKey = selectionKey;
    }
}
