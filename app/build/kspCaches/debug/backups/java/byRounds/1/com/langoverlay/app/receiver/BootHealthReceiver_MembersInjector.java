package com.langoverlay.app.receiver;

import com.langoverlay.app.data.SettingsRepository;
import com.langoverlay.app.di.ApplicationScope;
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
public final class BootHealthReceiver_MembersInjector implements MembersInjector<BootHealthReceiver> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<CoroutineScope> applicationScopeProvider;

  public BootHealthReceiver_MembersInjector(Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.applicationScopeProvider = applicationScopeProvider;
  }

  public static MembersInjector<BootHealthReceiver> create(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    return new BootHealthReceiver_MembersInjector(settingsRepositoryProvider, applicationScopeProvider);
  }

  @Override
  public void injectMembers(BootHealthReceiver instance) {
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
    injectApplicationScope(instance, applicationScopeProvider.get());
  }

  @InjectedFieldSignature("com.langoverlay.app.receiver.BootHealthReceiver.settingsRepository")
  public static void injectSettingsRepository(BootHealthReceiver instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }

  @InjectedFieldSignature("com.langoverlay.app.receiver.BootHealthReceiver.applicationScope")
  @ApplicationScope
  public static void injectApplicationScope(BootHealthReceiver instance,
      CoroutineScope applicationScope) {
    instance.applicationScope = applicationScope;
  }
}
