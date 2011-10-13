package org.lilystudio.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 真操作布尔表达式节点, 向JVM语句栈内放入1
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class TrueCheck extends CheckExpression {

  /** 节点布尔表达式为真时的短路标签 */
  private Label trueLabel;

  @Override
  public String toString() {
    return "true";
  }

  public void setCheckLabel(Label trueLabel, Label falseLabel) {
    this.trueLabel = trueLabel;
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    if (trueLabel == null) {
      mw.visitInsn(ICONST_1);
    } else {
      mw.visitJumpInsn(GOTO, trueLabel);
    }
  }

  public void scan(Template template) {
  }
}