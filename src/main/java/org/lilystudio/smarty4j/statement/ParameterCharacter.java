package org.lilystudio.smarty4j.statement;

import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.expression.TranslateString;
import org.lilystudio.smarty4j.expression.check.CheckExpression;
import org.lilystudio.smarty4j.expression.check.FalseCheck;
import org.lilystudio.smarty4j.expression.check.TranslateCheck;
import org.lilystudio.smarty4j.expression.check.TrueCheck;
import org.lilystudio.smarty4j.expression.number.ConstDouble;
import org.lilystudio.smarty4j.expression.number.ConstInteger;
import org.lilystudio.smarty4j.expression.number.DoubleExpression;
import org.lilystudio.smarty4j.expression.number.IntegerExpression;
import org.lilystudio.smarty4j.expression.number.TranslateDouble;
import org.lilystudio.smarty4j.expression.number.TranslateInteger;

/**
 * 函数参数特征信息描述类。
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class ParameterCharacter {

  /** 参数是逻辑常量类型 */
  public static final int BOOLEAN = 0;

  /** 参数是整数常量类型 */
  public static final int INTEGER = BOOLEAN + 1;

  /** 参数是浮点数常量类型 */
  public static final int DOUBLE = INTEGER + 1;

  /** 参数是字符串常量类型 */
  public static final int STRING = DOUBLE + 1;

  /** 参数是变量类型 */
  public static final int OBJECT = STRING + 1;

  /** 参数是逻辑变量类型 */
  public static final int BOOLOBJECT = OBJECT + 1;

  /** 参数是整数变量类型 */
  public static final int INTOBJECT = BOOLOBJECT + 1;

  /** 参数是浮点数变量类型 */
  public static final int DOUBLEOBJECT = INTOBJECT + 1;

  /** 参数是字符串变量类型 */
  public static final int STROBJECT = DOUBLEOBJECT + 1;

  /** 参数类型 */
  private int type;

  /** 是否为必需的参数 */
  private boolean required;

  /** 缺省值 */
  private IExpression value;

  /** 自定义属性 */
  private Object custom;

  /**
   * 创建函数参数信息对象，对于必填的参数使用，忽略自定义属性。
   * 
   * @param type
   *          参数类型
   */
  public ParameterCharacter(int type) {
    this(type, true, null, null);
  }

  /**
   * 创建函数参数信息对象，对于可省略的参数使用，忽略自定义属性。
   * 
   * @param type
   *          参数类型
   * @param defValue
   *          缺省值
   */
  public ParameterCharacter(int type, IExpression defValue) {
    this(type, false, defValue, null);
  }

  /**
   * 创建函数参数信息对象，对于必填的参数使用。
   * 
   * @param type
   *          参数类型
   * @param custom
   *          自定义属性
   */
  public ParameterCharacter(int type, Object custom) {
    this(type, true, null, custom);
  }

  /**
   * 创建函数参数信息对象，对于可省略的参数使用。
   * 
   * @param type
   *          参数类型
   * @param defValue
   *          缺省值
   * @param custom
   *          自定义属性
   */
  public ParameterCharacter(int type, IExpression defValue, Object custom) {
    this(type, false, defValue, custom);
  }

  /**
   * 创建函数参数信息对象。
   * 
   * @param type
   *          参数类型
   * @param isRequired
   *          参数是否为必需
   * @param defValue
   *          缺省值
   * @param custom
   *          自定义属性
   */
  private ParameterCharacter(int type, boolean isRequired,
      IExpression defValue, Object custom) {
    this.type = type;
    this.required = isRequired;
    this.value = defValue;
    this.custom = custom;
  }

  /**
   * 获取自定义属性。
   * 
   * @return 自定义属性对象
   */
  public Object getCustom() {
    return custom;
  }

  /**
   * 检测传入的参数表达式的合法性，并输出参数最终对应的表达式。
   * 
   * @param expression
   *          参数表达式
   * @return 参数表达式
   * @throws ParseException
   *           参数验证错误时产生异常
   */
  IExpression getExpression(IExpression expression) throws ParseException {
    if (expression == null) {
      if (required) {
        throw new ParseException("必须指定");
      } else {
        return value;
      }
    } else {
      switch (type) {
      case BOOLEAN:
        if (!(expression instanceof TrueCheck || expression instanceof FalseCheck)) {
          throw new ParseException("必须是true或false");
        }
        break;
      case INTEGER:
        if (!(expression instanceof ConstInteger)) {
          throw new ParseException("必须是整数");
        }
        break;
      case DOUBLE:
        if (!(expression instanceof ConstInteger)
            && !(expression instanceof ConstDouble)) {
          throw new ParseException("必须是浮点数");
        }
        break;
      case STRING:
        if (!(expression instanceof StringExpression)) {
          throw new ParseException("必须是字符串");
        }
        break;
      case OBJECT:
        break;
      case BOOLOBJECT:
        if (!(expression instanceof CheckExpression)) {
          expression = new TranslateCheck(expression);
        }
        break;
      case INTOBJECT:
        if (!(expression instanceof IntegerExpression)) {
          expression = new TranslateInteger(expression);
        }
        break;
      case DOUBLEOBJECT:
        if (!(expression instanceof DoubleExpression)) {
          expression = new TranslateDouble(expression);
        }
        break;
      case STROBJECT:
        if (!(expression instanceof StringExpression)) {
          expression = new TranslateString(expression);
        }
        break;
      }
      return expression;
    }
  }
}
