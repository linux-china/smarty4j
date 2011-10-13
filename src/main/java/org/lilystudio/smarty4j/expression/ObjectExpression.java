package org.lilystudio.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.objectweb.asm.MethodVisitor;

/**
 * 对象表达式节点, 向JVM语句栈内放入对象
 * 
 * @version 0.1.4, 2009/03/01
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public abstract class ObjectExpression implements IExpression {

  /** ASM名称 */
  public static final String NAME = ObjectExpression.class.getName().replace(
      '.', '/');

  /** 变量的扩展访问节点列表 */
  private List<IExtended> extendeds;

  /**
   * 对象转换成逻辑型数据, 对于NULL对象, 空字符串, 以及数值型的0,
   * 均对应false, 否则对应true
   * 
   * @param o
   *          源对象
   * @return 转换的结果, 0表示<tt>false</tt>, 1表示
   *         <tt>true</tt>
   */
  public static boolean toCheck(Object o) {
    if (o == null) {
      return false;
    } else if (o.equals("")) {
      return false;
    } else if (o instanceof Boolean) {
      return (Boolean) o;
    } else if (o instanceof Number) {
      return ((Number) o).doubleValue() != 0.0;
    }
    return true;
  }

  /**
   * 对象转换成整型数值, 对于数值类型的对象返回对应的整数值, 对于布尔对象用1/0表示
   * <tt>true</tt>/<tt>false</tt>, 其它对象将调用类型转换,
   * 如果无法转换对象的信息成为一个整数将返回0值
   * 
   * @param o
   *          需要转换的对象
   * @return 对象表示的整数
   */
  public static int toInteger(Object o) {
    if (o == null) {
      return 0;
    } else if (o instanceof Number) {
      return ((Number) o).intValue();
    } else if (o instanceof Boolean) {
      return ((Boolean) o) ? 1 : 0;
    }
    try {
      return Integer.parseInt(o.toString());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * 对象转换成整型数值, 对于数值类型的对象返回对应的整数值, 对于布尔对象用1/0表示
   * <tt>true</tt>/<tt>false</tt>, 其它对象将调用类型转换,
   * 如果无法转换对象的信息成为一个整数将返回NaN值
   * 
   * @param o
   *          需要转换的对象
   * @return 对象表示的浮点数
   */
  public static double toDouble(Object o) {
    if (o == null) {
      return Double.NaN;
    } else if (o instanceof Number) {
      return ((Number) o).doubleValue();
    }
    try {
      return Double.parseDouble(o.toString());
    } catch (NumberFormatException e) {
      return Double.NaN;
    }
  }

  public boolean isExtended() {
    return extendeds != null;
  }
  
  /**
   * 添加一个扩展引用节点, 变量可以要求进行列表或映射型的扩展,
   * 实现源数据容器中复杂的对象描述
   * 
   * @param extended
   *          变量访问扩展节点
   */
  public void add(IExtended extended) {
    if (extendeds == null) {
      extendeds = new ArrayList<IExtended>();
    }

    extendeds.add(extended);
  }

  public void scan(Template template) {
    if (extendeds != null) {
      for (IExtended extended : extendeds) {
        extended.scan(template);
      }
    }
  }

  public void parseCheck(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitMethodInsn(INVOKESTATIC, NAME, "toCheck", "(Ljava/lang/Object;)Z");
  }

  public void parseInteger(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw
        .visitMethodInsn(INVOKESTATIC, NAME, "toInteger",
            "(Ljava/lang/Object;)I");
  }

  public void parseDouble(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitMethodInsn(INVOKESTATIC, NAME, "toDouble", "(Ljava/lang/Object;)D");
  }

  public void parseString(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
    mw.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
        "(Ljava/lang/Object;)Ljava/lang/String;");
  }

  public void parseObject(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    parse(mw, local, variableNames);
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    parseSelf(mw, local, variableNames);
    // 展开扩展访问的操作代码
    if (extendeds != null) {
      for (IExtended extended : extendeds) {
        extended.parse(mw, local, variableNames);
      }
    }
  }
  
  public abstract void parseSelf(MethodVisitor mw, int local,
      Map<String, Integer> variableNames);
}
