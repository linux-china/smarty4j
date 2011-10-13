package org.lilystudio.smarty4j.expression.check;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 布尔表达式转换节点, 将其它表达式转换成布尔表达式
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class TranslateCheck extends CheckExpression {

  /** 需要转换的表达式 */
  private IExpression exp;

  /**
   * 创建布尔表达式转换节点
   * 
   * @param exp
   *          需要转换的表达式
   */
  public TranslateCheck(IExpression exp) {
    this.exp = exp;
  }

  public void setCheckLabel(Label trueLabel, Label falseLabel) {
    exp.setCheckLabel(trueLabel, falseLabel);
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    exp.parseCheck(mw, local, variableNames);
  }

  public void scan(Template template) {
    exp.scan(template);
  }
}
