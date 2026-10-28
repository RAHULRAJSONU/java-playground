package io.github.rahulrajsonu.foreignmemory;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class ColumnStore {
    record Customer(int age, double balance){}

    static void main() {
        int count = 10_000_000;
        try(Arena arena = Arena.ofConfined()){
            MemorySegment ages = arena.allocate((long) count * Integer.BYTES);
            MemorySegment balance = arena.allocate((long) count * Double.BYTES);

            // Populate the columns
            for(int i=0; i<count; i++){
                ages.setAtIndex(ValueLayout.JAVA_INT, i, 20 + (i % 50));
                balance.setAtIndex(ValueLayout.JAVA_DOUBLE, i, i * 1.25);
            }

            // Aggregate balance for customer over age 40
            double total = 0;
            for (int i =0; i < count; i++){
                int age = ages.getAtIndex(ValueLayout.JAVA_INT, i);
                if(age > 40){
                    total += balance.getAtIndex(ValueLayout.JAVA_DOUBLE, i);
                }
            }
            System.out.println("Total = "+total);
        }
    }
}
