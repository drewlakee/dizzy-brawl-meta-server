package dizzybrawl.verticles.eventBus;

import java.util.Objects;

public class EventBusObjectWrapper<E> {

    private final E pojoObject;

    private EventBusObjectWrapper(E pojoObject) {
        this.pojoObject = pojoObject;
    }

    public E get() {
        return pojoObject;
    }

    public static <E> EventBusObjectWrapper<E> of(E object) {
        return new EventBusObjectWrapper<>(object);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventBusObjectWrapper<?> that = (EventBusObjectWrapper<?>) o;
        return Objects.equals(pojoObject, that.pojoObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pojoObject);
    }
}
