-optimizationpasses 5 # 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-dontusemixedcaseclassnames # 混合时不使用大小写混合，混合后的类名为小写
-dontskipnonpubliclibraryclasses # 指定不去忽略非公共库的类

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型
-keepattributes Signature

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

-keep public class * extends android.app.Activity      # 保持Activity类不被混淆
-keep public class * extends android.app.Application   # 保持Application类不被混淆
-keep public class * extends android.app.Service       # 保持Service类不被混淆
-keep public class * extends android.content.BroadcastReceiver  # 保持BroadcastReceiver类不被混淆
-keep public class * extends android.preference.Preference      # 保持Preference类不被混淆
-keepnames class * implements java.io.Serializable #不混淆Serializable

-keep public class * extends android.content.ContentProvider     # 保持ContentProvider类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper   # 保持BackupAgentHelper类不被混淆
-keep public class com.android.vending.licensing.ILicensingService  # 保持ILicensingService类不被混淆

#-keepclassmembers class **.R$* { #不混淆资源类
#　　public static <fields>;
#}
-keepclasseswithmembernames class * {  # 保持 native 方法不被混淆
    native <methods>;
}
-keepclasseswithmembers class * {      # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {      # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {             # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {         # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}

-dontwarn android.support.**

-dontwarn com.google.**
-keep class com.google.** { *;}


#-----------------------以上常规混淆设置------------------------------------
#-----------------------以下第三方包不混淆----------------------------------
