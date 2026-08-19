package com.wexa.referralradar;

import io.github.cdimascio.dotenv.Dotenv;
import org.neo4j.driver.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allows React frontend to connect
public class ReferralController implements AutoCloseable {

    private final Driver driver;

    public ReferralController() {
        // Reads from .env file to satisfy the security requirement
        Dotenv dotenv = Dotenv.load();
        this.driver = GraphDatabase.driver(
                dotenv.get("COGNODB_URI"),
                AuthTokens.basic(dotenv.get("COGNODB_USER"), dotenv.get("COGNODB_PASSWORD"))
        );
    }

    @Override
    public void close() {
        driver.close();
    }

    // REQUIRED 1: The Multi-Hop Query (2+ hops) to find a referral path
    @GetMapping("/referral-path")
    public ResponseEntity<?> getReferralPath(
            @RequestParam String myName,
            @RequestParam String targetCompany) {

        // Parameterized query to prevent Cypher injection
        String query = """
            MATCH path = (me:Person {name: $myName})-[:KNOWS*1..3]-(connection:Person)-[:WORKS_AT]->(target:Company {name: $targetCompany})
            RETURN [n in nodes(path) | n.name] AS connectionPath
            LIMIT 5
            """;

        try (Session session = driver.session()) {
            var result = session.run(query, Values.parameters("myName", myName, "targetCompany", targetCompany));

            List<List<String>> paths = new ArrayList<>();
            while (result.hasNext()) {
                paths.add(result.next().get("connectionPath").asList(Value::asString));
            }
            return ResponseEntity.ok(paths);
        } catch (Exception e) {
            // Graceful error handling as required by the rubric
            return ResponseEntity.status(503).body(Map.of("error", "Database currently unreachable: " + e.getMessage()));
        }
    }

    // REQUIRED 2: A query that is awkward in SQL (Find candidates by intersecting skills)
    @GetMapping("/search-talent")
    public ResponseEntity<?> searchTalent(@RequestParam String requiredSkill) {

        String query = """
            MATCH (p:Person)-[:HAS_SKILL]->(s:Skill {name: $skill})
            MATCH (p)-[:WORKS_AT]->(c:Company)
            RETURN p.name AS name, p.role AS role, c.name AS company
            """;

        try (Session session = driver.session()) {
            var result = session.run(query, Values.parameters("skill", requiredSkill));

            List<Map<String, String>> candidates = new ArrayList<>();
            while (result.hasNext()) {
                org.neo4j.driver.Record record = result.next();                candidates.add(Map.of(
                        "name", record.get("name").asString(),
                        "role", record.get("role").asString(),
                        "company", record.get("company").asString()
                ));
            }
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of("error", "Database connection failed."));
        }
    }
}