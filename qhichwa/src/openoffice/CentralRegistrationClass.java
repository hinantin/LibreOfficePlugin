 package qhichwa.openoffice;
 
 import com.sun.star.lang.XSingleComponentFactory;
 import com.sun.star.registry.XRegistryKey;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.StringTokenizer;
 
 public class CentralRegistrationClass
 {
   public static XSingleComponentFactory __getComponentFactory(String sImplementationName)
   {
     String regClassesList = getRegistrationClasses();
     StringTokenizer t = new StringTokenizer(regClassesList, " ");
     while (t.hasMoreTokens())
     {
       String className = t.nextToken();
       if ((className != null) && (className.length() != 0)) {
         try
         {
           Class regClass = Class.forName(className);
           Method writeRegInfo = regClass.getDeclaredMethod("__getComponentFactory", new Class[] { String.class });
           Object result = writeRegInfo.invoke(regClass, new Object[] { sImplementationName });
           if (result != null) {
             return (XSingleComponentFactory)result;
           }
         }
         catch (ClassNotFoundException ex)
         {
           ex.printStackTrace();
         }
         catch (ClassCastException ex)
         {
           ex.printStackTrace();
         }
         catch (SecurityException ex)
         {
           ex.printStackTrace();
         }
         catch (NoSuchMethodException ex)
         {
           ex.printStackTrace();
         }
         catch (IllegalArgumentException ex)
         {
           ex.printStackTrace();
         }
         catch (InvocationTargetException ex)
         {
           ex.printStackTrace();
         }
         catch (IllegalAccessException ex)
         {
           ex.printStackTrace();
         }
       }
     }
     return null;
   }
   
   public static boolean __writeRegistryServiceInfo(XRegistryKey xRegistryKey)
   {
     boolean bResult = true;
     String regClassesList = getRegistrationClasses();
     StringTokenizer t = new StringTokenizer(regClassesList, " ");
     while (t.hasMoreTokens())
     {
       String className = t.nextToken();
       if ((className != null) && (className.length() != 0)) {
         try
         {
           Class regClass = Class.forName(className);
           Method writeRegInfo = regClass.getDeclaredMethod("__writeRegistryServiceInfo", new Class[] { XRegistryKey.class });
           Object result = writeRegInfo.invoke(regClass, new Object[] { xRegistryKey });
           bResult &= ((Boolean)result).booleanValue();
         }
         catch (ClassNotFoundException ex)
         {
           ex.printStackTrace();
         }
         catch (ClassCastException ex)
         {
           ex.printStackTrace();
         }
         catch (SecurityException ex)
         {
           ex.printStackTrace();
         }
         catch (NoSuchMethodException ex)
         {
           ex.printStackTrace();
         }
         catch (IllegalArgumentException ex)
         {
           ex.printStackTrace();
         }
         catch (InvocationTargetException ex)
         {
           ex.printStackTrace();
         }
         catch (IllegalAccessException ex)
         {
           ex.printStackTrace();
         }
       }
     }
     return bResult;
   }
   
   private static String getRegistrationClasses()
   {
     return "kukkuniiaat.openoffice.Main";
   }
 }
