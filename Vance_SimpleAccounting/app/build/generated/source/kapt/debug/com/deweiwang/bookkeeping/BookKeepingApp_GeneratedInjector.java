package com.deweiwang.bookkeeping;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = BookKeepingApp.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface BookKeepingApp_GeneratedInjector {
  void injectBookKeepingApp(BookKeepingApp bookKeepingApp);
}
