# General information:

for triggering the controller , we can use this endpoint:
GET - http://localhost:8082/player

I used the below maven dependencies:
<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-websocket</artifactId>
		</dependency>
		<!--//csv 3rd library for handling csv -->
		<dependency>
			<groupId>com.opencsv</groupId>
			<artifactId>opencsv</artifactId>
			<version>5.0</version>
		</dependency>
		<!--GSON dependency -->
		<dependency>
			<groupId>com.google.code.gson</groupId>
			<artifactId>gson</artifactId>
			<version>2.8.5</version>
		</dependency>
		<!-- Redis dependencies-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-cache</artifactId>
			<version>2.4.3</version>
		</dependency>
		<dependency>
			<groupId>redis.clients</groupId>
			<artifactId>jedis</artifactId>
			<version>2.8.1</version>
		</dependency>
	</dependencies>
	
	
	with the endpoint mentioned above there are two scenarios:
	1) that it is the 1st time we are connecting to this endpoint - 
	in such case , will use the input file and retreive the players details
	from the "https://www.balldontlie.io/api/v1/players/{id}" API.
	2) will have an exist output file and will need to check if it is 
	exist in cache.
	- in case it exist in cache , will skip updating the file. 
	- if not exist in cache , will write the file and set the cache.
	
	