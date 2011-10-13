package org.lilystudio.smarty4j.statement.function;

import java.util.Map;

import org.lilystudio.smarty4j.Analyzer;
import org.lilystudio.smarty4j.Operation;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.check.CheckExpression;
import org.lilystudio.smarty4j.expression.check.TranslateCheck;
import org.lilystudio.smarty4j.statement.Function;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.lilystudio.smarty4j.statement.ParentType;
import org.objectweb.asm.MethodVisitor;

/**
 * 参见if函数。
 * 
 * @see org.lilystudio.smarty4j.statement.function.$if
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
@ParentType(name = "if")
public class $elseif extends Function {

  /** 条件表达式 */
  private CheckExpression check;

  /**
   * 获取条件表达式
   * 
   * @return 条件表达式
   */
  public CheckExpression getCheckExpression() {
    return check;
  }

  @Override
  public void syntax(Template template, Object[] words, int wordSize)
      throws ParseException {
    check = new TranslateCheck(Analyzer.mergeExpression(words, 3, wordSize,
        Operation.FLOAT));
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
  }
}
