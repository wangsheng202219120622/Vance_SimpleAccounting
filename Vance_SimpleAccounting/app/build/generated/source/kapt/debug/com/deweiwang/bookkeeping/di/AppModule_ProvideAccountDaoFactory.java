// Generated by Dagger (https://dagger.dev).
package com.deweiwang.bookkeeping.di;

import com.deweiwang.bookkeeping.data.AccountDao;
import com.deweiwang.bookkeeping.data.AccountDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class AppModule_ProvideAccountDaoFactory implements Factory<AccountDao> {
  private final Provider<AccountDatabase> databaseProvider;

  public AppModule_ProvideAccountDaoFactory(Provider<AccountDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public AccountDao get() {
    return provideAccountDao(databaseProvider.get());
  }

  public static AppModule_ProvideAccountDaoFactory create(
      Provider<AccountDatabase> databaseProvider) {
    return new AppModule_ProvideAccountDaoFactory(databaseProvider);
  }

  public static AccountDao provideAccountDao(AccountDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAccountDao(database));
  }
}
