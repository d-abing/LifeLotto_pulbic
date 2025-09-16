-keepattributes Signature, *Annotation*, EnclosingMethod, InnerClasses, MethodParameters

# DTO 실제 경로로 맞추기
-keep class com.aube.data.model.response.** { *; }
-keepclassmembers class com.aube.data.model.response.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# (선택) 서비스 인터페이스도 배포 측에서 강제 보존하고 싶다면
-keepclasseswithmembers interface com.aube.data.retrofit.** {
    @retrofit2.http.* <methods>;
}