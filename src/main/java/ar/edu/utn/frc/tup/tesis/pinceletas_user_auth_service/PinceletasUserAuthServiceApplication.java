package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de la aplicación Spring Boot para el servicio de autenticación de usuarios de Pinceletas.
 * Sirve como punto de entrada para iniciar el servicio completo de autenticación y gestión de usuarios.
 * Configura la aplicación con Spring Boot, habilita tareas programadas y define el escaneo de componentes.
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {
		"ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service",
		"ar.edu.utn.frc.tup.tesis.pinceletas.common.security"
})
public class PinceletasUserAuthServiceApplication {

	/**
	 * Método principal que inicia la aplicación Spring Boot.
	 * Configura y lanza el servidor embebido con toda la configuración de autenticación y seguridad.
	 * Inicializa todos los beans, configuraciones y servicios definidos en la aplicación.
	 *
	 * @param args Argumentos de línea de comandos para configuración adicional de Spring Boot.
	 */
	public static void main(String[] args) {

		SpringApplication.run(PinceletasUserAuthServiceApplication.class, args);
	}

}
