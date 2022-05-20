
import interfaces.AfterSuite;
import interfaces.BeforeSuite;
import interfaces.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;

public class SomethingTest {
    @BeforeSuite
    public void beforeSuite() {
        System.out.println("Before Suite");
    }

    @Test(priority = 3)
    public void test3() {
        System.out.println("Test 3");
    }

    @Test(priority = 1)
    public void test1() {
        System.out.println("Test 1");
    }

    @Test(priority = 2)
    public void test2A() {
        System.out.println("Test 2A");
    }

    @Test(priority = 2)
    public void test2B() {
        System.out.println("Test 2B");
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("After Suite");
    }

    public static class Tester {
        public static void start(Class clazz) throws InvocationTargetException, IllegalAccessException {
            Method beforeMethod = null;
            Method afterMethod = null;
            ArrayList<Method> testMethods = new ArrayList<>();

            Object obj = null;
            try {
                obj = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Test.class)) {
                    testMethods.add(m);
                } else if (m.isAnnotationPresent(BeforeSuite.class)) {
                    if (beforeMethod == null) {
                        beforeMethod = m;
                    } else {
                        throw new RuntimeException("ƒолжно быть не более одного метода с аннотацией @BeforeSuite");
                    }
                } if (m.isAnnotationPresent(AfterSuite.class)) {
                    if (afterMethod == null) {
                        afterMethod = m;
                    } else {
                        throw new RuntimeException("ƒолжно быть не более одного метода с аннотацией @AfterSuite");
                    }
                }
            }

            if (beforeMethod != null) {
                beforeMethod.invoke(obj);
            }

            testMethods.sort(Comparator.comparingInt(o -> o.getAnnotation(Test.class).priority()));
            for (Method m : testMethods) {
                m.invoke(obj);
            }

            if (afterMethod != null) {
                afterMethod.invoke(obj);
            }
        }
    }
}
