package ar.edu.utn.frc.tup.tesis.pinceletas_user_auth_service.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
/**
 * Configuración para el servicio de ubicaciones geográficas.
 * Define RestTemplate y caché para optimizar las consultas a APIs externas de países y provincias.
 */
@Configuration
@EnableCaching
public class LocationConfig {

    /**
     * Crea un RestTemplate configurado para consultas a APIs de ubicaciones.
     *
     * @return RestTemplate con timeouts configurados.
     */
    @Bean
    public RestTemplate locationRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        return restTemplate;
    }

    /**
     * Configura los timeouts para las peticiones HTTP del RestTemplate.
     *
     * @return ClientHttpRequestFactory con timeouts de conexión y lectura.
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        return factory;
    }

    /**
     * Configura el gestor de caché para almacenar respuestas de las APIs de ubicaciones.
     * Reduce la cantidad de peticiones a las APIs externas y mejora el rendimiento.
     *
     * @return CacheManager con cachés para countries, country y states.
     */
    @Bean
    public CacheManager locationCacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
                "countries",
                "country",
                "states"
        ));
        return cacheManager;
    }
}
