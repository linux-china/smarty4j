package org.lilystudio.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lilystudio.smarty4j.Analyzer;
import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Operation;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Function;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.objectweb.asm.MethodVisitor;

/**
 * 算术运算函数。
 * 
 * <pre>
 * equation--算术表达式
 * format--运算结果的输出格式
 * assign--结果输出的变量名称，如果省略直接输出至输出流中
 * 此外，与include类似，函数还能附加定义一些数据。
 * 
 * {math equation=&quot;(x+(x+y * y))*x&quot; x=&quot;3&quot; y=1}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * 21
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $math extends Function {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STRING, "equation"),
      new ParameterCharacter(ParameterCharacter.STRING, new StringExpression(
          "%d"), "format"),
      new ParameterCharacter(ParameterCharacter.STRING, null, "assign") };

  /** 数学表达式 */
  private IExpression exp;

  @Override
  public void process(ParameterCharacter[] parameters,
      Map<String, IExpression> fields) throws ParseException {
    super.process(parameters, fields);
    fields.remove("equation");
    fields.remove("format");
    fields.remove("assign");

    Object[] words = analyseMath(getParameter(0).toString(), fields);
    exp = Analyzer.mergeExpression(words, 0, words.length, Operation.FLOAT);
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }

  @Override
  public void scan(Template template) {
    super.scan(template);
    if (getParameter(2) != null) {
      template.preventCacheVariable(getParameter(2).toString());
    }
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    // if (assign != null) {
    // context.set(assign, exps[0]);
    // } else {
    // writer.write(exps[0].toString());
    // }
    if (getParameter(2) != null) {
      mw.visitVarInsn(ALOAD, CONTEXT);
      getParameter(2).parse(mw, local, variableNames);
      exp.parseObject(mw, local, variableNames);
      mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
          "(Ljava/lang/String;Ljava/lang/Object;)V");
    } else {
      mw.visitVarInsn(ALOAD, WRITER);
      exp.parseString(mw, local, variableNames);
      mw.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write",
          "(Ljava/lang/String;)V");
    }
  }
  
  /**
   * 词法分析
   * 
   * @param line
   *          需要分析的行
   * @param fields
   *          当前的参数列表
   * @return 词法分析的结果
   * @throws ParseException
   *           语法产生错误时将产生异常
   */
  private Object[] analyseMath(String line, Map<String, IExpression> fields)
      throws ParseException {
    List<Object> words = new ArrayList<Object>();
    int start = 0;
    int len = line.length();
    while (start < len) {
      char c = line.charAt(start);
      switch (c) {
      case ' ':
        start++;
        continue;
      case '+':
      case '-':
      case '*':
      case '%':
      case '/':
      case '(':
      case ')':
        words.add(Operation.getOperation(c));
        start++;
        continue;
      default:
        if (Character.isDigit(c)) {
          // 处理数值常量
          int end = start + 1;
          while (end < len) {
            char d = line.charAt(end);
            if (Character.isJavaIdentifierStart(d)) {
              throw new ParseException("数值常量格式错误");
            } else if (!Character.isDigit(d)) {
              break;
            }
            end++;
          }

          int index = words.size() - 1;
          if (index > 3 && words.get(index) == Operation.C_SUB
              && words.get(index - 1) == Operation.C_SET) {
            // 识别负数
            words
                .add(new Integer(-Integer.parseInt(line.substring(start, end))));
          } else {
            words.add(new Integer(line.substring(start, end)));
          }
          start = end;
          continue;
        } else if (Character.isLetter(c)) {
          // 识别变量名
          int end = start + 1;
          while (end < len) {
            if (!Character.isJavaIdentifierPart(line.charAt(end))) {
              break;
            }
            end++;
          }

          String name = line.substring(start, end);
          if (!fields.containsKey(name)) {
            throw new ParseException("变量[" + name + "]不存在");
          }
          words.add(fields.get(name));
          start = end;
          continue;
        }
      }
      throw new ParseException("语法错误");
    }
    return words.toArray();
  }
}