package com.google.javascript.jscomp;

import com.google.common.base.Supplier;
import com.google.javascript.jscomp.CompilerOptions.Reach;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.SourceFile;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Test;

public class FunctionToBlockMutatorAndRenameLabelsTest extends CompilerTestCase {
  private Supplier<String> labelSupplier;
  private boolean removeUnused;

  public FunctionToBlockMutatorAndRenameLabelsTest() {
    labelSupplier = new FixedLabelSupplier();
    removeUnused = false;
  }

  @Override
  protected CompilerPass getProcessor(AbstractCompiler compiler) {
    return new RenameLabels(compiler, labelSupplier, removeUnused);
  }

  @Test
  public void testReferencedLabelAndBreakAreRenamedTogether() {
    labelSupplier = new FixedLabelSupplier();
    removeUnused = false;

    test(
        "target:{break target;}",
        "L1:{break L1;}");
  }

  @Test
  public void testContinueReferenceIsRenamedWithItsLoopLabel() {
    labelSupplier = new FixedLabelSupplier();
    removeUnused = false;

    test(
        "loop:for(;;){continue loop;}",
        "L1:for(;;){continue L1;}");
  }

  @Test
  public void testNestedLabelsKeepReferencesToCorrectDeclaration() {
    labelSupplier = new FixedLabelSupplier();
    removeUnused = false;

    test(
        "outer:{inner:{if(a){break outer;}break inner;}}",
        "L1:{L2:{if(a){break L1;}break L2;}}");
  }

  @Test
  public void testLabelsInSeparateFunctionNamespacesCanReuseShortName() {
    labelSupplier = new FixedLabelSupplier();
    removeUnused = false;

    test(
        "first:{break first;}function f(){second:{break second;}}",
        "L1:{break L1;}function f(){L1:{break L1;}}");
  }

  @Test
  public void testUnusedLabelIsRemovedWhenConfiguredToRemoveUnusedLabels() {
    labelSupplier = new FixedLabelSupplier();
    removeUnused = true;

    test(
        "unused:{var x=1;}",
        "var x=1;");
  }

  @Test
  public void testInliningRenamesLabelThatWouldOtherwiseCollideWithCallerLabel() {
    com.google.javascript.jscomp.Compiler compiler =
        new com.google.javascript.jscomp.Compiler();
    CompilerOptions options = new CompilerOptions();
    String source =
        "function f(){label:{break label;}}"
            + "label:{f();}";

    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("input", source)),
        options);
    Node root = compiler.parseInputs();

    new InlineFunctions(
        compiler,
        compiler.getUniqueNameIdSupplier(),
        Reach.ALL,
        false,
        true,
        true,
        true,
        true).process(null, root);

    String output = compiler.toSource();
    assertTrue(
        "An inlined function label must be made distinct from a caller label: " + output,
        output.contains("JSCompiler_inline_label_"));
  }

  @Test
  public void testInliningInLoopInitializesPreviouslyUninitializedLocal() {
    com.google.javascript.jscomp.Compiler compiler =
        new com.google.javascript.jscomp.Compiler();
    CompilerOptions options = new CompilerOptions();
    String source =
        "function f(){var x;if(x){g();}}"
            + "for(;;){f();break;}";

    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("input", source)),
        options);
    Node root = compiler.parseInputs();

    new InlineFunctions(
        compiler,
        compiler.getUniqueNameIdSupplier(),
        Reach.ALL,
        false,
        true,
        true,
        true,
        true).process(null, root);

    String output = compiler.toSource();
    assertTrue(
        "Inlining a call in a loop must reinitialize an uninitialized local: " + output,
        output.contains("void 0"));
  }

  private static final class FixedLabelSupplier implements Supplier<String> {
    private int next;

    @Override
    public String get() {
      return "L" + (++next);
    }
  }
}