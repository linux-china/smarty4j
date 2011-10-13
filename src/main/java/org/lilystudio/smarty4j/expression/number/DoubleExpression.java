package org.lilystudio.smarty4j.expression.number;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Utilities;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 浮点数表达式节点, 向JVM语句栈内放入一个浮点数值
 * 
 * @version 0.1.4, 2009/03/01
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public abstract class DoubleExpression implements INumberExpression {

  /** ASM名称 */
  public static final String NAME = DoubleExpression.class.getName().replace(
      '.', '/');

  /** 节点布尔表达式为真时的短路标签 */
  private Label trueLabel;

  /** 节点布尔表达式为假时的短路标签 */
  private Label falseLabel;

  /**
   * 浮点型转换成字符串, 如果浮点值等于一个整数, 将返回一个整型对象对应的字符串,
   * 即末尾不包含".0"
   * 
   * @param d
   *          浮点值
   * @return 转换的结果
   */
  public static Object toWeak(double d) {
    int i = (int) d;
    if (i == d) {
      return Integer.valueOf(i);
    } else {
      return new Double(d);
    }
  }
  
  public void setCheckLabel(Label trueLabel, Label falseLabel) {
    this.trueLabel = trueLabel;
    this.falseLabel = falseLabel;
  }

  public void parseCheck(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitInsn(DCONST_0);
    mw.visitInsn(DCMPL);
    Utilities.visitSCInsn(mw, IFNE, trueLabel, falseLabel);
  }

  public void parseInteger(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitInsn(D2I);
  }

  public void parseDouble(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
  }

  public void parseString(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitMethodInsn(INVOKESTATIC, NAME, "toWeak", "(D)Ljava/lang/Object;");
    mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
  }

  public void parseObject(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
  }
}
