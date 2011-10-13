package org.lilystudio.smarty4j.expression;

import java.util.Map;

import org.lilystudio.smarty4j.INode;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 表达式节点, 向JVM语句栈内放入一个数据
 * 
 * @version 0.1.4, 2009/03/01
 * @author 欧阳先伟
 * @since Smarty 0.1
 */
public interface IExpression extends INode {

  /**
   * 设置节点布尔表达式短路算法标签信息
   * 
   * @param trueLabel
   *          布尔表达式为真时的短路位置
   * @param falseLabel
   *          布尔表达式为假时的短路位置
   */
  void setCheckLabel(Label trueLabel, Label falseLabel);

  /**
   * 根据当前表达式向JVM语句栈内放入一个布尔值
   * 
   * @param mw
   *          ASM方法访问对象
   * @param local
   *          ASM方法内部的语句栈局部变量起始位置
   * @param variableNames
   *          需要缓存的变量名列表, 缓存的实际位置是LOCAL_START + i
   */
  void parseCheck(MethodVisitor mw, int local, Map<String, Integer> variableNames);

  /**
   * 根据当前表达式向JVM语句栈内放入一个整数
   * 
   * @param mw
   *          ASM方法访问对象
   * @param local
   *          ASM方法内部的语句栈局部变量起始位置
   * @param variableNames
   *          需要缓存的变量名列表, 缓存的实际位置是LOCAL_START + i
   */
  void parseInteger(MethodVisitor mw, int local, Map<String, Integer> variableNames);

  /**
   * 根据当前表达式向JVM语句栈内放入一个浮点数
   * 
   * @param mw
   *          ASM方法访问对象
   * @param local
   *          ASM方法内部的语句栈局部变量起始位置
   * @param variableNames
   *          需要缓存的变量名列表, 缓存的实际位置是LOCAL_START + i
   */
  void parseDouble(MethodVisitor mw, int local, Map<String, Integer> variableNames);

  /**
   * 根据当前表达式向JVM语句栈内放入一个字符串
   * 
   * @param mw
   *          ASM方法访问对象
   * @param local
   *          ASM方法内部的语句栈局部变量起始位置
   * @param variableNames
   *          需要缓存的变量名列表, 缓存的实际位置是LOCAL_START + i
   */
  void parseString(MethodVisitor mw, int local, Map<String, Integer> variableNames);

  /**
   * 根据当前表达式向JVM语句栈内放入一个对象, 如果是基本数据类型,
   * 将转义成最接近的包装对象
   * 
   * @param mw
   *          ASM方法访问对象
   * @param local
   *          ASM方法内部的语句栈局部变量起始位置
   * @param variableNames
   *          需要缓存的变量名列表, 缓存的实际位置是LOCAL_START + i
   */
  void parseObject(MethodVisitor mw, int local, Map<String, Integer> variableNames);
}