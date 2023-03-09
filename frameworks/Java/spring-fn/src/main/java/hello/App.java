package hello;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import com.zaxxer.hikari.HikariDataSource;
import hello.controller.HelloController;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MongoRepositoriesAutoConfiguration.class})
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Bean
	@Profile({ "jdbc", "jpa" })
	public DataSource datasource(DataSourceProperties dataSourceProperties) {
		HikariDataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class)
				.build();
		dataSource.setMaximumPoolSize(Runtime.getRuntime().availableProcessors() * 2);

		return dataSource;
	}

	@Bean
	public RouterFunction<ServerResponse> routerFunction(HelloController controller) {
		return RouterFunctions.route()
				.GET("/plaintext", controller::plainText)
				.GET("/json", controller::json)
				.GET("/db", controller::db)
				.GET("/queries", controller::queries)
				.GET("/updates", controller::updates)
				.GET("/fortunes", controller::fortunes)
				.build();
	}
}
