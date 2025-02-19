package io.scalecube.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import io.scalecube.config.source.LoadedConfigProperty;

class MultimapConfigPropertyImpl<T> extends AbstractSimpleConfigProperty<Map<String, List<T>>>
    implements MultimapConfigProperty<T> {

  static <T> Function<String, Map<String, List<T>>> toMultimapPropertyParser(
      Function<String, T> valueParser) {
    return str -> {
      Map<String, List<T>> result = new HashMap<>();
      String[] tokens = str.split(",");
      String key = null;
      for (String token : tokens) {
        String[] entry = token.split("=", 2);
        String value;
        if (entry.length > 1) { // entry ["key", "value"]
          key = entry[0];
          value = entry[1];
        } else { // only value ["value"]
          value = entry[0];
        }
        if (key != null) {
          result.computeIfAbsent(key, k -> new ArrayList<>()).add(valueParser.apply(value));
        }
      }
      return result;
    };
  }

  MultimapConfigPropertyImpl(
      String name,
      Map<String, LoadedConfigProperty> propertyMap,
      Map<String, Map<Class<?>, PropertyCallback<?>>> propertyCallbackMap,
      Function<String, T> valueParser) {
    super(
        name,
        getMapPropertyClass(valueParser),
        propertyMap,
        propertyCallbackMap,
        toMultimapPropertyParser(valueParser));
  }

  @Override
  public Map<String, List<T>> value(Map<String, List<T>> defaultValue) {
    return value().orElse(defaultValue);
  }

  @Override
  public Map<String, List<T>> valueOrThrow() {
    return value().orElseThrow(this::newNoSuchElementException);
  }

  @SuppressWarnings("unchecked")
  private static <T> Class<Map<String, List<T>>> getMapPropertyClass(
      Function<String, T> valueParser) {
    Class<?> result = null;
    if (ConfigRegistryImpl.STRING_PARSER == valueParser) {
      result = StringMultimap.class;
    } else if (ConfigRegistryImpl.DOUBLE_PARSER == valueParser) {
      result = DoubleMultimap.class;
    } else if (ConfigRegistryImpl.LONG_PARSER == valueParser) {
      result = LongMultimap.class;
    } else if (ConfigRegistryImpl.INT_PARSER == valueParser) {
      result = IntMultimap.class;
    } else if (ConfigRegistryImpl.DURATION_PARSER == valueParser) {
      result = DurationMultimap.class;
    }
    if (result == null) {
      throw new IllegalArgumentException(
          "MultimapConfigPropertyImpl: unsupported multimap valueParser " + valueParser);
    }
    return (Class<Map<String, List<T>>>) result;
  }

  private static abstract class StringMultimap implements Map<String, List<String>> {}

  private static abstract class DoubleMultimap implements Map<String, List<Double>> {}

  private static abstract class LongMultimap implements Map<String, List<Long>> {}

  private static abstract class IntMultimap implements Map<String, List<Integer>> {}

  private static abstract class DurationMultimap implements Map<String, List<Duration>> {}
}
