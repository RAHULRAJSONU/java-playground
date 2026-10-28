package io.github.rahulrajsonu.foreignmemory;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

public class ForeignMemoryDemo {
    static void main() {
        int size = 10_000_000;

        // Create an arena and allocate native memory
        try(Arena arena = Arena.ofConfined()){
            long bytes = (long) size * Integer.BYTES;
            MemorySegment data = arena.allocate(bytes);

            // Write integers directly into native memory
            for(int i=0; i < size; i++){
                int value = i;
                data.setAtIndex(ValueLayout.JAVA_INT, i, value);
            }

            // Read the integers and calculate a sum

            long sum = 0;
            for(int i=0; i<size; i++){
                sum += data.getAtIndex(ValueLayout.JAVA_INT, i);
            }

            System.out.println("Sum = "+sum);
            System.out.println("Bytes = "+data.byteSize()+ ", MB = "+data.byteSize()/(1024*1024));
        } // Native memory is releases here
    }
}
