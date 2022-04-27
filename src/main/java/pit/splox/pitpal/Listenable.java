package pit.splox.pitpal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Listenable<Self> {
    private List<Consumer<Self>> updateListeners = new ArrayList<Consumer<Self>>();
    public Self onUpdate(Consumer<Self> listener) {
        updateListeners.add(listener);
        return (Self) this;
    }
    public Self removeOnUpdateListener(Consumer<Self> listener) {
        updateListeners.remove(listener);
        return (Self) this;
    }

    public Self update(Consumer<Self> updater) {
        updater.accept((Self) this);
        triggerUpdates();
        return (Self) this;
    }

    private void triggerUpdates() {
        for (Consumer<Self> listener : updateListeners) {
            listener.accept((Self) this);
        }
    }
}
