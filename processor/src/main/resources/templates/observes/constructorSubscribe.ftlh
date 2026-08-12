beanManager.lookupBean(io.crysknife.client.internal.event.EventManager.class).getInstance().getRegistry().subscribe(${target}.class, this, _event_ -> {
    if (instance == null) {
        getInstance();
        ${consumer}.accept(_event_);
    }
});
