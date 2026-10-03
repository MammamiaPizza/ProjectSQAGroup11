package org.apache.commons.collections4.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class SetUniqueListSubListUnmodifiableTest {

    private interface Operation {
        void execute();
    }

    private SetUniqueList<String> createList() {
        return SetUniqueList.setUniqueList(new ArrayList<String>(
                Arrays.asList("a", "b", "c", "d")));
    }

    private void assertUnsupported(final Operation operation) {
        try {
            operation.execute();
            fail("subList should be unmodifiable");
        } catch (final UnsupportedOperationException expected) {
            // expected
        }
    }

    private void assertParentUnchanged(final SetUniqueList<String> parent) {
        assertEquals(Arrays.asList("a", "b", "c", "d"), parent);
        assertEquals(new HashSet<String>(Arrays.asList("a", "b", "c", "d")),
                parent.asSet());
    }

    @Test
    public void testNonEmptySubListRejectsAddOperations() {
        final SetUniqueList<String> parent = createList();
        final List<String> subList = parent.subList(1, 3);

        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                subList.add("x");
            }
        });
        assertParentUnchanged(parent);

        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                subList.add(0, "x");
            }
        });
        assertParentUnchanged(parent);
    }

    @Test
    public void testNonEmptySubListRejectsRemovalAndReplacement() {
        final SetUniqueList<String> parent = createList();
        final List<String> subList = parent.subList(1, 3);

        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                subList.set(0, "x");
            }
        });
        assertParentUnchanged(parent);

        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                subList.remove(0);
            }
        });
        assertParentUnchanged(parent);

        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                subList.remove("b");
            }
        });
        assertParentUnchanged(parent);
    }

    @Test
    public void testNonEmptySubListRejectsBulkAddAndClear() {
        final SetUniqueList<String> parent = createList();
        final List<String> subList = parent.subList(1, 3);

        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                subList.addAll(Arrays.asList("x", "y"));
            }
        });
        assertParentUnchanged(parent);

        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                subList.addAll(1, Arrays.asList("x", "y"));
            }
        });
        assertParentUnchanged(parent);

        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                subList.clear();
            }
        });
        assertParentUnchanged(parent);
    }

    @Test
    public void testEmptyBoundarySubListIsUnmodifiable() {
        final SetUniqueList<String> parent = createList();
        final List<String> subList = parent.subList(0, 0);

        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                subList.add("x");
            }
        });
        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                subList.addAll(Arrays.asList("x", "y"));
            }
        });
        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                subList.remove("x");
            }
        });
        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                subList.clear();
            }
        });
        assertParentUnchanged(parent);
    }

    @Test
    public void testFullRangeSubListIteratorCannotRemove() {
        final SetUniqueList<String> parent = createList();
        final List<String> subList = parent.subList(0, parent.size());
        final Iterator<String> iterator = subList.iterator();

        assertEquals("a", iterator.next());
        assertUnsupported(new Operation() {
            @Override
            public void execute() {
                iterator.remove();
            }
        });

        assertParentUnchanged(parent);
    }

@Test
public void testNonEmptySubListListIteratorRejectsSetAndAdd() {
    final SetUniqueList<String> parent = SetUniqueList.setUniqueList(
            new java.util.ArrayList<String>(java.util.Arrays.asList("a", "b", "c", "d")));

    final java.util.ListIterator<String> setIterator = parent.subList(1, 3).listIterator();
    setIterator.next();
    assertUnsupportedMutation(new java.lang.Runnable() {
        @Override
        public void run() {
            setIterator.set("x");
        }
    });
    org.junit.Assert.assertEquals(java.util.Arrays.asList("a", "b", "c", "d"), parent);

    final java.util.ListIterator<String> addIterator = parent.subList(1, 3).listIterator();
    assertUnsupportedMutation(new java.lang.Runnable() {
        @Override
        public void run() {
            addIterator.add("x");
        }
    });
    org.junit.Assert.assertEquals(java.util.Arrays.asList("a", "b", "c", "d"), parent);
}

@Test
public void testNonEmptySubListRejectsBulkRemovalOperations() {
    final SetUniqueList<String> removeAllParent = SetUniqueList.setUniqueList(
            new java.util.ArrayList<String>(java.util.Arrays.asList("a", "b", "c", "d")));
    assertUnsupportedMutation(new java.lang.Runnable() {
        @Override
        public void run() {
            removeAllParent.subList(1, 3).removeAll(java.util.Arrays.asList("b"));
        }
    });
    org.junit.Assert.assertEquals(java.util.Arrays.asList("a", "b", "c", "d"), removeAllParent);

    final SetUniqueList<String> retainAllParent = SetUniqueList.setUniqueList(
            new java.util.ArrayList<String>(java.util.Arrays.asList("a", "b", "c", "d")));
    assertUnsupportedMutation(new java.lang.Runnable() {
        @Override
        public void run() {
            retainAllParent.subList(1, 3).retainAll(java.util.Arrays.asList("b"));
        }
    });
    org.junit.Assert.assertEquals(java.util.Arrays.asList("a", "b", "c", "d"), retainAllParent);
}

@Test
public void testEmptySubListListIteratorRejectsAdd() {
    final SetUniqueList<String> parent = SetUniqueList.setUniqueList(
            new java.util.ArrayList<String>(java.util.Arrays.asList("a", "b", "c", "d")));
    final java.util.ListIterator<String> iterator = parent.subList(2, 2).listIterator();

    assertUnsupportedMutation(new java.lang.Runnable() {
        @Override
        public void run() {
            iterator.add("x");
        }
    });

    org.junit.Assert.assertEquals(java.util.Arrays.asList("a", "b", "c", "d"), parent);
}

private void assertUnsupportedMutation(final java.lang.Runnable operation) {
    try {
        operation.run();
        org.junit.Assert.fail("subList should be unmodifiable");
    } catch (final UnsupportedOperationException expected) {
        // expected
    }
}
}
