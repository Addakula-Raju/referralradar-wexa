# 🧭 ReferralRadar: Professional Network Graph

**🌟 Live Demo:** [https://referralradar-wexa-5evk.onrender.com](https://referralradar-wexa-5evk.onrender.com)  
**📹 Video Walkthrough:** [Insert your YouTube/Drive Link Here]

## The Use Case
ReferralRadar is a full-stack professional networking and talent-matching application. It empowers users to find warm introductions to target companies by traversing their network of connections (1st, 2nd, and 3rd-degree connections).

## Why a Graph Database?
Finding a "friend of a friend who works at Google" in a traditional Relational Database (SQL) is highly inefficient. It requires expensive, recursive `JOIN` operations across massive tables, which degrade performance exponentially as the network grows and the hops increase.

By using **CognoDB**, this application leverages **index-free adjacency**. Relationships are treated as first-class entities rather than computed at query time. A query to find a 3-hop referral path takes just a single, highly readable line of Cypher:
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

## Tech Stack & Architecture
* **Frontend:** React.js (Vite), deployed on Render (Static Site)
* **Backend:** Java Spring Boot 3, containerized with Docker, deployed on Render (Web Service)
* **Database:** CognoDB (Neo4j driver), cloud-hosted
* **Security:** Parameterized Cypher queries to prevent injection attacks; environment variables for safe credential management.

---

## Local Setup & Run Instructions

**1. Database (CognoDB)**
* Create a free instance at console.cognodb.com
* Run the Cypher seed script included in `seed.cypher` to populate the graph.

**2. Backend (Spring Boot)**
* Requires Java 21+
* Add your CognoDB credentials to a `.env` file in the root directory:
  ```env
  COGNODB_URI=bolt+s://<your-instance>.databases.cognodb.cloud
  COGNODB_USER=cognodb
  COGNODB_PASSWORD=<your-password>