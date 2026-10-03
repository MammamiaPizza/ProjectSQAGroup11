import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.BoundedCollection;
import org.apache.commons.collections4.collection.AbstractCollectionDecorator;
import org.apache.commons.collections4.collection.UnmodifiableBoundedCollection;
import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import org.junit.Test;

/**

 - Tests for {@link UnmodifiableBoundedCollection}.
 - <p>
 - Target bug COLLECTIONS-496: the factory method returned a new wrapper
 - even when the supplied collection was already an
 - UnmodifiableBoundedCollection instead of returning the existing instance.
  */
 public class UnmodifiableBoundedCollectionTest {
  // ---- helpers ----------------------------------------------------------------
  /** A simple BoundedCollection for testing.
  */
  private static BoundedCollection<String> testBoundedCollection() {
  return new FixedSizeListBoundedCollection<>(Arrays.asList(
          "One", "2", "Three", null, "4", "One", "5.0", "6.0",
          "Seven", "Eight", "Nine", "10", "11", "12",
          "Thirteen", "14", "15", "16"));
  }
  /** A bounded wrapper around a fixed-capacity ArrayList.
  */
  private static final class FixedSizeListBoundedCollection<E>
      extends AbstractCollectionDecorator<E>
      implements BoundedCollection<E> {
  private static final long serialVersionUID = 1L;
  private final int maxSize;
  FixedSizeListBoundedCollection(final List<E> initial) {
      super(new ArrayList<E>(initial));
      this.maxSize = initial.size();
  }
  @Override
  public boolean isFull() {
      return decorated().size() >= maxSize;
  }
  @Override
  public int maxSize() {
      return maxSize;
  }
  @Override
  @SuppressWarnings("unchecked")
  protected List<E> decorated() {
      return (List<E>) super.decorated();
  }
  // allow adding if not full
  @Override
  public boolean add(final E object) {
      if (isFull()) {
          throw new IllegalStateException("full");
      }
      return decorated().add(object);
  }
  }
  // ----
  1. identity preservation: decorator-factory on already-unmodifiable ----
  @Test
  public void testDecorateFactoryIdentity() {
  BoundedCollection<String> bounded = testBoundedCollection();
  BoundedCollection<String> first = UnmodifiableBoundedCollection
          .unmodifiableBoundedCollection(bounded);
  // decorate again – COLLECTIONS-496: must return same instance
  BoundedCollection<String> second = UnmodifiableBoundedCollection
          .unmodifiableBoundedCollection(first);
  assertSame("Double decoration must return the same instance",
          first, second);
  }
  @Test
  public void testDecorateFactoryIdentityViaCollectionOverload() {
  BoundedCollection<String> bounded = testBoundedCollection();
  BoundedCollection<String> first = UnmodifiableBoundedCollection
          .unmodifiableBoundedCollection(bounded);
  // use the Collection-accepting overload
  BoundedCollection<String> second = UnmodifiableBoundedCollection
          .unmodifiableBoundedCollection((Collection<? extends String>) first);
  assertSame("Collection-overload must also return same instance",
          first, second);
  }
  // ----
  2. content, size, bounds preserved after decoration --------------------
  @Test
  public void testContentsPreserved() {
  BoundedCollection<String> original = testBoundedCollection();
  BoundedCollection<String> ubc = UnmodifiableBoundedCollection
          .unmodifiableBoundedCollection(original);
  assertEquals("maxSize preserved", original.maxSize(), ubc.maxSize());
  assertEquals("isFull preserved", original.isFull(), ubc.isFull());
  assertEquals("size matches", original.size(), ubc.size());
  assertEquals("contents match",
          new ArrayList<>(original), new ArrayList<>(ubc));
  }
  // ----
  3. all mutators throw UOE ----------------------------------------------
  @Test(expected = UnsupportedOperationException.class)
  public void testAdd() {
  UnmodifiableBoundedCollection.unmodifiableBoundedCollection(testBoundedCollection())
          .add("new");
  }
  @Test(expected = UnsupportedOperationException.class)
  public void testAddAll() {
  UnmodifiableBoundedCollection.unmodifiableBoundedCollection(testBoundedCollection())
          .addAll(Arrays.asList("x"));
  }
  @Test(expected = UnsupportedOperationException.class)
  public void testClear() {
  UnmodifiableBoundedCollection.unmodifiableBoundedCollection(testBoundedCollection())
          .clear();
  }
  @Test(expected = UnsupportedOperationException.class)
  public void testRemove() {
  UnmodifiableBoundedCollection.unmodifiableBoundedCollection(testBoundedCollection())
          .remove("One");
  }
  @Test(expected = UnsupportedOperationException.class)
  public void testRemoveAll() {
  UnmodifiableBoundedCollection.unmodifiableBoundedCollection(testBoundedCollection())
          .removeAll(Arrays.asList("One"));
  }
  @Test(expected = UnsupportedOperationException.class)
  public void testRetainAll() {
  UnmodifiableBoundedCollection.unmodifiableBoundedCollection(testBoundedCollection())
          .retainAll(Arrays.asList("One"));
  }
  @Test(expected = UnsupportedOperationException.class)
  public void testIteratorRemove() {
  Iterator<String> it = UnmodifiableBoundedCollection
          .unmodifiableBoundedCollection(testBoundedCollection())
          .iterator();
  it.next();
  it.remove();
  }
  // ----
  4. iterator is unmodifiable but readable -------------------------------
  @Test
  public void testIteratorRead() {
  Iterator<String> it = UnmodifiableBoundedCollection
          .unmodifiableBoundedCollection(testBoundedCollection())
          .iterator();
  assertTrue(it instanceof UnmodifiableIterator);
  List<String> seen = new ArrayList<>();
  while (it.hasNext()) {
      seen.add(it.next());
  }
  assertEquals("iterator traverses all elements",
          new ArrayList<>(testBoundedCollection()), seen);
  }
  // ----
  5. null validation -----------------------------------------------------
  @Test(expected = IllegalArgumentException.class)
  public void testNullArgumentBoundedOverload() {
  UnmodifiableBoundedCollection.unmodifiableBoundedCollection(
          (BoundedCollection<?>) null);
  }
  @Test(expected = IllegalArgumentException.class)
  public void testNullArgumentCollectionOverload() {
  UnmodifiableBoundedCollection.unmodifiableBoundedCollection(
          (Collection<?>) null);
  }

}
