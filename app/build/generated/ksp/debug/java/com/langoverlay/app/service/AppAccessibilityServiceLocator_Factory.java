package com.langoverlay.app.service;

import com.langoverlay.app.data.SettingsRepository;
import com.langoverlay.core.state.LanguageStateManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineScope;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.langoverlay.app.di.ApplicationScope")
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
public final class AppAccessibilityServiceLocator_Factory implements Factory<AppAccessibilityServiceLocator> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<LanguageStateManager> languageStateManagerProvider;

  private final Provider<CoroutineScope> applicationScopeProvider;

  public AppAccessibilityServiceLocator_Factory(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<LanguageStateManager> languageStateManagerProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.languageStateManagerProvider = languageStateManagerProvider;
    this.applicationScopeProvider = applicationScopeProvider;
  }

  @Override
  public AppAccessibilityServiceLocator get() {
    return newInstance(settingsRepositoryProvider.get(), languageStateManagerProvider.get(), applicationScopeProvider.get());
  }

  public static AppAccessibilityServiceLocator_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<LanguageStateManager> languageStateManagerProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    return new AppAccessibilityServiceLocator_Factory(settingsRepositoryProvider, languageStateManagerProvider, applicationScopeProvider);
  }

  public static AppAccessibilityServiceLocator newInstance(SettingsRepository settingsRepository,
      LanguageStateManager languageStateManager, CoroutineScope applicationScope) {
    return new AppAccessibilityServiceLocator(settingsRepository, languageStateManager, applicationScope);
  }
}
