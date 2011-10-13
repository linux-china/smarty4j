package org.lilystudio.smarty4j.statement.function;

import static org.objectweb.asm.Opcodes.*;

import java.util.Map;

import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.TemplateReader;
import org.lilystudio.smarty4j.Utilities;
import org.lilystudio.smarty4j.statement.Function;
import org.lilystudio.smarty4j.statement.ILoop;
import org.lilystudio.smarty4j.statement.ParameterCharacter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * continue函数，支持回到区块函数的开始位置，
 * 被支持的区块函数需要实现ILoop接口，函数支持一个整数做为参数，
 * 表示需要回到多少层外的区块函数首部，不设置时仅回滚一层。
 * 
 * <pre>
 * {break 2}
 * </pre>
 * 
 * @see org.lilystudio.smarty4j.statement.ILoop
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $continue extends Function {

  /** 需要跳转的层数 */
  private int floor;

  /** 需要跳转到的循环体结束位置 */
  private Label startLabel;

  /** 需要依次恢复状态的循环体, 不包括需要跳出的那一层, 数量比floor减一 */
  private ILoop[] blocks;

  @Override
  public void syntax(Template template, Object[] words, int wordSize)
      throws ParseException {
    if (wordSize == 3) {
      floor = 1;
      return;
    } else if (wordSize == 4) {
      Object value = words[3];
      if (value instanceof Integer) {
        floor = (Integer) value;
        if (floor > 0) {
          return;
        }
      }
    }
    throw new ParseException("参数错误");
  }

  @Override
  public void process(Template template, TemplateReader in, String left,
      String right) {
    blocks = new ILoop[floor - 1];
    ILoop now = (ILoop) Utilities.find(getParent(), ILoop.class);
    int i = 1;
    while (true) {
      if (now == null) {
        in.addMessage("没有需要跳转的位置");
        return;
      }
      if (i == floor) {
        break;
      }
      blocks[i - 1] = now;
      now = (ILoop) Utilities.find(now.getParent(), ILoop.class);
      i++;
    }
    startLabel = ((ILoop) now).getStartLabel();
  }

  public ParameterCharacter[] getDefinitions() {
    return null;
  }

  public void parse(MethodVisitor mw, int local,
      Map<String, Integer> variableNames) {
    for (ILoop block : blocks) {
      block.restore(mw, variableNames);
    }
    mw.visitJumpInsn(GOTO, startLabel);
  }
}