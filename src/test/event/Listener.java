package test.event;

import java.util.EventListener;

public interface Listener extends EventListener {
    public void perform(Event e);
}
