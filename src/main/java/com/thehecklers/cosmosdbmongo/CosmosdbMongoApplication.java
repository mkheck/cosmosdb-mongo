package com.thehecklers.cosmosdbmongo;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CosmosdbMongoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CosmosdbMongoApplication.class, args);
    }

}

@Slf4j
@Component
@AllArgsConstructor
class DataLoader {
    private final UserRepository repo;

    @PostConstruct
    void loadData() {
        repo.deleteAll()
                .thenMany(Flux.just(new User("Alpha", "Bravo", "123 N 45th St"),
                        new User("Charlie", "Delta", "1313 Mockingbird Lane")))
                .flatMap(repo::save)
                .thenMany(repo.findAll())
                .subscribe(user -> log.info(user.toString()));
    }
}

@RestController
@AllArgsConstructor
class CosmosMongoController {
    private final UserRepository repo;

    @GetMapping
    Flux<User> getAllUsers() {
        return repo.findAll();
    }

    @GetMapping("/oneuser")
    Mono<User> getFirstUser() {
        return repo.findAll().next();
    }

    @PostMapping("/newuser")
    Mono<User> addUser(@RequestBody User user) {
        return repo.save(user);
    }
}

interface UserRepository extends ReactiveCrudRepository<User, String> {
}

@Document
@Data
@NoArgsConstructor
@RequiredArgsConstructor
class User {
    @Id
    private String id;
    @NonNull
    private String firstName, lastName, address;
}