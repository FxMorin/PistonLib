package ca.fxco.pistonlib.config;

import ca.fxco.api.pistonlib.config.Category;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.primitives.ImmutableIntArray;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Set;

@Getter
public class ParsedValue<T> {

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

    public ParsedValue(Field field, String desc, String[] more, String[] keywords, Category[] categories, int[] fixes) {
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
