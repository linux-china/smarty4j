package org.lilystudio.smarty4j.expression.check;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.MethodVisitor;

/**
 * 布尔表达式节点, 向JVM语句栈内放入一个整数值, 1表示<tt>true</tt>, 0表示
 * <tt>false</tt>
 * 
 * @version 0.1.4, 2009/03/01
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public abstract class CheckExpression implements IExpression {

  public void parseCheck(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
  }

  public void parseInteger(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
  }

  public void parseDouble(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitInsn(I2D);
  }

  public void parseString(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "toString",
        "(Z)Ljava/lang/String;");
  }

  public void parseObject(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf",
        "(Z)Ljava/lang/Boolean;");
  }
}
