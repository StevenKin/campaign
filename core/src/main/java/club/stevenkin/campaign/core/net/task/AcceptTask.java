package club.stevenkin.campaign.core.net.task;

import club.stevenkin.campaign.core.net.EventLoop;
import club.stevenkin.campaign.core.net.Task;

import java.nio.channels.SelectionKey;

public class AcceptTask extends IOTask implements Task {
    public AcceptTask(EventLoop eventLoop, SelectionKey selectionKey) {
        super(eventLoop, selectionKey);
    }

    @Override
    public void run() {

    }
}
