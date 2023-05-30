package ca.fxco.pistonlib.config;

import ca.fxco.api.gametestlib.config.ParsedValue;
import ca.fxco.api.pistonlib.config.Category;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.primitives.ImmutableIntArray;
import lombok.Getter;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Set;

@Getter
public class ResolveValue<T> {

    protected final Field field;
    protected final String name;
    protected final String description;
    protected final String[] moreInfo;
    protected final Set<String> keywords;
    protected final Set<Category> categories;
    protected final ImmutableIntArray fixes;
    protected final T defaultValue; // Set by the recommended option
    //public boolean requiresClient;
    //public final boolean clientOnly;

    public ResolveValue(Field field, String desc, String[] more, String[] keywords, Category[] categories, int[] fixes) {
        this.field = field;
        this.name = field.getName();
        this.description = desc;
        this.moreInfo = more;
        this.keywords = ImmutableSet.copyOf(keywords);
        this.categories = ImmutableSet.copyOf(categories);
        this.fixes = ImmutableIntArray.copyOf(fixes);
        this.defaultValue = getValue();
        //this.clientOnly = this.groups.contains(FixGroup.CLIENTONLY);
        //this.requiresClient = this.clientOnly || this.groups.contains(FixGroup.CLIENT);
    }

    // Default value will always be the first value!
    public T[] getAllTestingValues() {
        return (T[]) getAllTestingValuesObj();
    }

    public Object[] getAllTestingValuesObj() {
        Class<T> clazz = (Class<T>) ClassUtils.primitiveToWrapper(field.getType());
        if (clazz == Boolean.class) {
            return new Boolean[]{(Boolean) this.defaultValue,!((Boolean) this.defaultValue)};
        } else if (clazz == Integer.class) { // TODO: Def do this better...
            int def = (int) this.defaultValue;
            if (def != 0 && def != 1) {
                return new Integer[]{def, 0, 1};
            }
            return new Integer[]{def, def == 0 ? 1 : 0};
        } else if (clazz == String.class) {
            return new String[]{(String) this.defaultValue};
        } else if (clazz.isEnum()) {
            T[] enums = clazz.getEnumConstants();
            if (enums[0] == this.defaultValue) {
                return enums;
            }
            Object[] objs = new Object[enums.length];
            objs[0] = this.defaultValue;
            for (int i = 1; i < enums.length; i++) {
                if (enums[i] == this.defaultValue) {
                    objs[i] = enums[0];
                } else {
                    objs[i] = enums[i];
                }
            }
            return objs;
        }
        System.out.println("This values does not have any testing values yet: " + clazz);
        return new Object[]{defaultValue}; // TODO: Actually add testing values instead of just the default value
    }

    /**
     * Sets this value to its default value
     */
    public void reset() {
        setValue(this.defaultValue);
    }

    /**
     * Returns true if it's currently the default value
     */
    public boolean isDefaultValue() {
        return this.defaultValue.equals(getValue());
    }

    public void setValueObj(Object value) {
        try {
            if (!value.equals(getValue())) {
                this.field.set(null, value);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setValue(T value) {
        try {
            if (!value.equals(getValue())) {
                this.field.set(null, value);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public T getValue() {
        try {
            return (T) this.field.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Should not be used unless loading from the config
     */
    @SuppressWarnings("unchecked")
    protected void setValueFromConfig(Object value) {
        if (this.defaultValue.getClass() == value.getClass()) {
            setValue((T) this.defaultValue.getClass().cast(value));
        } else if (this.defaultValue.getClass().isEnum() && value instanceof String str) {
            Object e = Enum.valueOf((Class<? extends Enum>)this.defaultValue.getClass(), str);
            setValue((T) this.defaultValue.getClass().cast(e));
        }
    }

    /**
     * Returns true if the config value name or its description matches the search term
     */
    public boolean matchesTerm(String search) {
        search = search.toLowerCase(Locale.ROOT);
        if (this.name.toLowerCase(Locale.ROOT).contains(search)) {
            return true;
        }
        return Sets.newHashSet(this.description.toLowerCase(Locale.ROOT).split("\\W+")).contains(search);
    }

    /**
     * Returns true if the search term matches one of the config value keywords
     */
    public boolean doKeywordMatchSearch(String search) {
        search = search.toLowerCase(Locale.ROOT);
        for (String keyword : this.keywords) {
            if (keyword.toLowerCase(Locale.ROOT).equals(search)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the config value contains a category which matches the search term
     */
    public boolean doCategoryMatchSearch(String search) {
        search = search.toLowerCase(Locale.ROOT);
        for (Category category : this.categories) {
            if (category.name().toLowerCase(Locale.ROOT).equals(search)) {
                return true;
            }
        }
        return false;
    }
}
