package com.langoverlay.app.di;

import com.langoverlay.app.service.AppAccessibilityServiceLocator;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AppModule_ProvideAccessibilityServiceLocatorFactory implements Factory<AppAccessibilityServiceLocator> {
  private final Provider<AppAccessibilityServiceLocator> locatorProvider;

  public AppModule_ProvideAccessibilityServiceLocatorFactory(
      Provider<AppAccessibilityServiceLocator> locatorProvider) {
    this.locatorProvider = locatorProvider;
  }

  @Override
  public AppAccessibilityServiceLocator get() {
    return provideAccessibilityServiceLocator(locatorProvider.get());
  }

  public static AppModule_ProvideAccessibilityServiceLocatorFactory create(
      Provider<AppAccessibilityServiceLocator> locatorProvider) {
    return new AppModule_ProvideAccessibilityServiceLocatorFactory(locatorProvider);
  }

  public static AppAccessibilityServiceLocator provideAccessibilityServiceLocator(
      AppAccessibilityServiceLocator locator) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAccessibilityServiceLocator(locator));
  }
}
