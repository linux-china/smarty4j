package org.lilystudio.smarty4j.statement;

import static org.objectweb.asm.Opcodes.*;

import java.util.HashMap;
import java.util.Map;

import org.lilystudio.smarty4j.INode;
import org.lilystudio.smarty4j.Operation;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.TemplateReader;
import org.lilystudio.smarty4j.Utilities;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.NullExpression;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.expression.check.FalseCheck;
import org.lilystudio.smarty4j.expression.check.TrueCheck;
import org.lilystudio.smarty4j.expression.number.ConstDouble;
import org.lilystudio.smarty4j.expression.number.ConstInteger;
import org.objectweb.asm.MethodVisitor;

/**
 * 基本函数节点，表示一个完整的操作。
 * 
 * @see org.lilystudio.smarty4j.statement.ParameterCharacter
 * @see org.lilystudio.smarty4j.statement.function.$else
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public abstract class Function extends Parameter implements IFunction {

  /** 函数名 */
  private String name;

  /** 父节点 */
  private IBlockFunction parent;

  /**
   * 函数参数的赋值处理，需要对函数参数进行第二次处理的，
   * 或者函数参数有一些特别的语法规则的，需要重载这个方法。
   * 
   * @param parameters
   *          函数的缺省参数信息
   * @param fields
   *          当前的参数列表
   * @throws ParseException
   *           参数错误将产生这个异常
   */
  public void process(ParameterCharacter[] definitions,
      Map<String, IExpression> fields) throws ParseException {
    if (definitions != null) {
      int len = definitions.length;
      IExpression[] parameters = new IExpression[len];
      for (int i = 0; i < len; i++) {
        ParameterCharacter definition = definitions[i];
        String name = (String) definition.getCustom();
        try {
          parameters[i] = definition.getExpression(fields.get(name));
        } catch (ParseException e) {
          throw new ParseException(name + e.getMessage());
        }
      }
      setParameters(parameters);
    }
  }

  public String getName() {
    return name;
  }

  public IBlockFunction getParent() {
    return parent;
  }

  public void init(Template template, String name) {
    this.name = name;
  }

  public void syntax(Template template, Object[] words, int wordSize)
      throws ParseException {
    ParameterCharacter[] parameters = getDefinitions();
    if (parameters != null) {
      Map<String, IExpression> fields = new HashMap<String, IExpression>();

      for (int index = 3; index + 2 < wordSize; index += 3) {
        Object name = words[index];
        if ((name instanceof String) && Operation.C_SET == words[index + 1]) {
          // 将函数值转换成指定的数据类型
          Object word = words[index + 2];
          IExpression value;
          if (word instanceof IExpression) {
            value = (IExpression) word;
          } else if (Operation.C_SUB == word) {
            word = words[index + 3];
            index++;
            if (word instanceof Integer) {
              value = new ConstInteger(-((Integer) word));
            } else if (word instanceof Double) {
              value = new ConstDouble(-((Double) word));
            } else {
              throw new ParseException("不能识别的函数参数值");
            }
          } else if (word instanceof Integer) {
            value = new ConstInteger((Integer) word);
          } else if (word instanceof Double) {
            value = new ConstDouble((Double) word);
          } else if ("true".equals(word) || "yes".equals(word)
              || "on".equals(word)) {
            value = new TrueCheck();
          } else if ("false".equals(word) || "no".equals(word)
              || "off".equals(word)) {
            value = new FalseCheck();
          } else if ("null".equals(word)) {
            value = new NullExpression();
          } else if (word instanceof String) {
            value = new StringExpression((String) word);
          } else {
            throw new ParseException("不能识别的函数参数值");
          }

          if (index + 3 < wordSize && words[index + 3] == Operation.C_B_OR) {

          }
          fields.put((String) name, value);
          continue;
        }
        throw new ParseException("函数参数语法错误");
      }
      process(parameters, fields);
    }
  }

  public boolean setParent(IBlockFunction parent) throws ParseException {
    ParentType parentType = (ParentType) getClass().getAnnotation(
        ParentType.class);

    if (parentType != null) {
      String name = parentType.name();
      if (!name.equals(parent.getName())) {
        throw new ParseException(getClass().getSimpleName().substring(1)
            + "只能位于" + name + "中");
      }
    }

    this.parent = parent;
    return true;
  }

  public void process(Template template, TemplateReader in, String left,
      String right) {
  }

  /**
   * 将函数对象放入语句栈中，提供给LineFunction与BlockFunction使用。
   * 
   * @see org.lilystudio.smarty4j.statement.LineFunction
   * @see org.lilystudio.smarty4j.statement.BlockFunction
   * 
   * @param mw
   *          ASM方法操作者
   * @param local
   *          ASM语句栈的局部变量起始位置
   * @param index
   *          函数在Template中保存的位置
   */
  protected void parseFunction(MethodVisitor mw, int local, int index) {
    mw.visitVarInsn(ALOAD, TEMPLATE);
    Utilities.visitILdcInsn(mw, index);
    mw.visitMethodInsn(INVOKEVIRTUAL, Template.NAME, "getNode", "(I)L"
        + INode.NAME + ";");
    mw.visitTypeInsn(CHECKCAST, this.getClass().getName().replace('.', '/'));
  }
}