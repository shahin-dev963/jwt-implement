package com.shahin.jwt.api.controller;

import com.google.gson.Gson;
import com.shahin.jwt.api.entity.AuthRequest;
import com.shahin.jwt.api.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class ApiController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> get(HttpServletRequest request) {
        common(request);

        Map<String, Object> response = new HashMap<>();

        response.put("value", "Book");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<Object> getAll(HttpServletRequest request) {
        common(request);
        Map<String, Object> response = new HashMap<>();

        response.put("value", Arrays.asList("Pen", "Book"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public void common(HttpServletRequest request){

        String token = request.getHeader("Authorization");

        Claims claims = jwtUtil.extractAllClaims(token.substring(7));
        Map<String, Object> data = new LinkedHashMap<>();

        data.put( "name", claims.get("name") );
        data.put( "id", claims.get("id") );
        data.put( "validated", claims.get("validated") );

        Gson gson = new Gson();
        System.out.println(gson.toJson(data));
    }


    @PostMapping("/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
            );
        } catch (Exception ex) {
            throw new Exception("inavalid username/password");
        }
        return jwtUtil.generateToken(authRequest.getUserName());
    }
}
