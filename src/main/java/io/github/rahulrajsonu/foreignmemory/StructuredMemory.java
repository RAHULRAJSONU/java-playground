package io.github.rahulrajsonu.foreignmemory;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;

public class StructuredMemory {
    static final MemoryLayout CUSTOMER = MemoryLayout.structLayout(
            ValueLayout.JAVA_LONG.withName("id"),
            ValueLayout.JAVA_INT.withName("age"),
            MemoryLayout.paddingLayout(4),
            ValueLayout.JAVA_DOUBLE.withName("balance")
    );

    static void main(String[] args) {
        long idOffset = CUSTOMER.byteOffset(groupElement("id"));
        long ageOffset = CUSTOMER.byteOffset(groupElement("age"));
        long balanceOffset = CUSTOMER.byteOffset(groupElement("balance"));

        try(Arena arena = Arena.ofConfined()){
            MemorySegment record = arena.allocate(CUSTOMER);
            record.set(ValueLayout.JAVA_LONG, idOffset, 101L);
            record.set(ValueLayout.JAVA_INT, ageOffset, 33);
            record.set(ValueLayout.JAVA_DOUBLE, balanceOffset, 8500.50);

            System.out.println(record.get(ValueLayout.JAVA_LONG, idOffset));
            System.out.println(record.get(ValueLayout.JAVA_INT, ageOffset));
            System.out.println(record.get(ValueLayout.JAVA_DOUBLE, balanceOffset));
        }
    }
}
