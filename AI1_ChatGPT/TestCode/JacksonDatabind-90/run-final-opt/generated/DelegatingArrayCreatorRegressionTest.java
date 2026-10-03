import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

public class DelegatingArrayCreatorRegressionTest
{
    @Test
    public void abstractTypeCanBeCreatedUsingArrayDelegateCreator() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ArrayOnlyType value = mapper.readValue("[1,2,3]", ArrayOnlyType.class);

        assertEquals(ArrayOnlyTypeImpl.class, value.getClass());
        assertArrayEquals(new int[] { 1, 2, 3 }, value.values);
    }

    @Test
    public void arrayDelegateCreatorIsSelectedSeparatelyFromRegularDelegateCreator()
            throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        BothDelegateTypes arrayValue = mapper.readValue("[4,5]", BothDelegateTypes.class);
        BothDelegateTypes objectValue = mapper.readValue("{\"answer\":42}",
                BothDelegateTypes.class);

        assertArrayEquals(new int[] { 4, 5 }, arrayValue.arrayValues);
        assertNull(arrayValue.objectValues);
        assertNull(objectValue.arrayValues);
        assertEquals(Integer.valueOf(42), objectValue.objectValues.get("answer"));
    }

    @Test
    public void baseValueInstantiatorHasNoDelegateCapabilitiesByDefault() {
        ValueInstantiator instantiator = new ValueInstantiator.Base(Object.class);

        assertFalse(instantiator.canCreateUsingDelegate());
        assertFalse(instantiator.canCreateUsingArrayDelegate());
        assertNull(instantiator.getDelegateType(null));
        assertNull(instantiator.getArrayDelegateType(null));
    }

    public static abstract class ArrayOnlyType {
        public final int[] values;

        protected ArrayOnlyType(int[] values) {
            this.values = values;
        }

        @JsonCreator
        public static ArrayOnlyType fromArray(int[] values) {
            return new ArrayOnlyTypeImpl(values);
        }
    }

    public static class ArrayOnlyTypeImpl extends ArrayOnlyType {
        public ArrayOnlyTypeImpl(int[] values) {
            super(values);
        }
    }

    public static abstract class BothDelegateTypes {
        public final int[] arrayValues;
        public final Map<String, Integer> objectValues;

        protected BothDelegateTypes(int[] arrayValues, Map<String, Integer> objectValues) {
            this.arrayValues = arrayValues;
            this.objectValues = objectValues;
        }

        @JsonCreator
        public static BothDelegateTypes fromArray(int[] values) {
            return new BothDelegateTypesImpl(values, null);
        }

        @JsonCreator
        public static BothDelegateTypes fromObject(Map<String, Integer> values) {
            return new BothDelegateTypesImpl(null, values);
        }
    }

    public static class BothDelegateTypesImpl extends BothDelegateTypes {
        public BothDelegateTypesImpl(int[] arrayValues, Map<String, Integer> objectValues) {
            super(arrayValues, objectValues);
        }
    }
}
