package com.thehecklers.cosmosdbmongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CosmosdbMongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CosmosdbMongoApplication.class, args);
	}

}

@Component
class DataLoader {
	private final UserRepository repo;

	DataLoader(UserRepository repo) {
		this.repo = repo;
	}

	@PostConstruct
	void loadData() {
		repo.deleteAll()
				.thenMany(Flux.just(new User("1", "Alpha", "Bravo", "123 N 4th St"),
						new User("2", "Charlie", "Delta", "1313 Mockingbird Ln")))
				.flatMap(repo::save)
				.thenMany(repo.findAll())
				.subscribe(System.out::println);
	}
}

@RestController
class CosmosMongoController {
	private final UserRepository repo;

	CosmosMongoController(UserRepository repo) {
		this.repo = repo;
	}

	@GetMapping
	Flux<User> getAllUsers() {
		return repo.findAll();
	}
}

interface UserRepository extends ReactiveCrudRepository<User, String> {}

@Document
record User(@Id String id, String firstName, String lastName, String address) {}