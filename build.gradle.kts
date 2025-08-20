plugins {
    alias(libs.plugins.android.application) apply false
    id("org.jetbrains.kotlin.android") version "2.2.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
}
