package org.lilystudio.smarty4j;

import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.ObjectAndExpression;
import org.lilystudio.smarty4j.expression.ObjectOrExpression;
import org.lilystudio.smarty4j.expression.check.AEQCheck;
import org.lilystudio.smarty4j.expression.check.ANEQCheck;
import org.lilystudio.smarty4j.expression.check.AndCheck;
import org.lilystudio.smarty4j.expression.check.EQCheck;
import org.lilystudio.smarty4j.expression.check.GECheck;
import org.lilystudio.smarty4j.expression.check.GTCheck;
import org.lilystudio.smarty4j.expression.check.LECheck;
import org.lilystudio.smarty4j.expression.check.LTCheck;
import org.lilystudio.smarty4j.expression.check.NECheck;
import org.lilystudio.smarty4j.expression.check.NotCheck;
import org.lilystudio.smarty4j.expression.check.OrCheck;
import org.lilystudio.smarty4j.expression.number.AddDouble;
import org.lilystudio.smarty4j.expression.number.AddInteger;
import org.lilystudio.smarty4j.expression.number.ConstDouble;
import org.lilystudio.smarty4j.expression.number.ConstInteger;
import org.lilystudio.smarty4j.expression.number.DivDouble;
import org.lilystudio.smarty4j.expression.number.DivInteger;
import org.lilystudio.smarty4j.expression.number.ModDouble;
import org.lilystudio.smarty4j.expression.number.ModInteger;
import org.lilystudio.smarty4j.expression.number.MulDouble;
import org.lilystudio.smarty4j.expression.number.MulInteger;
import org.lilystudio.smarty4j.expression.number.SubDouble;
import org.lilystudio.smarty4j.expression.number.SubInteger;

/**
 * 表达式操作符类，用于描述和解析表达式，操作符分为1元和2元操作符，在解析过程中，
 * 根据表达式列表和自身的类型自动合并表达式生成新的包含操作符信息的表达式节点。
 * 
 * @see org.lilystudio.smarty4j.statement.function.$if
 * @see org.lilystudio.smarty4j.statement.function.$math
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class Operation {

  /** 结果返回浮点数 */
  public static final int FLOAT = 0;

  /** 结果返回整数 */
  public static final int INTEGER = 1;

  /** 结果尽量返回对象 */
  public static final int OBJECT = 2;

  /** 赋值符号 */
  public static final Character C_SET = new Character('=');

  /** 非操作符号 */
  public static final Character C_NOT = new Character('!');

  /** 等于符号 */
  public static final Character C_EQ = new Character((char) ('=' + '='));

  /** 不等于符号 */
  public static final Character C_NE = new Character((char) ('!' + '='));

  /** 全等于符号 */
  public static final Character C_AEQ = new Character((char) ('=' + '=' + '='));

  /** 全不等于符号 */
  public static final Character C_ANE = new Character((char) ('!' + '=' + '='));

  /** 加法符号 */
  public static final Character C_ADD = new Character('+');

  /** 减法符号 */
  public static final Character C_SUB = new Character('-');

  /** 乘法符号 */
  public static final Character C_MUL = new Character('*');

  /** 除法符号, 区块函数结束符号 */
  public static final Character C_DIV = new Character('/');

  /** 求余符号 */
  public static final Character C_MOD = new Character('+');

  /** MAP扩展符号 */
  public static final Character C_POINT = new Character('.');

  /** 左括号 */
  public static final Character C_L_GROUP = new Character('(');

  /** 右括号 */
  public static final Character C_R_GROUP = new Character(')');

  /** 下标左符号 */
  public static final Character C_L_ARRAY = new Character('[');

  /** 下标右符号 */
  public static final Character C_R_ARRAY = new Character(']');

  /** 变量调节器参数分隔符 */
  public static final Character C_COLON = new Character(':');

  /** 大于符号 */
  public static final Character C_GT = new Character('>');

  /** 大于等于符号 */
  public static final Character C_GE = new Character((char) ('>' + '='));

  /** 小于符号 */
  public static final Character C_LT = new Character('<');

  /** 小于等于符号 */
  public static final Character C_LE = new Character((char) ('<' + '='));

  /** 与符号 */
  public static final Character C_AND = new Character((char) ('&' + '&'));

  /** 或符号 */
  public static final Character C_OR = new Character((char) ('|' + '|'));

  /** 位与符号 */
  public static final Character C_B_AND = new Character('&');

  /** 位或符号，变量调节器开始符号 */
  public static final Character C_B_OR = new Character('|');

  /** 负号 */
  public static final Operation MINUS = new Operation(new Object[] { C_SUB },
      6, -1, 1);

  /** 非操作 */
  private static final int NOT = 0;

  /** 乘法操作 */
  private static final int MUL = NOT + 1;

  /** 除法操作 */
  private static final int DIV = MUL + 1;

  /** 求余操作 */
  private static final int MOD = DIV + 1;

  /** 加法操作 */
  private static final int ADD = MOD + 1;

  /** 减法操作 */
  private static final int SUB = ADD + 1;

  /** 大于操作 */
  private static final int GT = SUB + 1;

  /** 小于操作 */
  private static final int LT = GT + 1;

  /** 等于操作 */
  private static final int EQ = LT + 1;

  /** 不等于操作 */
  private static final int NEQ = EQ + 1;

  /** 判断是否完全等于 */
  private static final int AEQ = NEQ + 1;

  /** 判断是否完全不等于 */
  private static final int ANE = AEQ + 1;

  /** 大于等于操作 */
  private static final int GTE = ANE + 1;

  /** 小于等于操作 */
  private static final int LTE = GTE + 1;

  /** 与操作 */
  private static final int AND = LTE + 1;

  /** 或操作 */
  private static final int OR = AND + 1;

  /** 判断是否可以整除操作 */
  private static final int ISDIVBY = OR + 1;

  /** 判断整除的结果是否为偶数操作 */
  private static final int ISEVENBY = ISDIVBY + 1;

  /** 判断整除的结果是否为奇数操作 */
  private static final int ISODDBY = ISEVENBY + 1;

  /** 判断是否为偶数操作 */
  private static final int ISEVEN = ISODDBY + 1;

  /** 判断是否为奇数操作 */
  private static final int ISODD = ISEVEN + 1;

  static Operation[] opers = {
      new Operation(new Object[] { C_AND, "and" }, 2, AND),
      new Operation(new Object[] { C_OR, "or" }, 2, OR),
      new Operation(new Object[] { C_ADD }, 4, ADD),
      new Operation(new Object[] { C_SUB }, 4, SUB),
      new Operation(new Object[] { C_EQ, "eq" }, 3, EQ),
      new Operation(new Object[] { C_NE, "ne", "neq" }, 3, NEQ),
      new Operation(new Object[] { C_GT, "gt" }, 3, GT),
      new Operation(new Object[] { C_LT, "lt" }, 3, LT),
      new Operation(new Object[] { C_GE, "ge", "gte" }, 3, GTE),
      new Operation(new Object[] { C_LE, "le", "lte" }, 3, LTE),
      new Operation(new Object[] { C_NOT, "not" }, 6, NOT, 1),
      new Operation(new Object[] { C_AEQ }, 3, AEQ),
      new Operation(new Object[] { C_ANE }, 3, ANE),
      new Operation(new Object[] { C_MUL }, 5, MUL),
      new Operation(new Object[] { C_DIV, "div" }, 5, DIV),
      new Operation(new Object[] { C_MOD, "mod" }, 5, MOD),
      new Operation(new Object[] { "is div by" }, 0, ISDIVBY),
      new Operation(new Object[] { "is even by" }, 0, ISEVENBY),
      new Operation(new Object[] { "is odd by" }, 0, ISODDBY),
      new Operation(new Object[] { "is even", "is not odd" }, 0, ISEVEN, 1),
      new Operation(new Object[] { "is odd", "is not even" }, 0, ISODD, 1) };

  /** 操作符的名称数组 */
  Object[][] names;

  /** 操作符的优先级 */
  int priority;

  /** 操作符操作后需要的参数数量 */
  int param;

  /** 操作符的类型 */
  private int type;

  /**
   * 根据字符生成操作符对象。
   * 
   * @param c
   *          操作符字符串的描述字符
   * @return 操作符对象
   */
  public static Object getOperation(char c) {
    switch (c) {
    case '=':
      return C_SET;
    case '!':
      return C_NOT;
    case '+':
      return C_ADD;
    case '-':
      return C_SUB;
    case '*':
      return C_MUL;
    case '/':
      return C_DIV;
    case '%':
      return C_MOD;
    case '.':
      return C_POINT;
    case '(':
      return C_L_GROUP;
    case ')':
      return C_R_GROUP;
    case '[':
      return C_L_ARRAY;
    case ']':
      return C_R_ARRAY;
    case ':':
      return C_COLON;
    case '>':
      return C_GT;
    case '<':
      return C_LT;
    case '|':
      return C_B_OR;
    case '&':
      return C_B_AND;
    case '!' + '=':
      return C_NE;
    case '=' + '=':
      return C_EQ;
    case '>' + '=':
      return C_GE;
    case '<' + '=':
      return C_LE;
    case '|' + '|':
      return C_OR;
    case '&' + '&':
      return C_AND;
    case '!' + '=' + '=':
      return C_ANE;
    case '=' + '=' + '=':
      return C_AEQ;
    default:
      return Character.valueOf(c);
    }
  }

  /**
   * 建立操作符。
   * 
   * @param names
   *          操作符的名称字符串组
   * @param priority
   *          操作符的优先级
   * @param type
   *          操作符的类型
   */
  private Operation(Object[] names, int priority, int type) {
    this(names, priority, type, 2);
  }

  /**
   * 建立操作符。
   * 
   * @param names
   *          操作符的名称字符串组
   * @param priority
   *          操作符的优先级
   * @param type
   *          操作符的类型
   * @param param
   *          操作符的参数个数
   */
  private Operation(Object[] names, int priority, int type, int param) {
    int len = names.length;
    this.names = new Object[len][];
    for (int i = 0; i < len; i++) {
      Object o = names[i];
      this.names[i] = o instanceof String ? ((String) o).split(" ")
          : new Object[] { o };
    }
    this.priority = priority;
    this.type = type;
    this.param = param;
  }

  /**
   * 处理操作符，采用的是类似逆波兰式的算法实现。
   * 
   * @param expressions
   *          逆波兰式需要的栈内表达式列表
   * @param expressionSize
   *          逆波兰式需要的栈内表达式数量
   * @param mode
   *          运算的模式，0表示浮点数方式，1表示整数方式，2表示对象方式
   * @return 操作符合并的表达式数量
   * @throws ParseException
   *           如果参数不足
   */
  int process(IExpression[] expressions, int expressionSize, int mode)
      throws ParseException {
    int index = expressionSize - param;
    if (index < 0) {
      throw new ParseException("运算符参数不足");
    }
    IExpression exp = expressions[index];
    switch (type) {
    case -1:
      if (exp instanceof ConstInteger) {
        ((ConstInteger) exp).inverse();
      } else if (exp instanceof ConstDouble) {
        ((ConstDouble) exp).inverse();
      } else {
        expressions[index] = mode == INTEGER ? new SubInteger(new ConstInteger(
            0), exp) : new SubDouble(new ConstDouble(0.0), exp);
      }
      break;
    case NOT:
      expressions[index] = new NotCheck(exp);
      break;
    case MUL:
      expressions[index] = mode == INTEGER ? new MulInteger(exp,
          expressions[index + 1]) : new MulDouble(exp, expressions[index + 1]);
      break;
    case DIV:
      expressions[index] = mode == INTEGER ? new DivInteger(exp,
          expressions[index + 1]) : new DivDouble(exp, expressions[index + 1]);
      break;
    case MOD:
      expressions[index] = mode == INTEGER ? new ModInteger(exp,
          expressions[index + 1]) : new ModDouble(exp, expressions[index + 1]);
      break;
    case ADD:
      expressions[index] = mode == INTEGER ? new AddInteger(exp,
          expressions[index + 1]) : new AddDouble(exp, expressions[index + 1]);
      break;
    case SUB:
      expressions[index] = mode == INTEGER ? new SubInteger(exp,
          expressions[index + 1]) : new SubDouble(exp, expressions[index + 1]);
      break;
    case GT:
      expressions[index] = new GTCheck(exp, expressions[index + 1]);
      break;
    case LT:
      expressions[index] = new LTCheck(exp, expressions[index + 1]);
      break;
    case EQ:
      expressions[index] = new EQCheck(exp, expressions[index + 1]);
      break;
    case NEQ:
      expressions[index] = new NECheck(exp, expressions[index + 1]);
      break;
    case AEQ:
      expressions[index] = new AEQCheck(exp, expressions[index + 1]);
      break;
    case ANE:
      expressions[index] = new ANEQCheck(exp, expressions[index + 1]);
      break;
    case GTE:
      expressions[index] = new GECheck(exp, expressions[index + 1]);
      break;
    case LTE:
      expressions[index] = new LECheck(exp, expressions[index + 1]);
      break;
    case AND:
      expressions[index] = mode == OBJECT ? new ObjectAndExpression(exp,
          expressions[index + 1]) : new AndCheck(exp, expressions[index + 1]);
      break;
    case OR:
      expressions[index] = mode == OBJECT ? new ObjectOrExpression(exp,
          expressions[index + 1]) : new OrCheck(exp, expressions[index + 1]);
      break;
    case ISDIVBY:
      expressions[index] = new NotCheck(new ModInteger(exp,
          expressions[index + 1]));
      break;
    case ISEVENBY:
      expressions[index] = new NotCheck(new ModInteger(new DivInteger(exp,
          expressions[index + 1]), new ConstInteger(2)));
      break;
    case ISODDBY:
      expressions[index] = new ModInteger(new DivInteger(exp,
          expressions[index + 1]), new ConstInteger(2));
      break;
    case ISEVEN:
      expressions[index] = new NotCheck(
          new ModInteger(exp, new ConstInteger(2)));
      break;
    case ISODD:
      expressions[index] = new ModInteger(exp, new ConstInteger(2));
      break;
    }
    return param - 1;
  }
}
