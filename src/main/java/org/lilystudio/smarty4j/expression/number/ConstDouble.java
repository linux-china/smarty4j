package org.lilystudio.smarty4j.expression.number;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.objectweb.asm.MethodVisitor;

/**
 * 浮点数常数表达式节点, 向JVM语句栈内放入一个浮点数常量值
 * 
 * @version 0.1.4, 2009/03/01
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class ConstDouble extends DoubleExpression {

  /** 常量值 */
  private double value = 0;

  /**
   * 创建浮点数常数表达式节点
   * 
   * @param value
   *          常量值
   */
  public ConstDouble(double value) {
    this.value = value;
  }

  /**
   * 设置成相反数
   */
  public void inverse() {
    value = -value;
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    mw.visitLdcInsn(value);
  }

  public void scan(Template template) {
  }
}