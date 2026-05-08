package com.worksonourmachines.marketplace;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/marketplace/v1")
public class MarketplaceController {
  
    @RequestMapping("/hello")
    public ResponseEntity<String> hello() {
        return new ResponseEntity<>("Marketplace says Hello!", HttpStatus.OK);
    }
}
