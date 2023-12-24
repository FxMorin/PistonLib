package ca.fxco.pistonlib.helpers;

import ca.fxco.pistonlib.config.ParsedValue;
import com.google.common.primitives.Primitives;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class ConfigUtils {

    @SuppressWarnings("unchecked")
    public static <T> @Nullable T loadValueFromConfig(Object value, ParsedValue<T> parsedValue) {
        Class<?> clazz = parsedValue.getDefaultValue().getClass();
        if (clazz == value.getClass()) {
            return (T) clazz.cast(value);
        } else if (clazz.isEnum() && value instanceof String str) {
            Object e = Enum.valueOf((Class<? extends Enum>)clazz, str);
            return (T) clazz.cast(e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T parseValueFromString(ParsedValue<T> parsedValue, String inputValue) {
        Class<T> clazz = (Class<T>) parsedValue.getDefaultValue().getClass();
        if (clazz.isPrimitive()) {
            return (T) parsePrimitiveValue(clazz, inputValue);
        }
        if (Primitives.isWrapperType(clazz)) {
            return (T) parsePrimitiveValue(Primitives.unwrap(clazz), inputValue);
        }
        if (clazz.isEnum()) {
            return clazz.cast(Enum.valueOf((Class<? extends Enum>)clazz, inputValue));
        }
        return null;
    }

    public static Object parsePrimitiveValue(Class<?> clazz, String inputValue) {
        if (clazz == boolean.class) {
            return Boolean.parseBoolean(inputValue);
        } else if (clazz == int.class) {
            return Integer.parseInt(inputValue);
        } else if (clazz == long.class) {
            return Long.parseLong(inputValue);
        } else if (clazz == float.class) {
            return Float.parseFloat(inputValue);
        } else if (clazz == double.class) {
            return Double.parseDouble(inputValue);
        } else if (clazz == byte.class) {
            return Byte.parseByte(inputValue);
        } else if (clazz == short.class) {
            return Short.parseShort(inputValue);
        } else if (clazz == char.class) {
            return inputValue.charAt(0);
        }
        return inputValue;
    }
}
