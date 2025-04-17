package io.github.rahulrajsonu.stream.collector;

import java.util.List;
import java.util.stream.Collectors;

public class CollectorsTeeing {
    enum Color {RED,BLUE,WHITE,YELLOW}
    enum Engine {ELECTRIC, HYBRID, GAS}
    enum Drive { WD2, WD4}
    interface Vehicle{}
    record Car(Color color, Engine engine, Drive drive, int passengers) implements Vehicle{}
    record Truck(Engine engine, Drive drive, int weight) implements Vehicle{}

    public static void main(String[] args) {
        List<Vehicle> vehicles = List.of(
                new Car(Color.BLUE, Engine.ELECTRIC, Drive.WD2, 5),
                new Car(Color.YELLOW, Engine.ELECTRIC, Drive.WD4, 4),
                new Car(Color.WHITE, Engine.HYBRID, Drive.WD4, 7),
                new Truck(Engine.GAS, Drive.WD4, 12_000),
                new Truck(Engine.ELECTRIC, Drive.WD2, 8_000)
        );

        List<Vehicle> electricalVehicle = vehicles.stream()
                .collect(
                        Collectors.teeing(
                                Collectors.filtering(
                                        vehicle -> vehicle instanceof Car car && car.engine() == Engine.ELECTRIC,
                                        Collectors.toList()),
                                Collectors.filtering(
                                        vehicle -> vehicle instanceof Truck truck && truck.engine() == Engine.ELECTRIC,
                                        Collectors.toList()),
                                (cars, trucks)->{
                                    cars.addAll(trucks);
                                    return cars;
                                }
                        )
                );
        System.out.println("Electric Vehicles = "+electricalVehicle);
    }
}
