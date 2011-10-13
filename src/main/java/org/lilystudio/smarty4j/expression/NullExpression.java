package org.lilystudio.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * NULL表达式节点, 向JVM语句栈内放入NULL
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class NullExpression extends ObjectExpression {

  /** 节点布尔表达式为假时的短路标签 */
  private Label falseLabel;

  @Override
  public void parseCheck(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    if (falseLabel == null) {
      mw.visitInsn(ICONST_0);
    } else {
      mw.visitJumpInsn(GOTO, falseLabel);
    }
  }

  @Override
  public void parseInteger(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    mw.visitInsn(ICONST_0);
  }

  @Override
  public void parseDouble(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    mw.visitInsn(DCONST_0);
  }

  @Override
  public void parseString(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    if (isExtended()) {
      super.parseString(mw, local, variableNames);
    } else {
      mw.visitInsn(ACONST_NULL);
    }
  }

  public void setCheckLabel(Label trueLabel, Label falseLabel) {
    this.falseLabel = falseLabel;
  }

  public void parseSelf(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    mw.visitInsn(ACONST_NULL);
  }
}
