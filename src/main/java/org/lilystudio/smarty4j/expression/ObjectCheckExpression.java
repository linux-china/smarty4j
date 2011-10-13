package org.lilystudio.smarty4j.expression;

import org.lilystudio.smarty4j.Template;
import org.objectweb.asm.Label;

/**
 * 针对对象进行逻辑AND,OR运算, 实现短路输出
 * 
 * @version 0.1.4, 2009/03/01
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public abstract class ObjectCheckExpression extends ObjectExpression {

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
  public ObjectCheckExpression(IExpression exp1, IExpression exp2) {
    this.exp1 = exp1;
    this.exp2 = exp2;
  }

  public void setCheckLabel(Label trueLabel, Label falseLabel) {
    this.trueLabel = trueLabel;
    this.falseLabel = falseLabel;
  }

  public void scan(Template template) {
    super.scan(template);
    exp1.scan(template);
    exp2.scan(template);
  }
}
