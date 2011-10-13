package org.lilystudio.smarty4j.expression;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.objectweb.asm.MethodVisitor;

/**
 * 变量Map型变量扩展节点, 将对象当成映射型结构, 根据提供的名称来访问对应的值,
 * 如果对象为NULL返回NULL, 如果对象是<tt>java.util.Map</tt>
 * 返回指定关键字的对象, 否则将按JavaBean规范调用对应的getXXX方法,
 * 如果以上条件均不满足直接返回对象
 * 
 * @version 0.1.3, 2008/12/12
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public class MapExtended implements IExtended {

  /** ASM名称 */
  public static final String NAME = MapExtended.class.getName().replace('.',
      '/');

  /**
   * 从映射关系中获取指定的对象
   * 
   * @param map
   *          映射对象
   * @param key
   *          关键字
   * @return 关键字对应的对象
   */
  public static Object getValue(Object map, Object key) {
    if (map == null) {
      return null;
    } else if (map instanceof Map) {
      return ((Map<?, ?>) map).get(key);
    } else if (key == null) {
      return null;
    } else {
      StringBuilder s = new StringBuilder("get");
      s.append(key.toString());
      s.setCharAt(3, Character.toUpperCase(s.charAt(3)));
      try {
        return map.getClass().getMethod(s.toString()).invoke(map);
      } catch (Throwable e) {
        return null;
      }
    }
  }

  /** 关键字表达式 */
  private ObjectExpression key;

  /**
   * 建立Map型变量扩展节点
   * 
   * @param key
   *          关键字表达式
   */
  public MapExtended(IExpression key) {
    this.key = new TranslateString(key);
  }

  /**
   * 建立Map型变量扩展节点
   * 
   * @param key
   *          关键字表达式
   */
  public MapExtended(String key) {
    this(new StringExpression(key));
  }

  public void parse(MethodVisitor mw, int local, Map<String, Integer> variableNames) {
    key.parse(mw, local, variableNames);
    mw.visitMethodInsn(INVOKESTATIC, NAME, "getValue",
        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
  }

  public void scan(Template template) {
    key.scan(template);
  }
}