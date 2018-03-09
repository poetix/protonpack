package com.codepoetics.protonpack.stateful;

import java.util.Objects;

public final class TaggedValue<S, T> {

  public static <S, T> TaggedValue<S, T> of(S tag, T value) {
    return new TaggedValue<>(tag, value);
  }

  private final S tag;
  private final T value;

  private TaggedValue(S tag, T value) {
    this.tag = tag;
    this.value = value;
  }

  public S getTag() {
    return tag;
  }

  public T getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    return this == o
        || (o instanceof TaggedValue
    && Objects.equals(((TaggedValue) o).tag, tag)
    && Objects.equals(((TaggedValue) o).value, value));
  }

  @Override
  public int hashCode() {
    return Objects.hash(tag, value);
  }

  @Override
  public String toString() {
    return String.format("Tagged(%s, %s)", tag, value);
  }
}
