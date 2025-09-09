package com.vitalforge.companion.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.vitalforge.companion.data.model.VitalReading;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0016\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\t\u00a8\u0006\n"}, d2 = {"Lcom/vitalforge/companion/data/VitalReadingDao;", "", "getAll", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/vitalforge/companion/data/model/VitalReading;", "insert", "", "reading", "(Lcom/vitalforge/companion/data/model/VitalReading;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "companion_debug"})
@androidx.room.Dao()
public abstract interface VitalReadingDao {
    
    @androidx.room.Query(value = "SELECT * FROM vital_readings ORDER BY timestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.vitalforge.companion.data.model.VitalReading>> getAll();
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.vitalforge.companion.data.model.VitalReading reading, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}