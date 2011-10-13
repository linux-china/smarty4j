package org.lilystudio.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.util.List;
import java.util.Map;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.check.TrueCheck;
import org.lilystudio.smarty4j.expression.number.ConstInteger;
import org.lilystudio.smarty4j.statement.Block;
import org.lilystudio.smarty4j.statement.BlockStatement;
import org.lilystudio.smarty4j.statement.IBlockFunction;
import org.lilystudio.smarty4j.statement.ILoop;
import org.lilystudio.smarty4j.statement.IStatement;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 高级循环函数。
 * 
 * <pre>
 * name--循环内的变量名称
 * loop--循环体
 * start--开始时的坐标
 * step--步长
 * max--循环最多执行的次数
 * show--是否显示
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $section extends Block implements ILoop {

  /** ASM名称 */
  public static final String NAME = $section.class.getName().replace('.', '/');

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.OBJECT, "loop"),
      new ParameterCharacter(ParameterCharacter.STRING, "name"),
      new ParameterCharacter(ParameterCharacter.INTOBJECT, new ConstInteger(0),
          "start"),
      new ParameterCharacter(ParameterCharacter.INTOBJECT, new ConstInteger(1),
          "step"),
      new ParameterCharacter(ParameterCharacter.INTOBJECT, new ConstInteger(0),
          "max"),
      new ParameterCharacter(ParameterCharacter.BOOLEAN, new TrueCheck(),
          "show") };

  /**
   * 获取一个循环体源对象包含的对象数组, 如果源对象是Map, 将取回关键字对应的数组,
   * 如果无法将源对象转换成等价的对象数组, 源对象将直接被返回
   * 
   * @param o
   *          需要循环的源对象
   * @param start
   *          数组开始位置
   * @param step
   *          循环步长
   * @param max
   *          最多的循环次数, 如果为0表示要循环所有的值
   * @return 源对象数组
   */
  public static Object[] getLooper(Object o, int start, int step, int max) {
    Object[] list;
    if (o instanceof List) {
      list = ((List<?>) o).toArray();
    } else if (o instanceof Object[]) {
      list = (Object[]) o;
    } else if (o instanceof Map) {
      list = ((Map<?, ?>) o).values().toArray();
    } else {
      list = new Object[] { o };
    }
    int size = list.length - 1;
    if (start > size) {
      start = size;
    }
    size = step > 0 ? (size - start) / step + 1 : start / -step + 1;
    if (max > 0 && max < size) {
      size = max;
    }
    Object[] result = new Object[size];
    for (int i = 0; i < size; i++) {
      result[i] = list[start];
      start += step;
    }
    return result;
  }

  /** 循环开始标签 */
  private Label startLabel = new Label();

  /** 循环结束标签 */
  private Label endLabel = new Label();

  /** 循环体为空时对应的区块 */
  private IBlockFunction elseBlock;

  public Label getStartLabel() {
    return startLabel;
  }

  public Label getEndLabel() {
    return endLabel;
  }

  public void restore(MethodVisitor mw, Map<String, Integer> variableNames) {
    mw.visitInsn(POP2);
    if (variableNames == null) {
      mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
          "(Ljava/lang/String;Ljava/lang/Object;)V");
    }
  }

  @Override
  public void addStatement(IStatement statement) throws ParseException {
    if (statement instanceof $sectionelse) {
      if (elseBlock != null) {
        throw new ParseException("不能重复定义sectionelse");
      } else {
        elseBlock = new BlockStatement();
        elseBlock.setParent(this.getParent());
      }
    } else if (elseBlock != null) {
      elseBlock.addStatement(statement);
    } else {
      super.addStatement(statement);
    }
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }

  @Override
  public void scan(Template template) {
    super.scan(template);
    if (elseBlock != null) {
      elseBlock.scan(template);
    }
  }

  @Override
  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    Integer oldNameValue = null;

    IExpression name = getParameter(1);

    Label isnull = new Label();
    Label loopstart = new Label();
    Label end = new Label();

    getParameter(5).parse(mw, local, variableNames);
    mw.visitJumpInsn(IFEQ, end);

    getParameter(0).parseObject(mw, local, variableNames);
    mw.visitVarInsn(ASTORE, local);
    mw.visitVarInsn(ALOAD, local);
    mw.visitJumpInsn(IFNULL, isnull);

    if (variableNames != null) {
      oldNameValue = variableNames.get(name.toString());
      variableNames.put(name.toString(), Integer.valueOf(local + 1));
    } else {
      // 保存原始的循环变量名值
      mw.visitVarInsn(ALOAD, CONTEXT);
      name.parse(mw, local + 1, variableNames);
      mw.visitInsn(DUP2);
      mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
          "(Ljava/lang/String;)Ljava/lang/Object;");
    }

    // 生成用于循环的数组
    mw.visitVarInsn(ALOAD, local);
    getParameter(2).parse(mw, local + 1, variableNames);
    getParameter(3).parse(mw, local + 1, variableNames);
    getParameter(4).parse(mw, local + 1, variableNames);
    mw.visitMethodInsn(INVOKESTATIC, NAME, "getLooper",
        "(Ljava/lang/Object;III)[Ljava/lang/Object;");

    mw.visitInsn(DUP);
    mw.visitInsn(ARRAYLENGTH);
    mw.visitVarInsn(ISTORE, local);
    mw.visitInsn(ICONST_0);

    mw.visitLabel(loopstart);
    mw.visitInsn(DUP);
    mw.visitVarInsn(ILOAD, local);
    mw.visitJumpInsn(IF_ICMPEQ, endLabel);

    mw.visitInsn(DUP2);
    mw.visitInsn(AALOAD);
    if (variableNames != null) {
      mw.visitVarInsn(ASTORE, local + 1);
    } else {
      mw.visitVarInsn(ALOAD, CONTEXT);
      mw.visitInsn(SWAP);
      name.parse(mw, local + 2, variableNames);
      mw.visitInsn(SWAP);
      mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
          "(Ljava/lang/String;Ljava/lang/Object;)V");
    }
    super.parse(mw, local + 2, variableNames);
    mw.visitLabel(startLabel);
    mw.visitInsn(ICONST_1);
    mw.visitInsn(IADD);
    mw.visitJumpInsn(GOTO, loopstart);

    // 循环结束, 恢复恢复过程中被设置的属性的原始值
    mw.visitLabel(endLabel);
    restore(mw, variableNames);
    mw.visitJumpInsn(GOTO, end);

    // 循环源集合为空时的处理
    mw.visitLabel(isnull);
    if (elseBlock != null) {
      elseBlock.parse(mw, local, variableNames);
    }

    mw.visitLabel(end);

    if (variableNames != null) {
      variableNames.put(name.toString(), oldNameValue);
    }
  }
}