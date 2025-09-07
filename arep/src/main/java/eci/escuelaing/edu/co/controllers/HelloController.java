package eci.escuelaing.edu.co.controllers;

import eci.escuelaing.edu.co.annotations.*;

@RestController
public class HelloController {

    @GetMapping("/hola")
    public String saludo(@RequestParam(value = "name", defaultValue = "mundo") String name) {
        return "Hola " + name;
    }

    @GetMapping("/greetings")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}