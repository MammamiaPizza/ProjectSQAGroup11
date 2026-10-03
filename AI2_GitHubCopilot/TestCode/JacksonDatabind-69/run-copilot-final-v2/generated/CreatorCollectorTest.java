import static org.junit.Assert.*;
 import static org.mockito.Mockito.*;

 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.LinkedHashMap;
 import java.util.List;

 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.powermock.modules.junit4.PowerMockRunner;
 import org.powermock.reflect.Whitebox;

 import com.fasterxml.jackson.databind.BeanDescription;
 import com.fasterxml.jackson.databind.DeserializationConfig;
 import com.fasterxml.jackson.databind.JavaType;
 import com.fasterxml.jackson.databind.MapperFeature;
 import com.fasterxml.jackson.databind.cfg.MapperConfig;
 import com.fasterxml.jackson.databind.deser.CreatorProperty;
 import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
 import com.fasterxml.jackson.databind.deser.impl.CreatorCollector;
 import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
 import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
 import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;

 /**
  * Tests for {@link CreatorCollector} focusing on property-based creator handling
  * to expose issue #1476 where creator property name lookup fails.
  */
 @RunWith(PowerMockRunner.class)
 public class CreatorCollectorTest {

     private CreatorCollector collector;
     private BeanDescription beanDesc;
     private MapperConfig<?> config;

     @Before
     public void setUp() {
         beanDesc = mock(BeanDescription.class);
         config = mock(MapperConfig.class);
         when(config.canOverrideAccessModifiers()).thenReturn(true);
         when(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS)).thenReturn(true);
         collector = new CreatorCollector(beanDesc, config);
     }

     // -------------------------------------------------------------------
     //  addPropertyCreator / property-based creator tests
     // -------------------------------------------------------------------

     @Test
     public void testAddPropertyCreator_singleProperty_setsCreatorAndArgs() {
         AnnotatedWithParams creator = mock(AnnotatedWithParams.class);
         CreatorProperty prop = mock(CreatorProperty.class);
         when(prop.getName()).thenReturn("intField");
         when(prop.getInjectableValueId()).thenReturn(null);

         SettableBeanProperty[] props = { prop };
         collector.addPropertyCreator(creator, true, props);

         assertTrue("should flag property-based creator", collector.hasPropertyBasedCreator());
         assertSame("property args should be stored",
                 props, Whitebox.getInternalState(collector, "_propertyBasedArgs"));
         assertSame("creator should be stored at C_PROPS slot",
                 creator, Whitebox.getInternalState(collector, "_creators[7]"));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testAddPropertyCreator_duplicateNames_throws() {
         AnnotatedWithParams creator = mock(AnnotatedWithParams.class);
         CreatorProperty p1 = mock(CreatorProperty.class);
         CreatorProperty p2 = mock(CreatorProperty.class);
         when(p1.getName()).thenReturn("sameName");
         when(p1.getInjectableValueId()).thenReturn(null);
         when(p2.getName()).thenReturn("sameName");
         when(p2.getInjectableValueId()).thenReturn(null);

         SettableBeanProperty[] props = { p1, p2 };
         collector.addPropertyCreator(creator, true, props);
     }

     @Test
     public void testAddPropertyCreator_withInjectables_skipsEmptyName() {
         AnnotatedWithParams creator = mock(AnnotatedWithParams.class);
         CreatorProperty p1 = mock(CreatorProperty.class);
         CreatorProperty p2 = mock(CreatorProperty.class);
         when(p1.getName()).thenReturn("");
         when(p1.getInjectableValueId()).thenReturn("inj1");
         when(p2.getName()).thenReturn("name2");
         when(p2.getInjectableValueId()).thenReturn(null);

         SettableBeanProperty[] props = { p1, p2 };
         // no exception expected
         collector.addPropertyCreator(creator, true, props);
         assertNotNull(Whitebox.getInternalState(collector, "_propertyBasedArgs"));
     }

     @Test
     public void testAddPropertyCreator_explicitMarksExplicitCreators() {
         AnnotatedWithParams creator = mock(AnnotatedWithParams.class);
         CreatorProperty prop = mock(CreatorProperty.class);
         when(prop.getName()).thenReturn("x");
         when(prop.getInjectableValueId()).thenReturn(null);

         SettableBeanProperty[] props = { prop };
         collector.addPropertyCreator(creator, true, props);

         int explicitCreators = Whitebox.getInternalState(collector, "_explicitCreators");
         assertEquals("explicit creators mask should include C_PROPS bit",
                 1 << CreatorCollector.C_PROPS, explicitCreators);
     }

     @Test
     public void testAddPropertyCreator_implicitDoesNotMarkExplicit() {
         AnnotatedWithParams creator = mock(AnnotatedWithParams.class);
         CreatorProperty prop = mock(CreatorProperty.class);
         when(prop.getName()).thenReturn("y");
         when(prop.getInjectableValueId()).thenReturn(null);

         // call the no-explicit overload that delegates with false
         collector.addPropertyCreator(creator, new CreatorProperty[] { prop });

         int explicitCreators = Whitebox.getInternalState(collector, "_explicitCreators");
         assertEquals("explicit creators mask should be 0 for implicit", 0, explicitCreators);
     }

     // -------------------------------------------------------------------
     //  verifyNonDup / conflict resolution
     // -------------------------------------------------------------------

     @Test(expected = IllegalArgumentException.class)
     public void testVerifyNonDup_conflictSameTypeAndExplicit_throws() {
         AnnotatedWithParams c1 = mock(AnnotatedWithParams.class);
         when(c1.getRawParameterType(0)).thenReturn((Class) String.class);
         AnnotatedWithParams c2 = mock(AnnotatedWithParams.class);
         when(c2.getRawParameterType(0)).thenReturn((Class) String.class);

         // first explicit string creator
         collector.addStringCreator(c1, true);
         // second explicit string creator of same type -> conflict
         collector.addStringCreator(c2, true);
     }

     @Test
     public void testVerifyNonDup_moreSpecificTypeOverridesImplicit() {
         AnnotatedWithParams cOld = mock(AnnotatedWithParams.class);
         AnnotatedWithParams cNew = mock(AnnotatedWithParams.class);
         when(cOld.getRawParameterType(0)).thenReturn((Class) Object.class);
         when(cNew.getRawParameterType(0)).thenReturn((Class) String.class);

         // first implicit (old, general)
         collector.addStringCreator(cOld, false);
         // new implicit with more specific type -> should replace
         collector.addStringCreator(cNew, false);

         assertEquals("new creator should be stored",
                 cNew, Whitebox.getInternalState(collector, "_creators[1]"));
     }

     @Test
     public void testVerifyNonDup_implicitKeptWhenNewMoreGeneric() {
         AnnotatedWithParams cOld = mock(AnnotatedWithParams.class);
         AnnotatedWithParams cNew = mock(AnnotatedWithParams.class);
         when(cOld.getRawParameterType(0)).thenReturn((Class) String.class);
         when(cNew.getRawParameterType(0)).thenReturn((Class) Object.class);

         // old implicit (specific), new implicit (generic)
         collector.addStringCreator(cOld, false);
         collector.addStringCreator(cNew, false);

         // old specific should be kept
         assertEquals("old creator should be kept",
                 cOld, Whitebox.getInternalState(collector, "_creators[1]"));
     }

     // -------------------------------------------------------------------
     //  constructValueInstantiator path
     // -------------------------------------------------------------------

     @Test
     public void
testConstructValueInstantiator_withPropertyCreator_returnsInstantiatorWithProperties() {
         JavaType type = mock(JavaType.class);
         when(type.getRawClass()).thenReturn((Class) Object.class);
         when(beanDesc.getType()).thenReturn(type);
         DeserializationConfig deserConfig = mock(DeserializationConfig.class);

         AnnotatedWithParams creator = mock(AnnotatedWithParams.class);
         CreatorProperty prop = mock(CreatorProperty.class);
         when(prop.getName()).thenReturn("intField");
         when(prop.getInjectableValueId()).thenReturn(null);

         SettableBeanProperty[] props = { prop };
         collector.addPropertyCreator(creator, true, props);

         Object instantiator = collector.constructValueInstantiator(deserConfig);
         assertTrue(instantiator instanceof StdValueInstantiator);

         SettableBeanProperty[] storedProps =
                 Whitebox.getInternalState(instantiator, "_propertyBasedArgs");
         assertArrayEquals("property-based args should be retained", props, storedProps);
     }

     @Test
     public void testConstructValueInstantiator_defaultList_returnsVanilla() {
         JavaType type = mock(JavaType.class);
         when(type.getRawClass()).thenReturn((Class) ArrayList.class);
         when(beanDesc.getType()).thenReturn(type);
         DeserializationConfig deserConfig = mock(DeserializationConfig.class);

         Object instantiator = collector.constructValueInstantiator(deserConfig);
         assertTrue("ArrayList without creators should yield Vanilla",
                 instantiator instanceof CreatorCollector.Vanilla);
     }

     @Test
     public void testConstructValueInstantiator_defaultHashMap_returnsVanilla() {
         JavaType type = mock(JavaType.class);
         when(type.getRawClass()).thenReturn((Class) HashMap.class);
         when(beanDesc.getType()).thenReturn(type);
         DeserializationConfig deserConfig = mock(DeserializationConfig.class);

         Object instantiator = collector.constructValueInstantiator(deserConfig);
         assertTrue(instantiator instanceof CreatorCollector.Vanilla);
     }
 }
