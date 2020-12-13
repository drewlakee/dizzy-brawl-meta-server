package dizzybrawl.verticles.eventBus.codecs;

import java.util.Objects;

public class POJOEventBusWrapper<E> {

    private final E pojoObject;

    private POJOEventBusWrapper(E pojoObject) {
        this.pojoObject = pojoObject;
    }

    public E get() {
        return pojoObject;
    }

    public static <E> POJOEventBusWrapper<E> of(E pojoObject) {
        return new POJOEventBusWrapper<>(pojoObject);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        POJOEventBusWrapper<?> that = (POJOEventBusWrapper<?>) o;
        return Objects.equals(pojoObject, that.pojoObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pojoObject);
    }
}
