// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
id( "org.sonarqube" ) version "7.3.1.8318"
}

sonar {
  propriétés {
    property( "sonar.projectKey" , "epechassieu_carnet-de-chant" )
    propriété( "sonar.organisation" , "epechassieu" )
  }
}
