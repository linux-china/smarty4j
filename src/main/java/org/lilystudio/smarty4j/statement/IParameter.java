package org.lilystudio.smarty4j.statement;

import java.util.Map;

import org.lilystudio.smarty4j.INode;
import org.lilystudio.smarty4j.expression.IExpression;
import org.objectweb.asm.MethodVisitor;

/**
 * 包含参数定义的节点，主要是变量调节器节点与函数节点。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public interface IParameter extends INode {

  /**
   * 获取全部的参数特征定义。
   * 
   * @return 参数特征描述数组
   */
  ParameterCharacter[] getDefinitions();

  /**
   * 获取指定参数的表达式。
   * 
   * @param index
   *          参数的序号
   * @return 参数对应的表达式
   */
  IExpression getParameter(int index);

  /**
   * 设置全部的参数表达式。
   * 
   * @param expressions
   *          参数表达式列表
   */
  void setParameters(IExpression[] expressions);

  /**
   * 参数代码转换，根据节点的信息将参数转换成数组放入JVM语句栈中。
   * 
   * @param mw
   *          ASM方法访问对象
   * @param local
   *          ASM方法内部的语句栈局部变量起始位置
   * @param variableNames
   *          需要缓存的变量名集合
   */
  void parseAllParameters(MethodVisitor mw, int local,
      Map<String, Integer> variableNames);
}
