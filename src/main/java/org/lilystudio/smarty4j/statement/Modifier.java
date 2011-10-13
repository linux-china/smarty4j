package org.lilystudio.smarty4j.statement;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.INode;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.Utilities;
import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.MethodVisitor;

/**
 * 自定义变量调节器节点，它提供了一个高级的调用<br>
 * execute(Object, Object[])<br>
 * 方法，第一个参数是需要被调节的变量，第二个参数是变量调节器的参数组，
 * 如果不希望进行jvm字节码开发，开发人员应该继承自这个类来实现自己的变量调节器扩展节点。
 * 
 * @see org.lilystudio.smarty4j.Template#addModifier(IModifier)
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public abstract class Modifier extends Parameter implements IModifier {

  /** ASM名称 */
  public static final String NAME = Modifier.class.getName().replace(
      '.', '/');

  /** 是否需要递归处理，参见smarty中变量调节器的@规则 */
  private boolean ransack;

  /** 变量调节器在Template中的编号 */
  private int index;

  /**
   * 调用变量调节器对指定的对象进行调节处理。
   * 
   * @param obj
   *          需要处理的对象
   * @param context
   *          数据容器
   * @param values
   *          参数列表
   * @return 处理的结果对象
   */
  public Object call(Object obj, Context context, Object[] values) {
    if (ransack) {
      return recursion(obj, context, values);
    } else {
      return execute(obj, context, values);
    }
  }

  /**
   * 变量调节器对变量的处理。
   * 
   * @param obj
   *          需要处理的对象
   * @param context
   *          数据容器
   * @param values
   *          参数列表
   * @return 处理的结果
   */
  public abstract Object execute(Object obj, Context context, Object[] values);

  public void init(Template template, boolean ransack, List<IExpression> values)
      throws ParseException {
    ParameterCharacter[] definitions = getDefinitions();
    if (definitions != null) {
      int len = definitions.length;
      IExpression[] parameters = new IExpression[len];

      for (int i = 0; i < len; i++) {
        try {
          parameters[i] = definitions[i]
              .getExpression(i < values.size() ? values.get(i) : null);
        } catch (ParseException e) {
          throw new ParseException("第" + i + "个参数" + e.getMessage());
        }
      }

      setParameters(parameters);
    }

    this.index = template.addNode(this);
    this.ransack = ransack;
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    mw.visitVarInsn(ALOAD, TEMPLATE);
    Utilities.visitILdcInsn(mw, index);
    mw.visitMethodInsn(INVOKEVIRTUAL, Template.NAME, "getNode", "(I)L"
        + INode.NAME + ";");
    mw.visitTypeInsn(CHECKCAST, NAME);
    mw.visitInsn(SWAP);
    mw.visitVarInsn(ALOAD, INode.CONTEXT);
    parseAllParameters(mw, local, variableNames);
    mw.visitMethodInsn(INVOKEVIRTUAL, NAME, "call", "(Ljava/lang/Object;L"
        + Context.NAME + ";[Ljava/lang/Object;)Ljava/lang/Object;");
  }

  /**
   * 递归调用变量调节器对指定的对象进行调节处理。
   * 
   * @param obj
   *          需要处理的对象
   * @param context
   *          数据容器
   * @param values
   *          参数列表
   * @return 处理的结果对象
   */
  private Object recursion(Object obj, Context context, Object[] values) {
    if (obj instanceof Object[]) {
      Object[] objs = (Object[]) obj;
      Object[] result = new Object[objs.length];
      for (int i = objs.length - 1; i >= 0; i--) {
        result[i] = recursion(objs[i], context, values);
      }
      return result;
    } else if (obj instanceof Map) {
      Map<?, ?> map = (Map<?, ?>) obj;
      Map<Object, Object> result = new HashMap<Object, Object>();
      for (Entry<?, ?> entry : map.entrySet()) {
        result
            .put(entry.getKey(), recursion(entry.getValue(), context, values));
      }
      return result;
    } else if (obj instanceof List) {
      List<?> list = (List<?>) obj;
      List<Object> result = new ArrayList<Object>();
      for (Object item : list) {
        result.add(recursion(item, context, values));
      }
      return result;
    } else {
      return execute(obj, context, values);
    }
  }
}