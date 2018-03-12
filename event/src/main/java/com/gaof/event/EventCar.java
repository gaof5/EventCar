package com.gaof.event;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 数据传递 注册、发送接收、解除注册
 */
public class EventCar {

    private static EventCar instance;
    private Map<Object,List<SubscriberMethod>> cacheMap;
    private Handler handler;
    private ExecutorService executorService;

    private EventCar(){
        cacheMap=new HashMap<>();
        handler=new Handler();
        executorService= Executors.newCachedThreadPool();
    }

    public static EventCar getDefault(){
        if(null== instance){
            synchronized (EventCar.class){
                instance =new EventCar();
            }
        }
        return instance;
    }

    /**
     * 接受数据的类注册
     * @param object 接受数据的activity
     */
    public void register(Object object){
        List<SubscriberMethod> list=cacheMap.get(object);
        if(list==null){
            List<SubscriberMethod> methods = findSubscriberMethods(object);
            cacheMap.put(object,methods);
        }
    }

    public void unregister(Object object){
        cacheMap.remove(object);
    }

    private List<SubscriberMethod> findSubscriberMethods(Object object) {
        List<SubscriberMethod> list=new CopyOnWriteArrayList<>();
        Class<?> clazz=object.getClass();
        Method[] methods = clazz.getMethods();
        //循环查找包括父类的方法
        while (clazz!=null){
            String className=clazz.getName();
            //父类如为系统类则停止
            if(className.startsWith("java.")||className.startsWith("javax.")||className.startsWith("android.")){
                break;
            }
            for (Method method:methods){
                Subscriber subscriber=method.getAnnotation(Subscriber.class);
                if(subscriber==null){
                    continue;
                }
                Class<?>[] parametersClazz = method.getParameterTypes();
                if(parametersClazz.length!=1){
                    throw new RuntimeException("eventBus must be one parameter");
                }
                Class<?> parameterClazz = parametersClazz[0];
                SubscriberMethod subscriberMethod=new SubscriberMethod(method,subscriber.value(),parameterClazz);
                list.add(subscriberMethod);
            }
            clazz=clazz.getSuperclass();
        }
        return list;
    }

    /**
     * 发送数据
     * @param object 发送的数据
     */
    public void post(final Object object){
        Class<?> parameterClazz = object.getClass();
        Set<Object> keyCache = cacheMap.keySet();
        Iterator<Object> it=keyCache.iterator();
        while (it.hasNext()){
            final Object activity=it.next();
            List<SubscriberMethod> list = cacheMap.get(activity);
            for (final SubscriberMethod subscriberMethod:list){
                if(subscriberMethod.getEventType().isAssignableFrom(parameterClazz)){
                    switch (subscriberMethod.getThreadMode()){
                        case PostThread:
                            invoke(activity,subscriberMethod,object);
                            break;
                        case MainThread:
                            if(Looper.myLooper()==Looper.getMainLooper()){
                                invoke(activity,subscriberMethod,object);
                            }else {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(activity,subscriberMethod,object);
                                    }
                                });
                            }
                            break;
                        case BackgroundThread:
                            if(Looper.myLooper()!=Looper.getMainLooper()){
                                invoke(activity,subscriberMethod,object);
                            }else {
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(activity,subscriberMethod,object);
                                    }
                                });
                            }
                            break;
                    }
                }
            }
        }
    }

    //传递参数到对应方法
    private void invoke(Object activity, SubscriberMethod subscriberMethod, Object object) {
        try {
            subscriberMethod.getMethod().invoke(activity,object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
