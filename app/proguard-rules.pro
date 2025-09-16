# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- 메타데이터(제네릭·어노테이션·파라미터 이름·내부클래스) 보존 ---
-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses, MethodParameters

# --- Retrofit suspend 메서드 안전망 ---
# retrofit2.http 어노테이션이 붙은 메서드를 가진 인터페이스/클래스를 통째로 보존
-keepclasseswithmembers interface * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# --- Continuation 제네릭이 지워지지 않도록(보수적으로) ---
-keep class kotlin.coroutines.Continuation

# --- 너의 실제 서비스/DTO 패키지를 '정확히' 보존 ---
# 예시) 서비스 인터페이스
-keep interface com.aube.data.retrofit.** { *; }
# 예시) DTO
-keep class com.aube.data.model.response.** { *; }

# --- (필요 시) Repo/Mapper도 리플렉션 쓰면 보존 ---
# -keep class com.aube.data.repository.** { *; }
# -keep class com.aube.data.mapper.** { *; }

# --- Google Mobile Ads / UMP ---
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.internal.ads.** { *; }
-keep class com.google.android.ump.** { *; }
-dontwarn com.google.android.gms.**

# --- (선택) Retrofit/OkHttp 경고 억제 ---
-dontwarn retrofit2.**
-dontwarn okio.**