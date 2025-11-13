# Playground management application

### Startup
* To verify application tests run command `./mvnw clean verify`
* To start application run command `./mvnw spring-boot:run`

### Usage
* Create playsites
* Create attractions
* Add attractions to playsites
* Create customers
* Add/Remove customers from playsite

### API
* You can test API requests in file [create-test-data.http](src/http/create-test-data.http) and other files
* You can use [swagger](http://localhost:8080/swagger-ui/index.html) documentation

### Tech stack
* Java
* Spring boot
* JOOQ
* Flyway
* H2
* OpenAPI

### Todo
* Authentication and authorization
* Remaining unit and integration tests
* Support for multiple playgrounds
* Improve jakarta validations and error handling

### Task
Create a playground REST API

* Define clear and usable domain classes, services, repository, controllers that should be used
access required functionality/data
* API creates and manages play sites in playground. Play sites consists of attractions such as
  double swings, carousel, slide and a ball pit. API allows to create different play sites with
  different combinations of attractions (for example API should enable one to add play site that
  consists of two swings, one slide and three ball pits and etc.)
* Attractions has maximum capacity like swings can have two kids maximum capacity 
* API should expose endpoint that creates playsite with initial set of attractions, endpoint to
  edit playsite, get playsite info, delete playsite. 
* API should expose endpoint that allows to add kid to particular playsite (we know kid's
  name, age, ticket number). Kids ticket number identifies kid uniquely. Api also should have
  endpoint to remove kid from playsite. 
* API should not allow to add more kids to them than specified in configuration of play site
  (sum of all attractions capacity). 
* API should automatically enqueue kid if playsite is full or receive negative result if kid does
  not accept waiting in queue. Or move waiting kid from queue to playsite if kid other was
  removed from playsite API register queues on play sites when tries to add kid to play site,
  that is full, and kid accepts waiting in queue). 
* It should also be possible to remove kid from play site / queue 
* API should provide play site utilization at current moment. Utilization is measured in % 
* API should be able to provide a total visitor count during a current day on all play sites on
  the moment of request. 
* It is not required to use DB or persistent data store, In-memory storage is enough.
