# EventCar
根据EventBus原理自写数据传递框架


## 使用方法
接受发送对象要统一，且接受方法参数有且只有一个（例：User user）

1. 接受类注册，定义接受方法
```java
@Subscribe(threadMode = ThreadMode.MAIN)
public void onMessageEvent(User user) {/* Do something */};
```

Register and unregister your subscriber. For example on Android, activities and fragments should usually register according to their life cycle:

```java
 @Override<Br/>
 public void onCreate() {
     super.onCreate(savedInstanceState);
     EventCar.getDefault().register(this);
 }

 @Override
 public void onDestroy() {
     super.onDestroy();
     EventCar.getDefault().unregister(this);
 }
 ```

2. 发送数据
```java
EventBus.getDefault().post(new User());
 ```

## How to

To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

    gradle
    maven
    sbt
    leiningen

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


Step 2. Add the dependency

	dependencies {
	        compile 'com.github.gaof5:EventCar:v1.0'
	}
