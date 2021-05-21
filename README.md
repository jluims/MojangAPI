# MojangAPI Java Wrapper
This project wraps the MojangAPI using Java

# Building
Run the following command in a shell:
```shell
./gradlew build
```

# Gradle
Add jitpack.io to your gradle maven repositories
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add this project to your dependencies
```gradle
	dependencies {
	        implementation 'com.github.BrxenDev:MojangAPI:Tag'
	}
```

# Examples
Authentication wrapper:
```java
AuthenticationWrapper wrapper = new AuthenticationWrapper(clientToken, "YourUserAgent 1.0");
// Login
Player player = wrapper.authenicate(username, password);
UUID uuid = player.getUUID();
String username = player.getUsername();
String accessToken = player.getAccessToken();
```
API wrapper:
```java
MojangAPIWrapper wrapper = new MojangAPIWrapper("YourUserAgent 1.0 / Alpha");

for (NameHistoryEntry entry : wrapper.fromUUID(UUID.fromString("547b8192-7905-44dd-90ae-58608787c141"))) {
    Long date = entry.getDate();
    System.out.println(entry.getName() + " " + (date != null ? date : ""));
}
```

### This project is currently unfinished and is missing some API methods, I will soon add documented and non-documented endpoints.
