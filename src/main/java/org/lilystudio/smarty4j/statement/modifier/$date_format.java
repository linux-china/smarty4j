package org.lilystudio.smarty4j.statement.modifier;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.ParseException;
import org.lilystudio.smarty4j.Template;
import org.lilystudio.smarty4j.expression.IExpression;
import org.lilystudio.smarty4j.expression.StringExpression;
import org.lilystudio.smarty4j.statement.Modifier;
import org.lilystudio.smarty4j.statement.ParameterCharacter;

/**
 * 日期格式转换，已经完成了date_format函数，功能比它强大。
 * 
 * <pre>
 * {$now|date_format:'%A, %B %e, %Y'}
 * </pre>
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class $date_format extends Modifier {

  /** 参数定义 */
  private static ParameterCharacter[] definitions = {
      new ParameterCharacter(ParameterCharacter.STRING, new StringExpression(
          "%b %e, %Y")),
      new ParameterCharacter(ParameterCharacter.STRING, null),
      new ParameterCharacter(ParameterCharacter.STRING, null) };

  /** 允许使用的地区信息列表 */
  private static Map<String, Locale> locales = new HashMap<String, Locale>();

  static {
    locales.put("CANADA", Locale.CANADA);
    locales.put("CANADA_FRENCH", Locale.CANADA_FRENCH);
    locales.put("CHINA", Locale.CHINA);
    locales.put("CHINESE", Locale.CHINESE);
    locales.put("ENGLISH", Locale.ENGLISH);
    locales.put("FRANCE", Locale.FRANCE);
    locales.put("FRENCH", Locale.FRENCH);
    locales.put("GERMAN", Locale.GERMAN);
    locales.put("GERMANY", Locale.GERMANY);
    locales.put("ITALIAN", Locale.ITALIAN);
    locales.put("ITALY", Locale.ITALY);
    locales.put("JAPAN", Locale.JAPAN);
    locales.put("JAPANESE", Locale.JAPANESE);
    locales.put("KOREA", Locale.KOREA);
    locales.put("KOREAN", Locale.KOREAN);
    locales.put("PRC", Locale.PRC);
    locales.put("SIMPLIFIED_CHINESE", Locale.SIMPLIFIED_CHINESE);
    locales.put("TAIWAN", Locale.TAIWAN);
    locales.put("TRADITIONAL_CHINESE", Locale.TRADITIONAL_CHINESE);
    locales.put("UK", Locale.UK);
    locales.put("US", Locale.US);
  }

  /** 当前变量调节器使用的地域对象 */
  private Locale locale;

  /**
   * 向字符串缓冲区中添加两个字符
   * 
   * @param buf
   *          字符串缓冲区
   * @param value
   *          需要添加的数值
   */
  private void appendTwoChar(StringBuilder buf, int value) {
    buf.append((char) (value / 10 + '0'));
    buf.append((char) (value % 10 + '0'));
  }

  @Override
  public void init(Template parent, boolean ransack, List<IExpression> values)
      throws ParseException {
    super.init(parent, ransack, values);
    if (getParameter(2) != null) {
      locale = locales.get(getParameter(2).toString());
      if (locale == null) {
        throw new ParseException("不支持的地区");
      }
    }
    locale = Locale.getDefault();
  }

  @Override
  public Object execute(Object obj, Context context, Object[] values) {
    // 设置日期对象
    Calendar calendar;
    if (obj instanceof Calendar) {
      calendar = (Calendar) obj;
    } else {
      calendar = Calendar.getInstance(locale);
      if (obj instanceof Date) {
        calendar.setTime((Date) obj);
      } else if (obj instanceof String) {
        if (obj.equals("now")) {
          calendar.setTime(new Date());
        }
      } else {
        return values[1];
      }
    }

    // 设置输出格式
    DateFormatSymbols symbols = new DateFormatSymbols(locale);
    String format = (String) values[0];
    StringBuilder buf = new StringBuilder(64);

    for (int i = 0, len = format.length(); i < len; i++) {
      char c = format.charAt(i);
      if (c == '%' && ++i < len) {
        switch (format.charAt(i)) {
        case 'a':
          buf.append(symbols.getShortWeekdays()[calendar
              .get(Calendar.DAY_OF_WEEK)]);
          continue;
        case 'A':
          buf.append(symbols.getWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)]);
          continue;
        case 'b':
        case 'h':
          buf.append(symbols.getShortMonths()[calendar.get(Calendar.MONTH)]);
          continue;
        case 'B':
          buf.append(symbols.getMonths()[calendar.get(Calendar.MONTH)]);
          continue;
        case 'c':
          buf.append(DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
              DateFormat.DEFAULT, locale).format(calendar.getTime()));
          continue;
        case 'C':
          appendTwoChar(buf, calendar.get(Calendar.YEAR) / 100);
          continue;
        case 'd':
          appendTwoChar(buf, calendar.get(Calendar.DAY_OF_MONTH));
          continue;
        case 'D':
          appendTwoChar(buf, calendar.get(Calendar.MONTH) + 1);
          buf.append('/');
          appendTwoChar(buf, calendar.get(Calendar.DAY_OF_MONTH));
          buf.append('/');
          appendTwoChar(buf, calendar.get(Calendar.YEAR) % 100);
          continue;
        case 'e':
          buf.append(calendar.get(Calendar.DAY_OF_MONTH));
          continue;
        case 'F':
          appendTwoChar(buf, calendar.get(Calendar.YEAR) % 100);
          buf.append('-');
          appendTwoChar(buf, calendar.get(Calendar.MONTH) + 1);
          buf.append('-');
          appendTwoChar(buf, calendar.get(Calendar.DAY_OF_MONTH));
          continue;
        case 'g':
          calendar.add(Calendar.DATE,
              4 - ((calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7 + 1));
          appendTwoChar(buf, calendar.get(Calendar.YEAR) % 100);
        case 'G':
          calendar.add(Calendar.DATE,
              4 - ((calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7 + 1));
          appendTwoChar(buf, calendar.get(Calendar.YEAR));
        case 'H':
          appendTwoChar(buf, calendar.get(Calendar.HOUR_OF_DAY));
          continue;
        case 'I':
          appendTwoChar(buf, calendar.get(Calendar.HOUR));
          continue;
        case 'j': {
          int day = calendar.get(Calendar.DAY_OF_YEAR);
          buf.append((char) ((day / 100) + '0'));
          appendTwoChar(buf, day % 100);
          continue;
        }
        case 'k':
          buf.append(calendar.get(Calendar.HOUR_OF_DAY));
          continue;
        case 'l':
          buf.append(calendar.get(Calendar.HOUR));
          continue;
        case 'm':
          appendTwoChar(buf, calendar.get(Calendar.MONTH) + 1);
          continue;
        case 'M':
          appendTwoChar(buf, calendar.get(Calendar.MINUTE));
          continue;
        case 'n':
          buf.append('\n');
          continue;
        case 'p':
          buf.append(symbols.getAmPmStrings()[calendar.get(Calendar.AM_PM)]);
          continue;
        case 'r':
          buf.append((calendar.get(Calendar.HOUR) + 11) % 12 + 1);
          continue;
        case 'R':
          buf.append((calendar.get(Calendar.HOUR_OF_DAY) + 23) % 24 + 1);
          continue;
        case 'S':
          appendTwoChar(buf, calendar.get(Calendar.SECOND));
          continue;
        case 't':
          buf.append('\t');
          continue;
        case 'T':
          appendTwoChar(buf, calendar.get(Calendar.HOUR_OF_DAY));
          buf.append(':');
          appendTwoChar(buf, calendar.get(Calendar.MINUTE));
          buf.append(':');
          appendTwoChar(buf, calendar.get(Calendar.SECOND));
          continue;
        case 'u':
          buf.append((calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7 + 1);
          continue;
        case 'U':
          buf.append(calendar.get(Calendar.WEEK_OF_YEAR));
          continue;
        case 'V': {
          calendar.add(Calendar.DATE,
              4 - ((calendar.get(Calendar.DAY_OF_WEEK) + 6) % 7 + 1));
          int day = calendar.get(Calendar.DAY_OF_YEAR) - 1;
          buf.append(1 + day / 7
              + (day % 7 + (7 - calendar.get(Calendar.DAY_OF_WEEK)) % 7) / 7);
          continue;
        }
        case 'w':
          buf.append(calendar.get(Calendar.DAY_OF_WEEK) - 1);
          continue;
        case 'W': {
          int day = calendar.get(Calendar.DAY_OF_YEAR) - 1;
          buf.append(1 + day / 7
              + (day % 7 + (7 - calendar.get(Calendar.DAY_OF_WEEK)) % 7) / 7);
          continue;
        }
        case 'x':
          buf.append(DateFormat.getDateInstance(DateFormat.LONG, locale)
              .format(calendar.getTime()));
          continue;
        case 'X':
          buf.append(DateFormat.getTimeInstance(DateFormat.LONG, locale)
              .format(calendar.getTime()));
          continue;
        case 'y':
          appendTwoChar(buf, calendar.get(Calendar.YEAR) % 100);
          continue;
        case 'Y':
          buf.append(calendar.get(Calendar.YEAR));
          continue;
        case 'z':
        case 'Z':
          buf.append(calendar.getTimeZone().getDisplayName(true,
              TimeZone.SHORT, locale));
          continue;
        case '%':
          buf.append('%');
          continue;
        }
      } else {
        buf.append(c);
      }
    }
    return buf.toString();
  }

  public ParameterCharacter[] getDefinitions() {
    return definitions;
  }
}