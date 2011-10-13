package org.lilystudio.util;

/**
 * 这个类用于动态的加载字节流, 并将它转换成类对象
 * 
 * @version 0.1.1, 2007/03/29
 * @author 欧阳先伟
 * @since Common 0.1
 */
public class DynamicClassLoader extends ClassLoader {

  /** 系统的类加载器 */
  private static ClassLoader systemLoader = DynamicClassLoader.class
      .getClassLoader();

  /**
   * 给定字节流生成类对象
   * 
   * @param code
   *          类的字节数据
   * @return 类对象
   */
  public static Class<?> getClass(byte[] code) {
    // HARDCODE
    return getClass("anonymous", code);
  }

  /**
   * 给定字节流生成类对象
   * 
   * @param name
   *          类的名称
   * @param code
   *          类的字节数据
   * @return 类对象
   */
  public synchronized static Class<?> getClass(String name, byte[] code) {
    DynamicClassLoader loader = new DynamicClassLoader();
    return loader.defineClass(name, code, 0, code.length);
  }

  /**
   * 建立动态类对象加载器
   */
  private DynamicClassLoader() {
    super(systemLoader);
  }
}