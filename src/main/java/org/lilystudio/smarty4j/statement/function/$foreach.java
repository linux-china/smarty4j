package org.lilystudio.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.util.List;
import java.util.Map;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.statement.Block;
import org.lilystudio.smarty4j.statement.BlockStatement;
import org.lilystudio.smarty4j.statement.IBlockFunction;
import org.lilystudio.smarty4j.statement.ILoop;
import org.lilystudio.smarty4j.statement.IStatement;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 循环函数。
 * 
 * <pre>
 * from--待循环的数据集合，可以是数组，LIST/MAP或者单一对象
 * item--当前获得的单个数据，$item是from中的一个元素
 * key--如果是数组，LIST类的，$key就是当前的序号，如果是MAP，$key就是item对应的主键值
 * 
 * {assign var=&quot;animals&quot; value=&quot;Rat,Ox,Tiger,Hare,Dragon,Serpent,Horse,Sheep,Monkey,Rooster,Dog,Boar&quot; delimiter=&quot;,&quot;}
 * {foreach from=$animals item=&quot;item&quot; key=&quot;key&quot;}{$key}{$item}
 * {/foreach}
 * </pre>
 * 
 * <b>OUTPUT:</b>
 * 
 * <pre>
 * 0:Rat
 * 1:Ox
 * 2:Tiger
 * 3:Hare
 * 4:Dragon
 * 5:Serpent
 * 6:Horse
 * 7:Sheep
 * 8:Monkey
 * 9:Rooster
 * 10:Dog
 * 11:Boar
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $foreach extends Block implements ILoop {

  /** ASM名称 */
  public static final String NAME = $foreach.class.getName().replace('.', '/');

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.OBJECT, "from"),
      new ParameterCharacter(ParameterCharacter.STRING, "item"),
      new ParameterCharacter(ParameterCharacter.STRING, null, "key"),
      new ParameterCharacter(ParameterCharacter.STRING, null, "name") };

  /**
   * 获取一个循环体源对象包含的对象数组, 如果源对象是Map, 将取回关键字对应的数组,
   * 如果无法将源对象转换成等价的对象数组, 源对象将直接被返回
   * 
   * @param o
   *          需要循环的源对象
   * @return 源对象数组
   */
  public static Object[] getLooper(Object o) {
    if (o instanceof List) {
      return ((List<?>) o).toArray();
    } else if (o instanceof Object[]) {
      return (Object[]) o;
    } else if (o instanceof Map) {
      return ((Map<?, ?>) o).entrySet().toArray();
    } else {
      return new Object[] { o };
    }
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
      if (getParameter(2) != null) {
        mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
            "(Ljava/lang/String;Ljava/lang/Object;)V");
      }
      mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
          "(Ljava/lang/String;Ljava/lang/Object;)V");
    }
  }

  @Override
  public void addStatement(IStatement statement) throws ParseException {
    if (statement instanceof $foreachelse) {
      if (elseBlock != null) {
        throw new ParseException("不能重复定义foreachelse");
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
    Integer oldItemValue = null;
    Integer oldKeyValue = null;

    IExpression item = getParameter(1);
    IExpression key = getParameter(2);

    Label isnull = new Label();
    Label isnotmap = new Label();
    Label loopinit = new Label();
    Label setstart = new Label();
    Label setend = new Label();
    Label setlist = new Label();
    Label loopend = new Label();
    Label end = new Label();

    getParameter(0).parseObject(mw, local, variableNames);
    mw.visitVarInsn(ASTORE, local);
    mw.visitVarInsn(ALOAD, local);
    mw.visitJumpInsn(IFNULL, isnull);

    if (variableNames != null) {
      oldItemValue = variableNames.get(item.toString());
      variableNames.put(item.toString(), Integer.valueOf(local + 1));
      if (key != null) {
        oldKeyValue = variableNames.get(key.toString());
        variableNames.put(key.toString(), Integer.valueOf(local + 2));
      }
    } else {
      // 保存原始的循环变量名值
      mw.visitVarInsn(ALOAD, CONTEXT);
      item.parse(mw, local + 1, variableNames);
      mw.visitInsn(DUP2);
      mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
          "(Ljava/lang/String;)Ljava/lang/Object;");
      if (key != null) {
        mw.visitVarInsn(ALOAD, CONTEXT);
        key.parse(mw, local + 2, variableNames);
        mw.visitInsn(DUP2);
        mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "get",
            "(Ljava/lang/String;)Ljava/lang/Object;");
      }
    }

    mw.visitVarInsn(ALOAD, local);
    mw.visitMethodInsn(INVOKESTATIC, NAME, "getLooper",
        "(Ljava/lang/Object;)[Ljava/lang/Object;");
    mw.visitVarInsn(ALOAD, local);
    mw.visitTypeInsn(INSTANCEOF, "java/util/Map");
    mw.visitJumpInsn(IFEQ, isnotmap);
    mw.visitVarInsn(ALOAD, local);
    mw.visitTypeInsn(CHECKCAST, "java/util/Map");
    mw.visitJumpInsn(GOTO, loopinit);

    mw.visitLabel(isnotmap);
    mw.visitInsn(ACONST_NULL);

    mw.visitLabel(loopinit);
    mw.visitVarInsn(ASTORE, local);
    mw.visitInsn(DUP);
    mw.visitInsn(ARRAYLENGTH);
    mw.visitInsn(ICONST_0);
    mw.visitVarInsn(ISTORE, local + 3);

    mw.visitLabel(setstart);
    mw.visitInsn(DUP2);
    mw.visitVarInsn(ILOAD, local + 3);
    mw.visitJumpInsn(IF_ICMPEQ, loopend);

    if (getParameter(3) != null) {
      mw.visitVarInsn(ASTORE, local + 4);
      mw.visitInsn(DUP);
      mw.visitVarInsn(ALOAD, CONTEXT);
      mw.visitInsn(SWAP);
      getParameter(3).parse(mw, local + 5, variableNames);
      mw.visitVarInsn(ILOAD, local + 3);
      mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "setForeach",
          "(ILjava/lang/String;I)V");
      mw.visitVarInsn(ALOAD, local + 4);
    }

    if (variableNames == null) {
      mw.visitVarInsn(ALOAD, CONTEXT);
      mw.visitInsn(SWAP);
      item.parse(mw, local + 4, variableNames);
      mw.visitInsn(SWAP);
    }
    mw.visitVarInsn(ILOAD, local + 3);
    mw.visitInsn(AALOAD);
    mw.visitVarInsn(ALOAD, local);
    mw.visitJumpInsn(IFNULL, setlist);

    // 循环源集合是Map型时所执行的处理
    if (key != null) {
      mw.visitInsn(DUP);
      mw.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getKey",
          "()Ljava/lang/Object;");
      // 是否设置主键的值
      if (variableNames != null) {
        mw.visitVarInsn(ASTORE, local + 2);
      } else {
        mw.visitVarInsn(ALOAD, CONTEXT);
        mw.visitInsn(SWAP);
        key.parse(mw, local + 4, variableNames);
        mw.visitInsn(SWAP);
        mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
            "(Ljava/lang/String;Ljava/lang/Object;)V");
      }
    }
    mw.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getValue",
        "()Ljava/lang/Object;");
    mw.visitJumpInsn(GOTO, setend);

    // 循环源集合是List型时所执行的处理
    mw.visitLabel(setlist);
    if (key != null) {
      if (variableNames == null) {
        // 是否设置主键的值, 按数字编号
        mw.visitVarInsn(ALOAD, CONTEXT);
        key.parse(mw, local + 4, variableNames);
      }
      mw.visitVarInsn(ILOAD, local + 3);
      mw.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
          "(I)Ljava/lang/Integer;");
      if (variableNames != null) {
        mw.visitVarInsn(ASTORE, local + 2);
      } else {
        mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
            "(Ljava/lang/String;Ljava/lang/Object;)V");
      }
    }

    mw.visitLabel(setend);
    if (variableNames != null) {
      mw.visitVarInsn(ASTORE, local + 1);
    } else {
      mw.visitMethodInsn(INVOKEVIRTUAL, Context.NAME, "set",
          "(Ljava/lang/String;Ljava/lang/Object;)V");
    }
    super.parse(mw, local + 4, variableNames);
    mw.visitLabel(startLabel);
    mw.visitIincInsn(local + 3, 1);
    mw.visitJumpInsn(GOTO, setstart);

    mw.visitLabel(loopend);
    mw.visitInsn(POP);
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
      variableNames.put(item.toString(), oldItemValue);
      if (key != null) {
        variableNames.put(key.toString(), oldKeyValue);
      }
    }
  }
}