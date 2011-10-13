package org.lilystudio.smarty4j;

import static org.lilystudio.smarty4j.Operation.*;

import java.util.ArrayList;
import java.util.List;

import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.ListExtended;
import org.lilystudio.smarty4j.expression.MapExtended;
import org.lilystudio.smarty4j.expression.MixedStringExpression;
import org.lilystudio.smarty4j.expression.ModifierExtended;
import org.lilystudio.smarty4j.expression.NullExpression;
import org.lilystudio.smarty4j.expression.ObjectExpression;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.expression.VariableExpression;
import org.lilystudio.smarty4j.expression.check.FalseCheck;
import org.lilystudio.smarty4j.expression.check.TrueCheck;
import org.lilystudio.smarty4j.expression.number.ConstDouble;
import org.lilystudio.smarty4j.expression.number.ConstInteger;
import org.lilystudio.smarty4j.statement.IModifier;

/**
 * Smarty结构分析器。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class Analyzer {

  /** 普通变量 */
  private static final int NORMAL = 0;

  /** 表达式变量 */
  private static final int EXPRESSION = 1;

  /** 字符串变量 */
  private static final int STRING = 2;

  /**
   * 合并算术或者逻辑表达式。
   * 
   * @param words
   *          词法分析的结果
   * @param start
   *          算术表达式的开始位置
   * @param end
   *          算术表达式的结束位置
   * @param mode
   *          运算的模式，0表示浮点数方式，1表示整数方式，2表示对象方式
   * @return 算术表达式的描述节点
   * @throws ParseException
   *           如果算法或者逻辑表达式构成语法错误
   */
  public static IExpression mergeExpression(Object[] words, int start, int end,
      int mode) throws ParseException {
    // 逆波兰表达式算法, operation用于保存运算符,
    // expression用于保存变量节点
    Operation[] operations = new Operation[end - start];
    int lastOperation = -1;
    IExpression[] expressions = new IExpression[end - start];
    int expressionSize = 0;
    // 开始标志, 需要使用负号运算符
    boolean isFirst = true;
    main: for (int i = start; i < end;) {
      Object word = words[i];
      if (isFirst && word == C_SUB) {
        lastOperation++;
        operations[lastOperation] = MINUS;
        isFirst = false;
      } else {
        if (word.equals("null")) {
          expressions[expressionSize] = new NullExpression();
          expressionSize++;
          isFirst = false;
        } else if (word instanceof Integer) {
          expressions[expressionSize] = new ConstInteger((Integer) word);
          expressionSize++;
          isFirst = false;
        } else if (word instanceof IExpression) {
          expressions[expressionSize] = (IExpression) word;
          expressionSize++;
          isFirst = false;
        } else if (word instanceof Double) {
          expressions[expressionSize] = new ConstDouble((Double) word);
          expressionSize++;
          isFirst = false;
        } else if (word == C_L_GROUP) {
          lastOperation++;
          operations[lastOperation] = null;
          isFirst = true;
        } else if (word == C_R_GROUP) {
          while (true) {
            if (lastOperation < 0) {
              throw new ParseException("缺失左括号");
            }
            Operation op = operations[lastOperation];
            lastOperation--;
            if (op != null) {
              expressionSize -= op.process(expressions, expressionSize, mode);
            } else {
              break;
            }
          }
          isFirst = false;
        } else {
          for (Operation op : opers) {
            outer: for (Object[] name : op.names) {
              // 有一些操作符是由一组汉字组成的, 需要比较每一个
              int len = name.length;
              int index = i + len - 1;
              if (index < end) {
                for (; index >= i; index--) {
                  if (!name[index - i].equals(words[index])) {
                    continue outer;
                  }
                }
                // 如果栈内存在优先级较高的符号, 需要将数据弹出栈
                int priority = op.priority;
                for (; lastOperation >= 0; lastOperation--) {
                  Operation tmp = operations[lastOperation];
                  if ((tmp != null) && (priority <= tmp.priority)) {
                    expressionSize -= tmp.process(expressions, expressionSize,
                        mode);
                  } else {
                    break;
                  }
                }
                lastOperation++;
                operations[lastOperation] = op;
                i += len;
                continue main;
              }
            }
            isFirst = true;
          }
          throw new ParseException("不能识别的运算符");
        }
      }
      i++;
    }

    // 处理完表达式后, 将所有的数据弹出栈
    for (; lastOperation >= 0; lastOperation--) {
      expressionSize -= operations[lastOperation].process(expressions,
          expressionSize, mode);
    }

    if (expressionSize == 1) {
      return expressions[0];
    } else {
      throw new ParseException("表达式语法错误");
    }
  }

  /**
   * 合并变量调节器。
   * 
   * @param engine
   *          模板引擎对象
   * @param template
   *          模板对象
   * @param words
   *          词法分析的结果
   * @param index
   *          当前判断是否包含变量调节器的索引
   * @param wordSize
   *          词法分析结果的大小
   * @param expression
   *          需要合并变量调节器的对象表达式
   * @return 合并变量调节器后新的索引位置
   * @throws ParseException
   *           变量调节器语法错误
   */
  public static int mergeModifier(Engine engine, Template template,
      Object[] words, int index, int wordSize, ObjectExpression expression)
      throws ParseException {
    while (index < wordSize) {
      // 如果输出包含变量调节器, 初始化变量调节器节点
      if (Operation.C_B_OR == words[index]) {
        index++;
        if (index == wordSize) {
          throw new ParseException("变量调节器没有名称");
        } else {
          // 读取变量调节器的名称
          Object o = words[index];
          if (!(o instanceof String)) {
            throw new ParseException("变量调节器名称必须是字符串");
          } else {
            // 变量调节器名必须是字符串
            String name = (String) o;
            index++;
            List<IExpression> values = new ArrayList<IExpression>();
            outer: while ((index < wordSize)
                && Operation.C_COLON == words[index]) {
              index++;
              while (true) {
                if (index >= wordSize) {
                  break outer;
                }
                // 读取变量调节器的参数
                o = words[index];
                index++;
                // 检查参数分隔符的合法性
                if (Operation.C_COLON == o) {
                  values.add(null);
                  continue;
                }
                // 识别参数类型
                if (o instanceof IExpression) {
                  values.add((IExpression) o);
                } else if (o instanceof String) {
                  if ("true".equals(o)) {
                    values.add(new TrueCheck());
                  } else if ("false".equals(o)) {
                    values.add(new FalseCheck());
                  } else {
                    throw new ParseException("不能识别的保留字");
                  }
                } else if (o instanceof Integer) {
                  values.add(new ConstInteger(((Number) o).intValue()));
                } else if (o instanceof Double) {
                  values.add(new ConstDouble(((Number) o).doubleValue()));
                } else {
                  throw new ParseException("不能识别的参数");
                }
                break;
              }
            }
            boolean ransack;
            if (name.charAt(0) == '@') {
              name = name.substring(1);
              ransack = false;
            } else {
              ransack = true;
            }
            IModifier modifier = (IModifier) engine.createNode(name, false);
            modifier.init(template, ransack, values);
            expression.add(new ModifierExtended(modifier));
            continue;
          }
        }
      }
      break;
    }
    return index;
  }

  /**
   * 词法分析，其中返回的数值[0]表示左部文本的结束位置，[1]表示右部文本的开始位置，
   * 其它的位置表示词法提取的内容，如果返回null表示当前行内没有合法的smarty语句块。
   * 
   * @param line
   *          需要分析的行
   * @param leftDelimiter
   *          smarty左边界定界符
   * @param rightDelimiter
   *          smarty右边界定界符
   * @return 词法分析的结果
   */
  public static Object[] lexical(String line, String leftDelimiter,
      String rightDelimiter) throws ParseException {
    // 左边界符开始位置
    int leftStart = line.indexOf(leftDelimiter);
    if (leftStart < 0) {
      // 这一行中没有结束字符串
      return null;
    }
    // 当前的右边界符开始位置
    int rightStart = line.indexOf(rightDelimiter, leftStart);
    if (rightStart < 0) {
      return null;
    }
    // 已经分析出来的单词
    Object[] words = new Object[32];
    // 当前最后一个单词的位置
    int lastWord = 1;
    // 左,右分界符的长度
    int leftLength = leftDelimiter.length();
    int rightLength = rightDelimiter.length();
    // 最大的有效长度
    int len = line.length() - rightLength;
    // 表达式引用开始位置
    int expStart = 0;
    // 当前处理到的位置
    int start = leftStart + leftLength;
    lexical: while (true) {
      if (start > len) {
        return null;
      }
      if (start > rightStart) {
        // 右边界符是包含在特殊符号如引号,表达式符号中的,需要查找新的右边界符
        rightStart = line.indexOf(rightDelimiter, start);
        if (rightStart < 0) {
          return null;
        }
      }
      if (start == rightStart) {
        break;
      }
      char c = line.charAt(start);
      switch (c) {
      // 过滤空白字符
      case '\t':
      case '\r':
      case ' ':
        break;
      // 识别符号
      case '!':
      case '=':
      case '>':
      case '<':
      case '|':
      case '&':
        words = Utilities.setWord(words, ++lastWord, null);
        start = findOperation(line, start + 1, len, words, lastWord, c);
        continue;
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case '.':
        // 识别数字
        words = Utilities.setWord(words, ++lastWord, null);
        start = findNumber(line, start, len, words, lastWord);
        continue;
      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case 'a':
      case 'b':
      case 'c':
      case 'd':
      case 'e':
      case 'f':
      case 'g':
      case 'h':
      case 'i':
      case 'j':
      case 'k':
      case 'l':
      case 'm':
      case 'n':
      case 'o':
      case 'p':
      case 'q':
      case 'r':
      case 's':
      case 't':
      case 'u':
      case 'v':
      case 'w':
      case 'x':
      case 'y':
      case 'z':
      case '@':
        words = Utilities.setWord(words, ++lastWord, null);
        start = findIdentifier(line, start, len, words, lastWord);
        continue;
      case '$':
        // 识别变量表达式
        words = Utilities.setWord(words, ++lastWord, null);
        start = findVariable(line, start + 1, len, words, lastWord, NORMAL);
        continue;
      case '"':
      case '\'':
        words = Utilities.setWord(words, ++lastWord, null);
        start = findString(line, start + 1, len, words, lastWord, c);
        continue;
      case '`':
        words = Utilities.setWord(words, ++lastWord, null);
        start = findExpression(line, start + 1, len, words, lastWord, '`');
        continue;
      case '*':
        if (lastWord == 1) {
          // 设置成注释状态
          for (int i = rightStart - 1;; i--) {
            c = line.charAt(i);
            if (!Character.isWhitespace(c)) {
              words[2] = C_MUL;
              words[3] = line.substring(start + 1, rightStart);
              if (c == '*' && i != start) {
                words[4] = C_MUL;
              }
              lastWord = 4;
              break lexical;
            }
          }
        }
      default:
        words = Utilities.setWord(words, ++lastWord, getOperation(c));
      }
      start++;
    }
    // 处理结束, 已经识别一个独立的smarty区块
    if (expStart > 0) {
      // 如果还处于表达式引用解析状态
      return null;
    }
    words[0] = leftStart;
    words[1] = rightStart + rightLength;
    Object[] copy = new Object[++lastWord];
    System.arraycopy(words, 0, copy, 0, lastWord);
    return copy;
  }

  /**
   * 识别一个合法的操作符，将结果附加到词法分析结果中。
   * 
   * @param line
   *          需要处理的文本行
   * @param start
   *          文本行的开始位置
   * @param end
   *          文本行的结束位置
   * @param words
   *          词法分析结果
   * @param wordSize
   *          词法分析结果数量
   * @param op
   *          操作符第一个字符
   * @return 操作符的结束位置
   */
  private static int findOperation(String line, int start, int end,
      Object[] words, int wordSize, char op) {
    loop: while (start < end) {
      char c = line.charAt(start);
      switch (c) {
      case '=':
      case '|':
      case '&':
        op += c;
        break;
      default:
        break loop;
      }
      start++;
    }
    words[wordSize] = getOperation(op);
    return start;
  }

  /**
   * 识别一个合法的数字，如果将结果附加到词法分析结果中。
   * 
   * @param line
   *          需要处理的文本行
   * @param start
   *          文本行的开始位置
   * @param end
   *          文本行的结束位置
   * @param words
   *          词法分析结果
   * @param wordSize
   *          词法分析结果数量
   * @return 数字串的结束位置
   */
  private static int findNumber(String line, int start, int end,
      Object[] words, int wordSize) {
    // 是否为浮点数
    boolean isDouble = false;
    int pos;
    loop: for (pos = start + 1; pos < end; pos++) {
      switch (line.charAt(pos)) {
      case '.':
        if (isDouble) {
          break loop;
        } else {
          isDouble = true;
        }
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        continue;
      default:
        break loop;
      }
    }

    String word = line.substring(start, pos);
    words[wordSize] = isDouble ? (Object) new Double(word) : new Integer(word);
    return pos;
  }

  /**
   * 识别一个标识符。
   * 
   * @param line
   *          需要处理的文本行
   * @param start
   *          文本行的开始位置
   * @param end
   *          文本行的结束位置
   * @param words
   *          词法分析结果
   * @param wordSize
   *          词法分析结果数量
   * @return 标识符的结束位置
   */
  private static int findIdentifier(String line, int start, int end,
      Object[] words, int wordSize) {
    // 读取变量标识符
    int pos;
    for (pos = start + 1; pos < end; pos++) {
      if (!Character.isJavaIdentifierPart(line.charAt(pos))) {
        break;
      }
    }
    words[wordSize] = line.substring(start, pos);
    return pos;
  }

  /**
   * 识别一个字符串。
   * 
   * @param line
   *          需要处理的文本行
   * @param start
   *          文本行的开始位置
   * @param end
   *          文本行的结束位置
   * @param words
   *          词法分析结果
   * @param wordSize
   *          词法分析结果数量
   * @param c
   *          字符串边界符
   * @return 字符串的结束位置
   */
  private static int findString(String line, int start, int end,
      Object[] words, int wordSize, char c) {
    MixedStringExpression exp = null;
    StringBuilder s = new StringBuilder();
    quotation: while (start < end) {
      char d = line.charAt(start);
      switch (d) {
      case '$':
        if (!Character.isJavaIdentifierStart(line.charAt(start + 1))) {
          break;
        }
      case '`':
        if (exp == null) {
          exp = new MixedStringExpression();
        }
        exp.add(s.toString());
        s.setLength(0);
        if (d == '$') {
          start = findVariable(line, start + 1, end, words, wordSize, STRING);
        } else {
          start = findExpression(line, start + 1, end, words, wordSize, '`');
        }
        exp.add((IExpression) words[wordSize]);
        continue;
      case '\\': {
        // 启用字符转义, 转义回车,换行制表符以及转义字符
        start++;
        d = line.charAt(start);
        switch (d) {
        case 'n':
          d = '\n';
          break;
        case 'r':
          d = '\r';
          break;
        case 't':
          d = '\t';
          break;
        case '`':
        case '$':
        case '"':
        case '\'':
        case '\\':
        case '.':
        case '[':
        case ']':
          break;
        default:
          s.append('\\');
        }
        break;
      }
      default:
        // 寻找成对的字符串标识符
        if (d == c) {
          if (exp == null) {
            words[wordSize] = new StringExpression(s.toString());
          } else {
            exp.add(s.toString());
            words[wordSize] = exp;
          }
          break quotation;
        }
      }
      s.append(d);
      start++;
    }
    return start + 1;
  }

  /**
   * 识别一个变量。
   * 
   * @param line
   *          需要处理的文本行
   * @param start
   *          文本行的开始位置
   * @param end
   *          文本行的结束位置
   * @param words
   *          词法分析结果
   * @param wordSize
   *          词法分析结果数量
   * @param type
   *          变量类型
   * @return 变量的结束位置
   */
  private static int findVariable(String line, int start, int end,
      Object[] words, int wordSize, int type) {
    // 检查是否是一个合法的变量名称
    if (!Character.isJavaIdentifierStart(line.charAt(start))) {
      words[wordSize] = getOperation('$');
      return start;
    }
    start = findIdentifier(line, start, end, words, wordSize);
    VariableExpression variable = new VariableExpression(
        (String) words[wordSize]);
    loop: while (start < end) {
      char c = line.charAt(start);
      switch (c) {
      case '[': {
        words[wordSize] = null;
        int pos = findExpression(line, start + 1, end, words, wordSize, ']');
        Object o = words[wordSize];
        if (o == null) {
          break loop;
        }
        start = pos;
        variable.add(new ListExtended((IExpression) o));
        continue;
      }
      case '.': {
        if (type != STRING) {
          c = line.charAt(start + 1);
          IExpression exp;
          if (c == '`') {
            words[wordSize] = null;
            int pos = findExpression(line, start + 2, end, words, wordSize, '`');
            Object o = words[wordSize];
            if (o == null) {
              break loop;
            }
            start = pos;
            exp = (IExpression) words[wordSize];
          } else if (!Character.isJavaIdentifierStart(c)) {
            break loop;
          } else {
            start = findIdentifier(line, start + 1, end, words, wordSize);
            exp = new StringExpression((String) words[wordSize]);
          }
          variable.add(new MapExtended(exp));
          continue;
        }
      }
      default:
        break loop;
      }
    }
    words[wordSize] = variable;
    return start;
  }

  /**
   * 识别一个表达式。
   * 
   * @param line
   *          需要处理的文本行
   * @param start
   *          文本行的开始位置
   * @param end
   *          文本行的结束位置
   * @param words
   *          词法分析结果
   * @param wordSize
   *          词法分析结果数量
   * @param endChar
   *          表达式的结束字符
   * @return 表达式的结束位置
   */
  private static int findExpression(String line, int start, int end,
      Object[] words, int wordSize, char endChar) {
    int lastWord = -1;
    Object[] tmpWords = new Object[32];
    loop: while (start < end) {
      char c = line.charAt(start);
      if (c == endChar) {
        try {
          words[wordSize] = mergeExpression(tmpWords, 0, lastWord + 1, OBJECT);
          start++;
        } catch (Exception e) {
        }
        break loop;
      }
      switch (c) {
      // 过滤空白字符
      case '\t':
      case '\r':
      case ' ':
        break;
      // 识别符号
      case '!':
      case '=':
      case '>':
      case '<':
      case '|':
      case '&':
        tmpWords = Utilities.setWord(tmpWords, ++lastWord, null);
        start = findOperation(line, start + 1, end, tmpWords, lastWord, c);
        continue;
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case '.': {
        // 识别数字
        tmpWords = Utilities.setWord(tmpWords, ++lastWord, null);
        start = findNumber(line, start, end, tmpWords, lastWord);
        continue;
      }
      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case 'a':
      case 'b':
      case 'c':
      case 'd':
      case 'e':
      case 'f':
      case 'g':
      case 'h':
      case 'i':
      case 'j':
      case 'k':
      case 'l':
      case 'm':
      case 'n':
      case 'o':
      case 'p':
      case 'q':
      case 'r':
      case 's':
      case 't':
      case 'u':
      case 'v':
      case 'w':
      case 'x':
      case 'y':
      case 'z': {
        tmpWords = Utilities.setWord(tmpWords, ++lastWord, null);
        start = findIdentifier(line, start, end, tmpWords, lastWord);
        continue;
      }
      case '$': {
        // 识别变量表达式
        tmpWords = Utilities.setWord(tmpWords, ++lastWord, null);
        start = findVariable(line, start + 1, end, tmpWords, lastWord,
            EXPRESSION);
        continue;
      }
      default:
        tmpWords = Utilities.setWord(tmpWords, ++lastWord, getOperation(c));
      }
      start++;
    }
    return start;
  }
}
