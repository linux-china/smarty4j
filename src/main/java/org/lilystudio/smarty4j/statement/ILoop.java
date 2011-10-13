package org.lilystudio.smarty4j.statement;

import java.util.Map;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * 循环区块函数节点，被用于支持break与continue等指令。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public interface ILoop extends IBlockFunction {

  /**
   * 获取循环开始位置。
   * 
   * @return 循环的开始位置标签
   */
  Label getStartLabel();

  /**
   * 获取循环结束位置。
   * 
   * @return 循环的结束位置标签
   */
  Label getEndLabel();
  
  /**
   * 恢复循环体的状态。
   * 
   * @param mw
   *          ASM方法访问对象
   * @param variableNames
   *          需要缓存的变量名集合
   */
  void restore(MethodVisitor mw, Map<String, Integer> variableNames);
}
