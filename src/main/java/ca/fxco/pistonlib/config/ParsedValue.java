package ca.fxco.pistonlib.config;

import ca.fxco.api.pistonlib.config.Category;
import ca.fxco.api.pistonlib.config.Observer;
import ca.fxco.api.pistonlib.config.Parser;
import ca.fxco.pistonlib.PistonLib;
import ca.fxco.pistonlib.helpers.ConfigUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.primitives.ImmutableIntArray;
import lombok.Getter;
import net.minecraft.commands.CommandSourceStack;

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
    protected final String[] requires;
    protected final String[] conflicts;
    protected final ImmutableIntArray fixes;
    protected final boolean requiresRestart;
    protected final T defaultValue; // Set by the recommended option
    protected final Parser<T>[] parsers;
    protected final Observer<T>[] observers;
    //public boolean requiresClient;
    //public final boolean clientOnly;

    public ParsedValue(Field field, String desc, String[] more, String[] keywords, Category[] categories,
                       String[] requires, String[] conflicts, boolean requiresRestart, int[] fixes,
                       Parser<?>[] parsers, Observer<?>[] observers) {
        this.field = field;
        this.name = field.getName();
        this.description = desc;
        this.moreInfo = more;
        this.keywords = ImmutableSet.copyOf(keywords);
        this.categories = ImmutableSet.copyOf(categories);
        this.requires = requires;
        this.conflicts = conflicts;
        this.requiresRestart = requiresRestart;
        this.fixes = ImmutableIntArray.copyOf(fixes);
        this.parsers = (Parser<T>[]) parsers;
        this.observers = (Observer<T>[]) observers;
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
        setValue(value, false);
    }

    public void setValue(T value, boolean load) {
        try {
            T currentValue = getValue();
            if (!value.equals(currentValue)) {
                if (!load) {
                    for (Parser<T> parser : this.parsers) {
                        value = parser.modify(this, value, false);
                    }
                }
                this.field.set(null, value);
                for (Observer<T> observer : this.observers) {
                    if (load) {
                        observer.onLoad(this, isDefaultValue());
                    } else {
                        observer.onChange(this, currentValue, value);
                    }
                }
            } else if (load) {
                for (Observer<T> observer : this.observers) {
                    observer.onLoad(this, isDefaultValue());
                }
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
    protected void setValueFromConfig(Object value) {
        T newValue = ConfigUtils.loadValueFromConfig(value, this);
        if (newValue == null) {
            // TODO: Allow for multiple custom config managers, not just the PistonLib one
            newValue = PistonLib.getConfigManager().tryLoadingValue(value, this);
        }
        if (newValue != null) {
            for (Parser<T> parser : this.parsers) {
                newValue = parser.modify(this, newValue, true);
            }
            setValue(newValue, true);
        }
    }

    /**
     * Returns the value that should be used within the config file
     */
    protected Object getValueForConfig() {
        // TODO: Allow for multiple custom config managers, not just the PistonLib one
        return PistonLib.getConfigManager().trySavingValue(this.getValue(), this);
    }

    /**
     * Used when attempting to parse the value from a command as a string
     */
    protected void parseValue(CommandSourceStack source, String inputValue) {
        boolean useDefault = true;
        for (Parser<T> parser : this.parsers) {
            T newValue = parser.parse(source, inputValue, this);
            if (newValue != null) {
                setValue(newValue);
                useDefault = false;
            }
        }
        if (useDefault) {
            T newValue = ConfigUtils.parseValueFromString(this, inputValue);
            if (newValue != null) {
                setValue(newValue);
            }
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
