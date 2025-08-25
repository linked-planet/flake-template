# Template

## Features

Here's a list of features included in this project:

| Name                                                                  | Description                                                                        |
| -----------------------------------------------------------------------|------------------------------------------------------------------------------------ |
| [Caching Headers](https://start.ktor.io/p/caching-headers)            | Provides options for responding with standard cache-control headers                |
| [Compression](https://start.ktor.io/p/compression)                    | Compresses responses using encoding algorithms like GZIP                           |
| [Default Headers](https://start.ktor.io/p/default-headers)            | Adds a default set of headers to HTTP responses                                    |
| [Forwarded Headers](https://start.ktor.io/p/forwarded-header-support) | Allows handling proxied headers (X-Forwarded-*)                                    |
| [Conditional Headers](https://start.ktor.io/p/conditional-headers)    | Skips response body, depending on ETag and LastModified headers                    |
| [Routing](https://start.ktor.io/p/routing)                            | Provides a structured routing DSL                                                  |
| [Call Logging](https://start.ktor.io/p/call-logging)                  | Logs client requests                                                               |
| [Call ID](https://start.ktor.io/p/callid)                             | Allows to identify a request/call.                                                 |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation)    | Provides automatic content conversion according to Content-Type and Accept headers |
| [GSON](https://start.ktor.io/p/ktor-gson)                             | Handles JSON serialization using GSON library                                      |
| [Status Pages](https://start.ktor.io/p/status-pages)                  | Provides exception handling for routes                                             |

## Building & Running

### Using Just

This project includes a `.justfile` for simplified task management. Use the following commands:

| Command           | Description                                                          |
| ------------------|---------------------------------------------------------------------- |
| `just`            | Interactive menu to select and run available tasks                  |
| `just build`      | Build the project using gradle                                      |
| `just start`      | Run the server                                                       |
| `just develop`    | Start the server and watch for changes (parallel execution)         |
| `just watch`      | Continuously build on file changes                                   |
| `just format`     | Format code using nix formatter                                      |
| `just lint`       | Run linting checks using nix flake                                   |

### Using Gradle

Alternatively, you can use gradle directly:

| Task                          | Description                                                          |
| -------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `buildImage`                  | Build the docker image to use with the fat JAR                       |
| `publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `run`                         | Run the server                                                       |
| `runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

