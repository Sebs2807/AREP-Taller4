package eci.escuelaing.edu.co;

import eci.escuelaing.edu.co.annotations.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

public class MicroSpringBoot {

    public static void main(String[] args) throws Exception {
        scanPackage("eci.escuelaing.edu.co.controllers");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Apagando servidor...");
            HttpServer.stop();
        }));

        HttpServer.staticfiles("www");

        HttpServer.main(new String[]{});
    }

    /**
     * Escanea un paquete en busca de clases anotadas con @RestController
     */
    public static void scanPackage(String packageName) throws Exception {
        Reflections reflections = new Reflections(
            new ConfigurationBuilder()
                .forPackages(packageName)
                .addScanners(Scanners.SubTypes, Scanners.TypesAnnotated)
        );

        Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(RestController.class);

        for (Class<?> c : controllers) {
            processClass(c);
        }
    }

    /**
     * Procesa una clase y registra sus métodos con @GetMapping en el HttpServer
     */
    private static void processClass(Class<?> c) throws Exception {
        System.out.println("Encontrado controller: " + c.getName());

        Object controllerInstance = c.getDeclaredConstructor().newInstance();

        for (Method method : c.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                String path = mapping.value();

                HttpServer.get(path, (req, res) -> {
                    try {
                        // Construir lista de argumentos
                        Object[] args = new Object[method.getParameterCount()];
                        Parameter[] params = method.getParameters();

                        for (int i = 0; i < params.length; i++) {
                            if (params[i].isAnnotationPresent(RequestParam.class)) {
                                RequestParam rp = params[i].getAnnotation(RequestParam.class);
                                String value = req.getQuery(rp.value());
                                if (value == null || value.isEmpty()) {
                                    value = rp.defaultValue();
                                }
                                args[i] = value;
                            }
                        }

                        Object result = method.invoke(controllerInstance, args);
                        return result != null ? result.toString() : "";
                    } catch (Exception e) {
                        e.printStackTrace();
                        return "500 Internal Server Error: " + e.getMessage();
                    }
                });

                System.out.println("   → Ruta registrada: " + path);
            }
        }
    }
}
