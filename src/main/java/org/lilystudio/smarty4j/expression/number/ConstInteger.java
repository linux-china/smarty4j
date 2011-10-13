package org.lilystudio.smarty4j.expression.number;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.Utilities;
import org.objectweb.asm.MethodVisitor;

/**
 * 整数常数表达式节点, 向JVM语句栈内放入一个整数常量值
 * 
 * @version 0.1.4, 2009/03/01
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class ConstInteger extends IntegerExpression {

  /** 常量值 */
  private int value = 0;

  /**
   * 创建整数常数表达式节点
   * 
   * @param value
   *          常量值
   */
  public ConstInteger(int value) {
    this.value = value;
  }

  /**
   * 设置成相反数
   */
  public void inverse() {
    value = -value;
  }

  public int getValue() {
    return value;
  }
  @Override
  public String toString() {
    return Integer.toString(value);
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    Utilities.visitILdcInsn(mw, value);
  }

  public void scan(Template template) {
  }
}