package com.vitalforge.companion.data;

import com.vitalforge.companion.data.model.VitalReading;
import kotlinx.coroutines.flow.Flow;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0012\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006J\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/vitalforge/companion/data/VitalForgeRepository;", "", "dao", "Lcom/vitalforge/companion/data/VitalReadingDao;", "(Lcom/vitalforge/companion/data/VitalReadingDao;)V", "getAllReadings", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/vitalforge/companion/data/model/VitalReading;", "insertVitalReading", "", "reading", "(Lcom/vitalforge/companion/data/model/VitalReading;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "companion_debug"})
public final class VitalForgeRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.vitalforge.companion.data.VitalReadingDao dao = null;
    
    @javax.inject.Inject()
    public VitalForgeRepository(@org.jetbrains.annotations.NotNull()
    com.vitalforge.companion.data.VitalReadingDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.vitalforge.companion.data.model.VitalReading>> getAllReadings() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertVitalReading(@org.jetbrains.annotations.NotNull()
    com.vitalforge.companion.data.model.VitalReading reading, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}