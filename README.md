# 🧭 ReferralRadar: Professional Network Graph

## The Use Case
ReferralRadar is a professional networking and talent-matching application. It allows users to find warm introductions to target companies by traversing their network of connections (1st, 2nd, and 3rd-degree connections).

## Why a Graph Database?
Finding a "friend of a friend who works at Google" in a traditional Relational Database (SQL) is highly inefficient. It requires expensive, recursive `JOIN` operations across massive tables, which degrade performance exponentially as the network grows.

By using **CognoDB**, the application leverages **index-free adjacency**. Relationships are treated as first-class entities. A query to find a 3-hop referral path takes just a single, highly readable line of Cypher:
`MATCH path = (me:Person)-[:KNOWS*1..3]-(connection)-[:WORKS_AT]->(target:Company)`
This graph-native approach drops query latency from seconds (in SQL) to milliseconds, while making the data model far easier to scale and maintain.

## Graph Data Model
**Nodes:**
* `(:Person {id, name, role})`
* `(:Company {name, industry})`
* `(:Skill {name})`

**Relationships:**
* `(Person)-[:KNOWS]->(Person)`
* `(Person)-[:WORKS_AT]->(Company)`
* `(Person)-[:HAS_SKILL]->(Skill)`

## Setup & Run Instructions
**1. Database (CognoDB)**
* Create a free instance at console.cognodb.com
* Run the Cypher seed script included in `seed.cypher`

**2. Backend (Spring Boot)**
* Requires Java 21+
* Add your CognoDB credentials to a `.env` file in the root directory:
  `COGNODB_URI=bolt+s://<your-instance>.databases.cognodb.cloud`
  `COGNODB_USER=cognodb`
  `COGNODB_PASSWORD=<your-password>`
* Run `ReferralradarApplication.java` (Starts on Port 8080)

**3. Frontend (React / Vite)**
* Navigate to `referral-ui` directory
* Run `npm install`
* Run `npm run dev` (Starts on Port 5173)