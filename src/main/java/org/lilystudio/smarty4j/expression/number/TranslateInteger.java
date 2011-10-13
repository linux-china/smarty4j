package org.lilystudio.smarty4j.expression.number;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.MethodVisitor;

/**
 * 整数表达式转换节点, 将表达式转换成整数表达式
 * 
 * @version 0.1.1, 2007/03/24
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class TranslateInteger extends IntegerExpression {

  /** 需要转换的表达式 */
  private IExpression exp;

  /**
   * 建立整数表达式转换节点
   * 
   * @param exp
   *          需要转换的表达式
   */
  public TranslateInteger(IExpression exp) {
    this.exp = exp;
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    exp.parseInteger(mw, local, variableNames);
  }

  public void scan(Template template) {
    exp.scan(template);
  }
}
