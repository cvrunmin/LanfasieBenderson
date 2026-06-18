package io.github.cvrunmin.lanfasie.benderson.compat.projectme;

import java.lang.reflect.Field;

public class ReflectiveConfigItemAccessor<T> implements ConfigItemAccessor<T>{

    private final Object underlying;
    private Field field;

    public ReflectiveConfigItemAccessor(Object underlyingObject, Class<T> fieldType){
        this.underlying = underlyingObject;
        try{
            field = underlyingObject.getClass().getDeclaredField("value");
            if(!fieldType.isAssignableFrom(field.getType())) field = null;
        } catch (NoSuchFieldException e) {
            field = null;
        }
    }

    @Override
    public T getValue() {
        if(field == null) return null;
        try {
            var maybeValue = field.get(underlying);
            return (T) maybeValue;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}
