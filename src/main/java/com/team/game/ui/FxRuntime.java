package main.java.com.team.game.ui;

import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;

public final class FxRuntime {
    private static final AtomicBoolean started = new AtomicBoolean(false);

    private FxRuntime() {}

    public static void ensureStarted() {
        if (started.compareAndSet(false, true)) {
            Platform.startup(() -> {});
            Platform.setImplicitExit(false);
        }
    }
}
