package org.lilystudio.smarty4j.expression.number;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Utilities;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 整数表达式节点, 向JVM语句栈内放入一个整数值
 * 
 * @version 0.1.4, 2009/03/01
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public abstract class IntegerExpression implements INumberExpression {

  /** 节点布尔表达式为真时的短路标签 */
  private Label trueLabel;

  /** 节点布尔表达式为假时的短路标签 */
  private Label falseLabel;

  public void setCheckLabel(Label trueLabel, Label falseLabel) {
    this.trueLabel = trueLabel;
    this.falseLabel = falseLabel;
  }

  public void parseCheck(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    Utilities.visitSCInsn(mw, IFNE, trueLabel, falseLabel);
  }

  public void parseInteger(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
  }

  public void parseDouble(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitInsn(I2D);
  }

  public void parseString(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "toString",
        "(I)Ljava/lang/String;");
  }
  
  public void parseObject(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
        "(I)Ljava/lang/Integer;");
  }
}
