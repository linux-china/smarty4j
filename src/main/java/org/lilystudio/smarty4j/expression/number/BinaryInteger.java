package org.lilystudio.smarty4j.expression.number;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.MethodVisitor;

/**
 * 二元整数操作表达式, 向JVM语句栈内放入一个整数值表示两个对象的操作结果
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public abstract class BinaryInteger extends IntegerExpression {

  /** 表达式1 */
  private IntegerExpression exp1;

  /** 表达式2 */
  private IntegerExpression exp2;

  /**
   * 建立二元整数操作表达式节点
   * 
   * @param exp1
   *          表达式1
   * @param exp2
   *          表达式2
   */
  public BinaryInteger(IExpression exp1, IExpression exp2) {
    this.exp1 = exp1 instanceof IntegerExpression ? (IntegerExpression) exp1
        : new TranslateInteger(exp1);
    this.exp2 = exp2 instanceof IntegerExpression ? (IntegerExpression) exp2
        : new TranslateInteger(exp2);
  }

  /**
   * 对整数进行二元操作
   * 
   * @param mw
   *          ASM的方法操作对象
   */
  protected abstract void process(MethodVisitor mw);
  
  public void scan(Template template) {
    exp1.scan(template);
    exp2.scan(template);
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    exp1.parse(mw, local, variableNames);
    exp2.parse(mw, local, variableNames);
    process(mw);
  }
}