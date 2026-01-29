package com.cine.cinema.web;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.*;

@Controller
public class ProductsController {

    private final JdbcTemplate jdbcTemplate;

    public ProductsController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        var rows = jdbcTemplate.queryForList("select id, nom, prix, stock from produit order by id");
        model.addAttribute("products", rows);
        return "products";
    }

    @PostMapping("/products/buy")
    public String buyProduct(@RequestParam Long productId, @RequestParam Long clientId, @RequestParam Integer quantity, HttpSession session) {
        if (quantity == null || quantity <= 0) quantity = 1;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> cart = (Map<String, Object>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.get("items");
        if (items == null) {
            items = new ArrayList<>();
        }
        
        Map<String, Object> item = new HashMap<>();
        item.put("productId", productId);
        item.put("quantity", quantity);
        
        items.add(item);
        cart.put("items", items);
        cart.put("clientId", clientId);
        
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }
    
    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        Map<String, Object> cart = (Map<String, Object>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.get("items");
        if (items == null) {
            items = new ArrayList<>();
        }
        
        List<Map<String, Object>> enriched = new ArrayList<>();
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            Long productId = ((Number) item.get("productId")).longValue();
            Integer qty = ((Number) item.get("quantity")).intValue();
            var prod = jdbcTemplate.queryForMap("select id, nom, prix from produit where id = ?", productId);
            java.math.BigDecimal price = (java.math.BigDecimal) prod.get("prix");
            java.math.BigDecimal itemTotal = price.multiply(java.math.BigDecimal.valueOf(qty));
            
            Map<String, Object> enrichedItem = new HashMap<>(prod);
            enrichedItem.put("quantity", qty);
            enrichedItem.put("itemTotal", itemTotal);
            enriched.add(enrichedItem);
            total = total.add(itemTotal);
        }
        
        model.addAttribute("cartItems", enriched);
        model.addAttribute("cartTotal", total);
        model.addAttribute("clientId", cart.get("clientId"));
        return "cart";
    }
    
    @PostMapping("/cart/checkout")
    public String checkoutCart(HttpSession session, @RequestParam Long clientId, @RequestParam(required = false) String dateVente) {
        @SuppressWarnings("unchecked")
        Map<String, Object> cart = (Map<String, Object>) session.getAttribute("cart");
        if (cart == null) {
            return "redirect:/products";
        }
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) cart.get("items");
        if (items == null || items.isEmpty()) {
            return "redirect:/products";
        }
        
        LocalDate date = LocalDate.now();
        if (dateVente != null && !dateVente.isEmpty()) {
            try {
                date = LocalDate.parse(dateVente);
            } catch (Exception e) {
                date = LocalDate.now();
            }
        }
        
        for (Map<String, Object> item : items) {
            Long productId = ((Number) item.get("productId")).longValue();
            Integer qty = ((Number) item.get("quantity")).intValue();
            jdbcTemplate.update("insert into vente_produit (idproduit, idClient, quantite, dateVente) values (?,?,?,?)",
                    productId, clientId, qty, date);
        }
        
        session.removeAttribute("cart");
        return "redirect:/products?success=true";
    }
}
