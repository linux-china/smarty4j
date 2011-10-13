package org.lilystudio.smarty4j.statement;

import java.util.List;

import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;

/**
 * 变量调节器节点。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public interface IModifier extends IParameter {

  /**
   * 变量调节器初始化，读取并识别模板文件中变量调节器的参数。
   * 
   * @param template
   *          用于保存变量调节器对象的模板
   * @param ransack
   *          变量调节器需要递归处理设置为<tt>true</tt>
   * @param values
   *          模板引擎解析得到的当前变量调节器的参数值
   * @throws ParseException
   *           参数不合法
   * @see org.lilystudio.smarty4j.Template#addNode
   */
  void init(Template template, boolean ransack, List<IExpression> values)
      throws ParseException;
}
