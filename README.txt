# Build project:
mvn clean package

# Run:
java -classpath target/cache-1.0.jar:target/libs/*:. edu.uv.cs.cache.main.WebAppCache

# Sample requests:

# GET request
curl http://localhost:8080/service/cache

# Post request to add a new pair
curl -X POST -d '{"key":"IP","value":"10.2.2.2"}' http://localhost:8080/service/cache

# Requests to get a pair
curl http://localhost:8080/service/cache?key=IP

# Request to add a new Pair
curl -X POST -d '{"key":"N","value":"1"}' http://localhost:8080/service/cache

# GET all pairs
curl http://localhost:8080/service/cache

# This is not going to work as the code that deals with DELETE is commented
# DELETE a pair
curl -X DELETE -d 'IP' http://localhost:8080/service/cache

# GET al pairs
curl http://localhost:8080/service/cache
