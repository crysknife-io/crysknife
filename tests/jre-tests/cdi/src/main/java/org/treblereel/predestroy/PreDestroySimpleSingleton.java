package org.treblereel.predestroy;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class PreDestroySimpleSingleton {

    public int check = new Random().nextInt();

    private Runnable disposed;

    public void setCallback(Runnable disposed) {
        this.disposed = disposed;
    }

    @PreDestroy
    private void onDestroy() {
        disposed.run();
    }
}
