package io.github.cvrunmin.lanfasie.benderson.compat.projectme;

import io.github.cvrunmin.lanfasie.benderson.LanfasieBenderson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.function.Supplier;

public class ReflectiveConfigItemAccessor<T> implements ConfigItemAccessor<T>{

    @Nullable
    private Supplier<Object> underlyingSupplier;
    private final Class<T> fieldType;
    @Nullable
    private Object underlying;
    @Nullable
    private Field field;

    public ReflectiveConfigItemAccessor(@Nonnull Supplier<Object> underlyingObjectSupplier, Class<T> fieldType){
        this.underlyingSupplier = underlyingObjectSupplier;
        this.fieldType = fieldType;
    }

    public ReflectiveConfigItemAccessor(@Nonnull Object underlyingObject, Class<T> fieldType){
        this(() -> underlyingObject, fieldType);
        this.underlying = underlyingObject;
    }

    @Override
    public T getValue() {
        if(underlying == null && underlyingSupplier != null){
            try{
                this.underlying = underlyingSupplier.get();
                underlyingSupplier = null;
                field = underlying.getClass().getDeclaredField("value");
            } catch (Exception e) {
                field = null;
            }
        }
        if(field == null) return null;
        try {
            var maybeValue = field.get(underlying);
            if (!fieldType.isInstance(maybeValue)) {
                field = null;
                return null;
            }
            return (T) maybeValue;
        }catch (ClassCastException e){
            LanfasieBenderson.LOGGER.warn("Cannot get config item with suitable type", e);
            field = null;
            return null;
        }catch (Exception e) {
            return null;
        }
    }
}
