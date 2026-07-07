package com.langoverlay.app;

import com.langoverlay.app.data.SettingsRepository;
import com.langoverlay.app.di.ApplicationScope;
import com.langoverlay.app.service.AppAccessibilityServiceLocator;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineScope;

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
public final class LangOverlayApplication_MembersInjector implements MembersInjector<LangOverlayApplication> {
  private final Provider<AppAccessibilityServiceLocator> accessibilityServiceLocatorProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<CoroutineScope> applicationScopeProvider;

  public LangOverlayApplication_MembersInjector(
      Provider<AppAccessibilityServiceLocator> accessibilityServiceLocatorProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    this.accessibilityServiceLocatorProvider = accessibilityServiceLocatorProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.applicationScopeProvider = applicationScopeProvider;
  }

  public static MembersInjector<LangOverlayApplication> create(
      Provider<AppAccessibilityServiceLocator> accessibilityServiceLocatorProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    return new LangOverlayApplication_MembersInjector(accessibilityServiceLocatorProvider, settingsRepositoryProvider, applicationScopeProvider);
  }

  @Override
  public void injectMembers(LangOverlayApplication instance) {
    injectAccessibilityServiceLocator(instance, accessibilityServiceLocatorProvider.get());
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
    injectApplicationScope(instance, applicationScopeProvider.get());
  }

  @InjectedFieldSignature("com.langoverlay.app.LangOverlayApplication.accessibilityServiceLocator")
  public static void injectAccessibilityServiceLocator(LangOverlayApplication instance,
      AppAccessibilityServiceLocator accessibilityServiceLocator) {
    instance.accessibilityServiceLocator = accessibilityServiceLocator;
  }

  @InjectedFieldSignature("com.langoverlay.app.LangOverlayApplication.settingsRepository")
  public static void injectSettingsRepository(LangOverlayApplication instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }

  @InjectedFieldSignature("com.langoverlay.app.LangOverlayApplication.applicationScope")
  @ApplicationScope
  public static void injectApplicationScope(LangOverlayApplication instance,
      CoroutineScope applicationScope) {
    instance.applicationScope = applicationScope;
  }
}
