import java.util.Properties;

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = Properties();
val localPropertiesFile = rootProject.file("local.properties");
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { input ->
        localProperties.load(input)
    };
}

android {
    namespace = "com.example.monechattest"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.monechattest"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"${localProperties.getProperty("KAKAO_NATIVE_APP_KEY")}\"");
        buildConfigField("String", "SERVER_ADDRESS", "\"${localProperties.getProperty("SERVER_ADDRESS")}\"");

        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = localProperties.getProperty("KAKAO_NATIVE_APP_KEY");
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    implementation ("com.github.AnyChart:AnyChart-Android:1.1.5")

    implementation ("com.kakao.sdk:v2-all:2.20.3") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation ("com.kakao.sdk:v2-user:2.20.3") // 카카오 로그인 API 모듈

    implementation ("io.socket:socket.io-client:2.0.1") // Socket.IO 클라이언트 라이브러리
    implementation ("com.squareup.okhttp3:okhttp:4.9.2") // okhttp 설치 모듈(http 연결 지원)
    implementation ("org.json:json:20201115") // JSON 추출 모듈

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
}