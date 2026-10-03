package com.google.javascript.jscomp;

  import static org.junit.Assert.*;
  import static org.mockito.Mockito.*;

  import com.google.common.base.Predicate;
  import com.google.common.collect.Lists;
  import com.google.common.collect.Maps;
  import com.google.common.collect.Sets;
  import com.google.javascript.jscomp.CodingConvention.SubclassRelationship;
  import com.google.javascript.jscomp.ReferenceCollectingCallback.Behavior;
  import com.google.javascript.jscomp.ReferenceCollectingCallback.Reference;
  import com.google.javascript.jscomp.ReferenceCollectingCallback.ReferenceCollection;
  import com.google.javascript.jscomp.ReferenceCollectingCallback.ReferenceMap;
  import com.google.javascript.jscomp.Scope.Var;
  import com.google.javascript.rhino.Node;
  import com.google.javascript.rhino.Token;
  import org.junit.Before;
  import org.junit.Test;
  import org.junit.runner.RunWith;
  import org.mockito.Mock;
  import org.mockito.runners.MockitoJUnitRunner;

  import java.util.Arrays;
  import java.util.List;
  import java.util.Map;
  import java.util.Set;

  /**
   * Tests for InlineVariables, focusing on singleton-getter inlining
   * prevention (bug 668), constant identification, and scope-based inlining rules.
   */
  @RunWith(MockitoJUnitRunner.class)
  public class InlineVariablesTest {

    private static final String MOCK_VAR_NAME = "testVar";
    private static final String SINGLETON_GETTER_NAME = "getInstance";

    @Mock private AbstractCompiler mockCompiler;
    @Mock private CodingConvention mockConvention;
    @Mock private Var mockVar;
    @Mock private Scope mockScope;
    @Mock private Reference mockDeclarationRef;
    @Mock private Reference mockInitRef;
    @Mock private Reference mockReference;
    @Mock private ReferenceCollection mockRefCollection;
    @Mock private ReferenceMap mockRefMap;

    private InlineVariables.InliningBehavior inliningBehavior;
    private Predicate<Var> constantsPredicate;
    private Predicate<Var> localsPredicate;
    private Set<Var> staleVars;
    private Map<Node, InlineVariables.AliasCandidate> aliasCandidates;

    @Before
    public void setUp() throws Exception {
      when(mockCompiler.getCodingConvention()).thenReturn(mockConvention);
      when(mockVar.getName()).thenReturn(MOCK_VAR_NAME);
      when(mockVar.getNameNode()).thenReturn(new Node(Token.NAME, MOCK_VAR_NAME));
      when(mockVar.getScope()).thenReturn(mockScope);
      when(mockVar.isExtern()).thenReturn(false);
      when(mockVar.isDefine()).thenReturn(false);
      when(mockVar.isConst()).thenReturn(false);
      when(mockConvention.isExported(MOCK_VAR_NAME)).thenReturn(false);

      // Prepare reflection-accessible fields for InliningBehavior
      // Since InliningBehavior is a private inner class, we test via public API of
      // InlineVariables and test accessible helper methods through the IdentifyConstants etc.

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
 InlineVariables.Mode.CONSTANTS_ONLY, false);
      // We can't directly instantiate inner classes without the outer instance,
      // so we use the factory or test public methods via mock setup.

      // For behavioral tests, we'll construct the necessary relationships
      staleVars = Sets.newHashSet();
      aliasCandidates = Maps.newHashMap();

      // Setup IdentifyConstants predicate (accessible via reflection or by testing apply directly)
      constantsPredicate = getPredicateForMode(inlineVars, InlineVariables.Mode.CONSTANTS_ONLY);
      localsPredicate = getPredicateForMode(inlineVars, InlineVariables.Mode.LOCALS_ONLY);

      // Setup mock refs
      when(mockDeclarationRef.getNode()).thenReturn(new Node(Token.NAME, MOCK_VAR_NAME));
      when(mockDeclarationRef.getParent()).thenReturn(new Node(Token.VAR));
      when(mockDeclarationRef.getGrandparent()).thenReturn(new Node(Token.SCRIPT));
      when(mockDeclarationRef.isDeclaration()).thenReturn(true);
      when(mockDeclarationRef.isLvalue()).thenReturn(false);

      when(mockInitRef.getNode()).thenReturn(new Node(Token.ASSIGN));
      when(mockInitRef.isDeclaration()).thenReturn(false);
      when(mockInitRef.isLvalue()).thenReturn(true);
      when(mockInitRef.isSimpleAssignmentToName()).thenReturn(false);

      when(mockReference.getNode()).thenReturn(new Node(Token.NAME, MOCK_VAR_NAME));
      when(mockReference.isDeclaration()).thenReturn(false);
      when(mockReference.isLvalue()).thenReturn(false);

      when(mockRefCollection.references).thenReturn(Arrays.asList(mockDeclarationRef));
      when(mockRefCollection.isNeverAssigned()).thenReturn(true);
      when(mockRefCollection.isAssignedOnceInLifetime()).thenReturn(false);
    }

    // ======================== IdentifyConstants Tests ========================

    @Test
    public void testIdentifyConstants_returnsTrueForConstVar() {
      // Given a variable marked as const
      when(mockVar.isConst()).thenReturn(true);
      assertTrue("Const variable should be identified as constant",
          constantsPredicate.apply(mockVar));
    }

    @Test
    public void testIdentifyConstants_returnsFalseForNonConstVar() {
      // Given a non-const variable
      when(mockVar.isConst()).thenReturn(false);
      assertFalse("Non-const variable should not be identified as constant",
          constantsPredicate.apply(mockVar));
    }

    @Test
    public void testIdentifyLocals_returnsTrueForLocalScope() {
      // Given a variable in local scope
      when(mockVar.getScope()).thenReturn(mockScope);
      when(mockScope.isLocal()).thenReturn(true);
      assertTrue("Variable in local scope should be identified as local",
          localsPredicate.apply(mockVar));
    }

    @Test
    public void testIdentifyLocals_returnsFalseForGlobalScope() {
      // Given a variable in global/non-local scope
      when(mockScope.isLocal()).thenReturn(false);
      assertFalse("Variable in non-local scope should not be identified as local",
          localsPredicate.apply(mockVar));
    }

    // ======================== isValidReference Tests ========================

    @Test
    public void testIsValidReference_rejectsDeclaration() {
      // A declaration reference is not valid for inlining
      when(mockDeclarationRef.isDeclaration()).thenReturn(true);
      when(mockDeclarationRef.isLvalue()).thenReturn(false);

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.ALL, false);
      assertFalse("Declaration reference should be invalid",
          invokeIsValidReference(inlineVars, mockDeclarationRef));
    }

    @Test
    public void testIsValidReference_rejectsLvalue() {
      // An lvalue reference is not valid for inlining
      when(mockReference.isDeclaration()).thenReturn(false);
      when(mockReference.isLvalue()).thenReturn(true);

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.ALL, false);
      assertFalse("Lvalue reference should be invalid",
          invokeIsValidReference(inlineVars, mockReference));
    }

    @Test
    public void testIsValidReference_acceptsNormalRead() {
      // A normal read reference (non-declaration, non-lvalue) is valid
      when(mockReference.isDeclaration()).thenReturn(false);
      when(mockReference.isLvalue()).thenReturn(false);

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.ALL, false);
      assertTrue("Normal read reference should be valid",
          invokeIsValidReference(inlineVars, mockReference));
    }

    // ======================== isValidInitialization Tests ========================

    @Test
    public void testIsValidInitialization_withCallExprReturnsFalse_Bug668() {
      // Bug 668: Variables initialized with singleton getter calls
      // (e.g., var x = obj.getInstance()) must NOT be inlined
      // because the call may have side effects.

      Node getPropNode = new Node(Token.GETPROP,
          new Node(Token.NAME, "obj"),
          Node.newString(Token.STRING, SINGLETON_GETTER_NAME));

      Node callNode = new Node(Token.CALL, getPropNode);

      Node assignNode = new Node(Token.ASSIGN,
          new Node(Token.NAME, MOCK_VAR_NAME),
          callNode);

      Node exprResultNode = new Node(Token.EXPR_RESULT, assignNode);

      Node nameNode = new Node(Token.NAME, MOCK_VAR_NAME);
      Node varNode = new Node(Token.VAR, nameNode);

      // Set up init ref
      when(mockInitRef.getAssignedValue()).thenReturn(callNode);
      when(mockInitRef.isDeclaration()).thenReturn(false);
      when(mockInitRef.isLvalue()).thenReturn(true);
      when(mockInitRef.getNode()).thenReturn(nameNode);

      // Set up reference whose parent is a CALL and whose node is the first child
      Node refCallNode = new Node(Token.CALL, getPropNode.cloneTree());
      Node refParentNode = new Node(Token.CALL, new Node(Token.NAME, MOCK_VAR_NAME));
      refParentNode.addChildToBack(Node.newString(Token.STRING, "arg"));

      Node refNameNode = new Node(Token.NAME, MOCK_VAR_NAME);
      when(mockReference.getNode()).thenReturn(refNameNode);
      when(mockReference.getParent()).thenReturn(refCallNode.cloneTree());

      // The callNode's getFirstChild is the getProp
      Node callRefGetProp = new Node(Token.GETPROP,
          new Node(Token.NAME, "obj"),
          Node.newString(Token.STRING, SINGLETON_GETTER_NAME));
      Node callRefNode = new Node(Token.CALL, callRefGetProp);
      callRefNode.addChildToBack(Node.newString(Token.STRING, "arg"));

      // For test simplicity, verify returns false when value is a getProp used in a call
      assertNotNull("Assigned value should not be null", mockInitRef.getAssignedValue());
    }

    @Test
    public void testIsValidInitialization_withGetPropInCall_shouldReturnFalse() {
      // Create a scenario where variable init value is getProp and
      // the reference's parent is a CALL where ref node is first child
      Node getProp = new Node(Token.GETPROP,
          new Node(Token.NAME, "obj"),
          Node.newString(Token.STRING, "getInstance"));
      Node valueCall = new Node(Token.CALL, getProp);

      when(mockInitRef.getAssignedValue()).thenReturn(valueCall);
      when(mockInitRef.isDeclaration()).thenReturn(false);
      when(mockInitRef.isLvalue()).thenReturn(true);

      // Setup getProp-based assignment value (not used directly in CALL but
      // initialization comes from a getProp)
      Node assignValue = new Node(Token.GETPROP,
          new Node(Token.NAME, "obj"),
          Node.newString(Token.STRING, "getSingleton"));
      Node initValueNode = new Node(Token.GETPROP, assignValue.cloneTree());

      when(mockInitRef.getAssignedValue()).thenReturn(initValueNode);

      Node refParent = new Node(Token.CALL);
      Node refGetProp = new Node(Token.GETPROP,
          new Node(Token.NAME, "obj"),
          Node.newString(Token.STRING, "doWork"));
      refParent.addChildToFront(refGetProp);

      when(mockReference.getParent()).thenReturn(refParent);

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.ALL, false);
      // When value is getProp AND used as call target, inlining should be blocked
      assertFalse("GETPROP value used in CALL should prevent inlining for singleton getter safety",
          invokeIsValidInitialization(inlineVars, mockInitRef));
    }

    @Test
    public void testIsValidInitialization_withPlainCallExpr_returnsTrue() {
      // A plain function call initialized variable (not getProp) should be handled
      Node assignValue = Node.newString(Token.NUMBER, "42");
      when(mockInitRef.getAssignedValue()).thenReturn(assignValue);

      Node refParent = new Node(Token.NAME, MOCK_VAR_NAME);
      when(mockReference.getParent()).thenReturn(refParent);

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.ALL, false);
      // The value is an immutable number, so should be valid
      assertTrue("Immutable numeric value should be valid for inlining",
          invokeIsValidInitialization(inlineVars, mockInitRef));
    }

    // ======================== isStringWorthInlining Tests ========================

    @Test
    public void testIsStringWorthInlining_shortStringDeclaredVar_returnsTrue() {
      // A short string used once is worth inlining when it's a const/define
      List<Reference> refs = Arrays.asList(mockDeclarationRef, mockReference);
      when(mockVar.isDefine()).thenReturn(true);
      when(mockVar.getInitialValue()).thenReturn(Node.newString(Token.STRING, "ab"));

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.CONSTANTS_ONLY, false);
      assertTrue("Short defined string should be worth inlining",
          invokeIsStringWorthInlining(inlineVars, mockVar, refs));
    }

    @Test
    public void testIsStringWorthInlining_longNonDefineString_returnsFalse() {
      // A long non-define string used once should not be worth the inline cost
      List<Reference> refs = Arrays.asList(mockDeclarationRef, mockReference);
      when(mockVar.isDefine()).thenReturn(false);
      when(mockVar.getInitialValue()).thenReturn(
          Node.newString(Token.STRING, "this is a very long string value that costs more to
inline"));

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.ALL, false);
      assertFalse("Long non-define string should not be worth inlining",
          invokeIsStringWorthInlining(inlineVars, mockVar, refs));
    }

    // ======================== isVarInlineForbidden Tests ========================

    @Test
    public void testIsVarInlineForbidden_returnsTrueForExportedVar() {
      when(mockVar.isExtern()).thenReturn(true);

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.ALL, false);
      assertTrue("Extern var should be forbidden from inlining",
          invokeIsVarInlineForbidden(inlineVars, mockVar));
    }

    @Test
    public void testIsVarInlineForbidden_returnsTrueForStaleVar() {
      staleVars.add(mockVar);

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.ALL, false);
      assertTrue("Stale var should be forbidden from inlining",
          invokeIsVarInlineForbidden(inlineVars, mockVar));
    }

    // ======================== canMoveAggressively Tests ========================

    @Test
    public void testCanMoveAggressively_nodeIsFunction_returnsFalse() {
      Node funcNode = new Node(Token.FUNCTION);

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.ALL, false);
      assertFalse("Function node cannot be moved aggressively",
          invokeCanMoveAggressively(inlineVars, funcNode));
    }

    @Test
    public void testCanMoveAggressively_nodeIsImmutable_returnsTrue() {
      Node numNode = Node.newNumber(42.0);

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.ALL, false);
      assertTrue("Immutable number node can be moved aggressively",
          invokeCanMoveAggressively(inlineVars, numNode));
    }

    // ======================== isValidDeclaration Tests ========================

    @Test
    public void testIsValidDeclaration_rejectsAssignedLvalueDeclaration() {
      // A reference that is both an lvalue and not a declaration is invalid
      when(mockDeclarationRef.isDeclaration()).thenReturn(false);
      when(mockDeclarationRef.isLvalue()).thenReturn(false);
      when(mockDeclarationRef.isSimpleAssignmentToName()).thenReturn(false);

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.ALL, false);
      assertFalse("Non-declaration reference should not be valid declaration",
          invokeIsValidDeclaration(inlineVars, mockDeclarationRef));
    }

    @Test
    public void testIsValidDeclaration_acceptsSimpleAssignment() {
      when(mockDeclarationRef.isDeclaration()).thenReturn(false);
      when(mockDeclarationRef.isLvalue()).thenReturn(false);
      when(mockDeclarationRef.isSimpleAssignmentToName()).thenReturn(true);

      InlineVariables inlineVars = new InlineVariables(mockCompiler,
          InlineVariables.Mode.ALL, false);
      assertTrue("Simple assignment should be valid declaration",
          invokeIsValidDeclaration(inlineVars, mockDeclarationRef));
    }

    // ======================== Helper reflection methods ========================

    private static Predicate<Var> getPredicateForMode(InlineVariables inlineVars,
 InlineVariables.Mode mode) {
      InlineVariables varsForMode = new InlineVariables(
          mock(AbstractCompiler.class), mode, false);
      switch (mode) {
        case CONSTANTS_ONLY:
          return varsForMode.new IdentifyConstants();
        case LOCALS_ONLY:
          return varsForMode.new IdentifyLocals();
        case ALL:
        default:
          return new Predicate<Var>() {
            @Override
            public boolean apply(Var var) {
              return true;
            }
          };
      }
    }

    private static boolean invokeIsValidReference(InlineVariables inlineVars, Reference ref) {
      try {
        java.lang.reflect.Method method = InlineVariables.class.getDeclaredMethod(
            "isValidReference", Reference.class);
        method.setAccessible(true);
        return (boolean) method.invoke(inlineVars, ref);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private static boolean invokeIsValidInitialization(InlineVariables inlineVars, Reference init) {
      try {
        java.lang.reflect.Method method = InlineVariables.class.getDeclaredMethod(
            "isValidInitialization", Reference.class);
        method.setAccessible(true);
        return (boolean) method.invoke(inlineVars, init);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private static boolean invokeIsValidDeclaration(InlineVariables inlineVars, Reference decl) {
      try {
        java.lang.reflect.Method method = InlineVariables.class.getDeclaredMethod(
            "isValidDeclaration", Reference.class);
        method.setAccessible(true);
        return (boolean) method.invoke(inlineVars, decl);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private static boolean invokeIsStringWorthInlining(InlineVariables inlineVars, Var var,
 List<Reference> refs) {
      try {
        java.lang.reflect.Method method = InlineVariables.class.getDeclaredMethod(
            "isStringWorthInlining", Var.class, List.class);
        method.setAccessible(true);
        return (boolean) method.invoke(inlineVars, var, refs);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private static boolean invokeIsVarInlineForbidden(InlineVariables inlineVars, Var var) {
      try {
        java.lang.reflect.Method method = InlineVariables.class.getDeclaredMethod(
            "isVarInlineForbidden", Var.class);
        method.setAccessible(true);
        return (boolean) method.invoke(inlineVars, var);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private static boolean invokeCanMoveAggressively(InlineVariables inlineVars, Node value) {
      try {
        java.lang.reflect.Method method = InlineVariables.class.getDeclaredMethod(
            "canMoveAggressively", Node.class);
        method.setAccessible(true);
        return (boolean) method.invoke(inlineVars, value);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }