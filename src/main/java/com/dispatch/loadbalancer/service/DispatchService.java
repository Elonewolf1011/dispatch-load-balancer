package com.dispatch.loadbalancer.service;

import com.dispatch.loadbalancer.model.Order;
import com.dispatch.loadbalancer.model.Vehicle;
import com.dispatch.loadbalancer.util.DistanceUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DispatchService {

    private final List<Order> storedOrders = new ArrayList<>();
    private final List<Vehicle> storedVehicles = new ArrayList<>();

    public synchronized void saveOrders(List<Order> orders) {
        this.storedOrders.clear();
        if (orders != null) {
            this.storedOrders.addAll(orders);
        }
    }

    public synchronized void saveVehicles(List<Vehicle> vehicles) {
        this.storedVehicles.clear();
        if (vehicles != null) {
            this.storedVehicles.addAll(vehicles);
        }
    }

    public synchronized List<Map<String, Object>> generateDispatchPlan() {
        List<Order> sortedOrders = new ArrayList<>(storedOrders);
        sortedOrders.sort((o1, o2) -> getPriorityRank(o2.getPriority()) - getPriorityRank(o1.getPriority()));

        Map<String, Double> vehicleLoads = new HashMap<>();
        Map<String, Double> vehicleDistances = new HashMap<>();
        Map<String, List<Order>> vehicleAssignedOrders = new HashMap<>();
        Map<String, double[]> vehicleCurrentLocations = new HashMap<>();

        for (Vehicle v : storedVehicles) {
            vehicleLoads.put(v.getVehicleId(), 0.0);
            vehicleDistances.put(v.getVehicleId(), 0.0);
            vehicleAssignedOrders.put(v.getVehicleId(), new ArrayList<>());
            vehicleCurrentLocations.put(v.getVehicleId(),
                    new double[]{v.getCurrentLatitude(), v.getCurrentLongitude()});
        }

        for (Order order : sortedOrders) {
            Vehicle bestVehicle = null;
            double minDistance = Double.MAX_VALUE;

            for (Vehicle v : storedVehicles) {
                double currentLoad = vehicleLoads.get(v.getVehicleId());
                if (currentLoad + order.getPackageWeight() <= v.getCapacity()) {
                    double[] loc = vehicleCurrentLocations.get(v.getVehicleId());
                    double dist = DistanceUtils.calculateDistance(
                            loc[0], loc[1], order.getLatitude(), order.getLongitude());

                    if (dist < minDistance) {
                        minDistance = dist;
                        bestVehicle = v;
                    }
                }
            }

            if (bestVehicle != null) {
                String vId = bestVehicle.getVehicleId();
                vehicleLoads.put(vId, vehicleLoads.get(vId) + order.getPackageWeight());
                vehicleDistances.put(vId, vehicleDistances.get(vId) + minDistance);
                vehicleAssignedOrders.get(vId).add(order);
                vehicleCurrentLocations.put(vId, new double[]{order.getLatitude(), order.getLongitude()});
            }
        }

        List<Map<String, Object>> dispatchPlan = new ArrayList<>();
        for (Vehicle v : storedVehicles) {
            Map<String, Object> planEntry = new LinkedHashMap<>();
            planEntry.put("vehicleId", v.getVehicleId());
            planEntry.put("totalLoad", vehicleLoads.get(v.getVehicleId()));
            planEntry.put("totalDistance", String.format("%.0f km", vehicleDistances.get(v.getVehicleId())));
            planEntry.put("assignedOrders", vehicleAssignedOrders.get(v.getVehicleId()));
            dispatchPlan.add(planEntry);
        }

        return dispatchPlan;
    }

    private int getPriorityRank(String priority) {
        if (priority == null) return 0;
        return switch (priority.toUpperCase()) {
            case "HIGH" -> 3;
            case "MEDIUM" -> 2;
            case "LOW" -> 1;
            default -> 0;
        };
    }
}