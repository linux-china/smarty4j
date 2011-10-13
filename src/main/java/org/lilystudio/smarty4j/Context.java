package org.lilystudio.smarty4j;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 模板数据容器，提供给当前正在操作的模板访问，保存了当前处理的全部数据与环境。
 * 数据容器可以有父容器，例如模板被另一个模板在解析过程中引用时，
 * 数据容器嵌套级别与模板调用嵌套级别一一对应，从而保证数据容器内数据生存域的隔离控制。
 * 在查询数据时，优先查找当前数据容器，在找不到的情况下将递归查询父容器。
 * 
 * @see org.lilystudio.smarty4j.Template#merge
 * 
 * @version 1.0.0, 2010/10/01
 * @author 欧阳先伟
 * @since Smarty 1.0
 */
public class Context {

  /** ASM名称 */
  public static final String NAME = Context.class.getName().replace('.', '/');

  /** 父容器的信息 */
  private Context parent;

  /** 当前容器对应的模板 */
  private Template template;

  /** 容器数据集 */
  private Map<String, Object> data = new HashMap<String, Object>();

  /** smarty环境数据集 */
  private Map<String, Object> env = new HashMap<String, Object>();

  /** 配置信息数据集合 */
  private Map<String, Object> configures;

  /** foreach数据集合 */
  private Map<String, Map<String, Object>> foreachs;

  /** Capture数据集合 */
  private Map<String, Object> captures;

  /** 当前这次调用的共用属性集合，所有的级联容器都共享一个对象，模板不能直接访问其中的数据 */
  private Map<String, Object> properties;

  /**
   * 建立容器类，设置容器基本环境信息。
   */
  public Context() {
    env.put("now", new Date());
    data.put("smarty", env);
  }

  /**
   * 建立容器类并设置父容器，在include,eval等函数里，
   * 将传递一些临时的参数进入子容器中。
   * 
   * @param parent
   *          父数据容器
   * @see org.lilystudio.smarty4j.statement.function.$eval
   * @see org.lilystudio.smarty4j.statement.function.$include
   */
  public Context(Context parent) {
    this();
    this.parent = parent;
    if (parent.captures != null) {
      getCaptures().putAll(parent.captures);
    }
    if (parent.configures != null) {
      getConfigures().putAll(parent.configures);
    }
    this.properties = parent.properties;
  }

  /**
   * 获取父容器
   * 
   * @return 父数据容器
   */
  public Context getParent() {
    return parent;
  }

  /**
   * 获取当前容器的关联模板对象, 如果容器没有指定模板, 将返回父容器的模板.
   * 
   * @return 容器关联的模板对象
   */
  public Template getTemplate() {
    return (template == null) && (parent != null) ? parent.getTemplate()
        : template;
  }

  /**
   * 获取数据，在当前容器查找不到指定的数据时，递归的调用父容器进行查找。
   * 
   * @param name
   *          数据名称
   * @return 数据对象
   */
  public Object get(String name) {
    Object value = data.get(name);
    return (value == null) && (parent != null) ? parent.get(name) : value;
  }

  /**
   * 设置单个数据，容器不允许设置名称为smarty的数据，
   * 如果用户设置一个名称为smarty的数据，实际上将不执行任何操作。
   * 
   * @param name
   *          数据名称
   * @param value
   *          数据对象
   */
  public void set(String name, Object value) {
    if (name.equals("smarty")) {
      return;
    }
    data.put(name, value);
  }

  /**
   * 批量向容器中设置数据，批量导入的源数据中如果包含名称为smarty的数据，
   * 这个数据将不会被设置进入容器。
   * 
   * @param map
   *          数据集合
   */
  public void putAll(Map<String, Object> map) {
    Object o = map.remove("smarty");
    data.putAll(map);
    map.put("smarty", o);
  }

  /**
   * 批量向容器中设置Bean数据。
   * 
   * @param o
   *          JavaBean对象
   */
  public void putBean(Object o) {
    try {
      for (PropertyDescriptor prop : Introspector.getBeanInfo(o.getClass())
          .getPropertyDescriptors()) {
        Method accessor = prop.getReadMethod();
        if (accessor != null) {
          String name = prop.getName();
          if (!"smarty".equals(name)) {
            try {
              data.put(name, accessor.invoke(o));
            } catch (Exception e) {
            }
          }
        }
      }
    } catch (IntrospectionException e) {
    }
  }

  /**
   * 获取配置信息数据集合。
   * 
   * @return 配置信息数据集合
   */
  public Map<String, Object> getConfigures() {
    if (configures == null) {
      configures = new HashMap<String, Object>();
      env.put("config", configures);
    }
    return configures;
  }

  /**
   * 设置foreach的内置信息。
   * 
   * @param total
   *          循环全部的数量
   * @param name
   *          循环的名称
   * @param index
   *          当前循环的位置序号
   */
  public void setForeach(int total, String name, int index) {
    if (foreachs == null) {
      foreachs = new HashMap<String, Map<String, Object>>();
      env.put("foreach", foreachs);
    }

    Map<String, Object> map = foreachs.get(name);
    if (map == null) {
      map = new HashMap<String, Object>();
      foreachs.put(name, map);
    }

    if (index == total - 1) {
      map.put("last", true);
    } else {
      map.put("last", false);
    }
    if (index == 0) {
      map.put("first", true);
    } else {
      map.put("first", false);
    }

    map.put("index", index);
    map.put("total", total);
  }

  /**
   * 获取Capture数据集合。
   * 
   * @return Capture数据集合
   */
  public Map<String, Object> getCaptures() {
    if (captures == null) {
      captures = new HashMap<String, Object>();
      env.put("capture", captures);
    }
    return captures;
  }

  /**
   * 获取容器共用属性集合。
   * 
   * @param name
   *          共用属性名称
   * @return 属性对象
   * @see org.lilystudio.smarty4j.statement.function.$counter
   * @see org.lilystudio.smarty4j.statement.function.$cycle
   */
  public Object getProperties(String name) {
    return properties != null ? properties.get(name) : null;
  }

  /**
   * 向容器共用属性集合中设置一个属性。
   * 
   * @param name
   *          共用属性名称
   * @param value
   *          属性值对象
   */
  public void setProperties(String name, Object value) {
    if (properties == null) {
      properties = new HashMap<String, Object>();
      Context context = this.parent;
      while (context != null) {
        context.properties = properties;
      }
    }
    properties.put(name, value);
  }

  /**
   * 设置当前容器的关联模板对象，同时初始化容器的配置信息。
   * 
   * @param template
   *          容器关联的模板对象
   */
  void setTemplate(Template template) {
    this.template = template;
    // 复制环境控制器公共容器信息
    Map<String, String> globalConfig = template.getEngine().getConfigures();
    if (globalConfig != null) {
      getConfigures().putAll(globalConfig);
    }
  }
}