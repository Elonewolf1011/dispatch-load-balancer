package com.dispatch.loadbalancer.controller;

import com.dispatch.loadbalancer.model.Order;
import com.dispatch.loadbalancer.model.Vehicle;
import com.dispatch.loadbalancer.service.DispatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> acceptOrders(@RequestBody Map<String, List<Order>> payload) {
        dispatchService.saveOrders(payload.get("orders"));
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Delivery orders accepted.");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/vehicles")
    public ResponseEntity<Map<String, Object>> acceptVehicles(@RequestBody Map<String, List<Vehicle>> payload) {
        dispatchService.saveVehicles(payload.get("vehicles"));
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Vehicle details accepted.");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/plan")
    public ResponseEntity<Map<String, Object>> getDispatchPlan() {
        Map<String, Object> response = new HashMap<>();
        response.put("dispatchPlan", dispatchService.generateDispatchPlan());
        return ResponseEntity.ok(response);
    }
}