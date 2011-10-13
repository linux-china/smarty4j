package org.lilystudio.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.statement.Function;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.objectweb.asm.MethodVisitor;

/**
 * 变量赋值。
 * 
 * <pre>
 * var--变量名称，必需
 * value--变量的值，必需
 * delimiter--如果指定它的值，函数将把value的值字符串当成一个数组输入，使用它作为输入分隔符
 * 
 * {assign var=&quot;name&quot; value=&quot;Bob&quot;}
 * The value of $name is {$name}.
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * The value of $name is Bob.
 * 
 * {assign var=&quot;animals&quot; value=&quot;Rat,Ox,Tiger,Hare,Dragon,Serpent,Horse,Sheep,Monkey,Rooster,Dog,Boar&quot; delimiter=&quot;,&quot;}
 * 此时animals保存的是一个数组，对应十二生肖的名称。
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $assign extends Function {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STRING, "var"),
      new ParameterCharacter(ParameterCharacter.OBJECT, "value"),
      new ParameterCharacter(ParameterCharacter.STRING, null, "delimiter") };

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    // if (delimiter != null) {
    // value = value.toString().split(delimiter);
    // }
    // context.set(var, value);
    IExpression delimiter = getParameter(2);

    mw.visitVarInsn(ALOAD, CONTEXT);
    IExpression var = getParameter(0);
    var.parse(mw, local, variableNames);
    getParameter(1).parseObject(mw, local, variableNames);

    if (delimiter != null) {
      mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString",
          "()Ljava/lang/String;");
      delimiter.parse(mw, local, variableNames);
      mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "split",
          "(Ljava/lang/String;)[Ljava/lang/String;");
    }

    Integer index = variableNames == null ? null : variableNames.get(var
        .toString());
    if (index != null) {
      mw.visitInsn(DUP);
      mw.visitVarInsn(ASTORE, index);
    }

    mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
        "(Ljava/lang/String;Ljava/lang/Object;)V");
  }
}