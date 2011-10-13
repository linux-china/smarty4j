package org.lilystudio.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 假操作布尔表达式节点, 向JVM语句栈内放入0
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class FalseCheck extends CheckExpression {

  /** 节点布尔表达式为假时的短路标签 */
  private Label falseLabel;

  @Override
  public String toString() {
    return "false";
  }

  public void setCheckLabel(Label trueLabel, Label falseLabel) {
    this.falseLabel = falseLabel;
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    if (falseLabel == null) {
      mw.visitInsn(ICONST_0);
    } else {
      mw.visitJumpInsn(GOTO, falseLabel);
    }
  }

  public void scan(Template template) {
  }
}