WQWeb is a wrapper around the Spark Framework, that aims to offer an opinionated view of how a web framework should be configured. The solution adds helpers for Database access, JWT (Json Web Token) and other utilities. It also allows the spark framework routes to be neatly split into several controllers, to more effectively partition your web application.

# Set Up Guide #

These steps will get simpler in a later iteration, when the run scripts have been written. 

1. Clone this git repo (or download zip). Expand to a location.
2. Run `mvn package` and then `mvn install` to get this package in your local maven repo
3. Create a new project, with a pom.xml similar to the below sample
4. Create a new Java class, annotated with @Controller, with an initialiser block containing your routes, see example below
5. Finally, run the application with `mvn exec:java`, all your Controllers on the classpath will automatically get picked up, and will be ready to roll! Point your browser at `http://localhost:9000/` to see.

*optional* - Create an app.conf if you intend to use JWT / DB or a custom port / custom static file location      


        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
            <modelVersion>4.0.0</modelVersion>
            <groupId>codemwnci</groupId>
            <version>0.0.1-SNAPSHOT</version>
              
            <properties>
                <exec.mainClass>wqweb.ApplicationBootstrap</exec.mainClass>
            </properties>

            
            <dependencies>
                <dependency>
                    <groupId>codemwnci</groupId>
                    <artifactId>wqweb</artifactId>
                    <version>0.0.1</version>
                </dependency>
                <dependency>
                    <groupId>mysql</groupId>
                    <artifactId>mysql-connector-java</artifactId>
                    <version>5.1.33</version>
                </dependency>				
            </dependencies>	
            <artifactId>your-application-name</artifactId>
        </project>  


## Example ##

The following is a simple controller, with two routes. 

The first route responds with a simple hello world. 

The second route is slightly more complex, as it uses JWT for auth tokens, a DB for inserting data, and also returns different status codes depending on success

    package myapp;

    import wqweb.*;
    import java.sql.*;
    import static spark.Spark.*;

    @Controller
    public class TestController {
        {			
            // simple Hello World GET route
            get("/", (req, res) -> { 
               return "Hello World";
            });
            
            get("/api/person/:id", (req, res) -> { 
               return "Hello from user ID " + req.params("id");
            });
		
			// a POST route to add a new person into the database, first checking the JWT
			post("/api/person/", (req, res) -> {
				
				long uid = JWT.getId(req);
				
	            String sql = "INSERT INTO ...";
	            try (Connection conn = DB.getConn(); PreparedStatement ps = conn.prepareStatement(sql)) {
	                // ... set values / execute update etc
	                // get id
	                res.status(200);
	                return "{\"id\": "+id+"}";
	            }
	            catch (SQLException sqlx) {	halt(503, "something went wrong"); }	
	            
                res.status(500);
                return "it didn't work out";
	        });
	    }
    }



## app.conf settings ##

The app confing allows you to configure the behaviour of the application, such as the port, static content location, database configuration and JWT security configuration.

Copy the following code and place it inside a file named `app.conf` in the root of the project directory. Modify the settings to meet your needs. 

    app.secret=a-random-secret-string-for-JWT-security

    db.driver=com.mysql.jdbc.Driver            
    db.url=jdbc:mysql://localhost:3306/<your-dbname-here>
    db.user=<your-dbusername-here>
    db.pass=<your-dbpassword-here>

    # defaults to 'client' if not present
    public_dir=public

    # defaults to '9000' if not present
    port=9000 


## Future Enhancements ##

play framework style scripts for
 - publish to maven central to remove steps 1&2 from the quick start
 - generating the application structure (wqweb new)
 - running the server (wqweb run)

 
