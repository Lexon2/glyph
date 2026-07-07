package com.langoverlay.app.di;

import com.langoverlay.app.data.SettingsRepository;
import com.langoverlay.core.state.LanguageStateManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideLanguageStateManagerFactory implements Factory<LanguageStateManager> {
  private final Provider<CoroutineScope> scopeProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public AppModule_ProvideLanguageStateManagerFactory(Provider<CoroutineScope> scopeProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.scopeProvider = scopeProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public LanguageStateManager get() {
    return provideLanguageStateManager(scopeProvider.get(), settingsRepositoryProvider.get());
  }

  public static AppModule_ProvideLanguageStateManagerFactory create(
      Provider<CoroutineScope> scopeProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new AppModule_ProvideLanguageStateManagerFactory(scopeProvider, settingsRepositoryProvider);
  }

  public static LanguageStateManager provideLanguageStateManager(CoroutineScope scope,
      SettingsRepository settingsRepository) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideLanguageStateManager(scope, settingsRepository));
  }
}
