package org.lilystudio.smarty4j.expression.check;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.Label;

/**
 * 二元布尔表达式节点, 向JVM语句栈内放入整数值表示两个对象的逻辑操作结果
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public abstract class BinaryCheckExpression extends CheckExpression {

  /** ASM名称 */
  public static final String NAME = BinaryCheckExpression.class.getName().replace('.',
      '/');

  /** 节点布尔表达式为真时的短路标签 */
  protected Label trueLabel;

  /** 节点布尔表达式为假时的短路标签 */
  protected Label falseLabel;

  /** 表达式1 */
  protected IExpression exp1;

  /** 表达式2 */
  protected IExpression exp2;

  /**
   * 创建二元布尔表达式节点
   * 
   * @param exp1
   *          表达式1
   * @param exp2
   *          表达式2
   */
  public BinaryCheckExpression(IExpression exp1, IExpression exp2) {
    this.exp1 = exp1;
    this.exp2 = exp2;
  }

  public void setCheckLabel(Label trueLabel, Label falseLabel) {
    this.trueLabel = trueLabel;
    this.falseLabel = falseLabel;
  }
  
  public void scan(Template template) {
    exp1.scan(template);
    exp2.scan(template);
  }
}