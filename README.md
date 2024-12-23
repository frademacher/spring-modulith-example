# Spring Modulith Example
This example Spring Boot application illustrates several techniques provided by
[Spring Modulith](https://spring.io/projects/spring-modulith) to create and ensure the correctness of the architectural
structure of [modulithic applications](https://dzone.com/articles/architecture-style-modulith-vs-microservices).

More precisely, the following features of Spring Modulith are exemplified:
- Implicit (visibility-based) and explicit (`package-info.java`) module definition.
- Handling of nested and shared modules.
- Adherence to and verification of the intended modulithic architecture.
- API-based and event-based module interactions.
- Module integration testing.

## Starting Points for Understanding the Example Code
The example application comes with four test classes which provide a good start to understand, and play around with the
application's modulithic structure and implementation (see below for more details on the application's modulithic
architecture):
1. [`ArchitectureTest`](src/test/java/de/codecentric/spring_modulith_example/ArchitectureTest.java): Shows how to
trigger Spring Modulith's verification and diagram generation of modulithic architectures.
2. [`CatalogIntegrationTests`](src/test/java/de/codecentric/spring_modulith_example/catalog/CatalogIntegrationTests.java):
Integration tests for the Catalog module that in particular show how to test modulithic events that happened at
application startup as well as the usage of Spring Modulith's Scenario API.
3. [`MockedCatalogIntegrationTests`](src/test/java/de/codecentric/spring_modulith_example/catalog/MockedCatalogIntegrationTests.java):
Integration tests for the Catalog module that rely on database mocks. The tests in particular show Spring Modulith's
interplay with [Mockito](https://site.mockito.org), and how to leverage the Scenario API to trigger and check the
ramifications of stimuli known to a modulithic application.
4. [`InventoryIntegrationTests`](src/test/java/de/codecentric/spring_modulith_example/inventory/InventoryIntegrationTests.java):
Integration tests for the Inventory module. These tests in particular show how to leverage Spring Modulith's Scenario
API to check for state changes that are expected after event occurrence and how to use the `PublishedEvents`
abstraction to check for structural properties of module events that occurred during test execution.

The tests can be executed with Maven as follows:
`mvn clean test`.

## Details on the Example Application's Modulithic Architecture
The example application is as concise as necessary to illustrate the above features of Spring Modulith. It consists of
four modules, of which the first two account for the majority of illustrated features of Spring Modulith:
1. [Catalog](src/main/java/de/codecentric/spring_modulith_example/catalog/README.md): This module implements the
[`Product`](src/main/java/de/codecentric/spring_modulith_example/catalog/model/Product.java) class which enables the
storage and retrieval of information about company products. This information can be queried by HTTP clients from the
[`ProductApi`](src/main/java/de/codecentric/spring_modulith_example/catalog/controller/ProductApi.java). Upon
application startup, the module creates three example products within
[`CatalogData.initialData()`](src/main/java/de/codecentric/spring_modulith_example/catalog/model/CatalogData.java).
For each created product, the module fires an
[`InventoryProductCreated`](src/main/java/de/codecentric/spring_modulith_example/catalog/external_events/InventoryProductCreated.java)
event, which is an implementation of the Inventory module's
[`ProductCreated`](src/main/java/de/codecentric/spring_modulith_example/inventory/ProductCreated.java) event interface
(see below).
[`InventoryProductCreated`](src/main/java/de/codecentric/spring_modulith_example/catalog/external_events/InventoryProductCreated.java)
informs the Inventory module about the creation of a new product, and the module reacts to this event by creating a new
[`Stock`](src/main/java/de/codecentric/spring_modulith_example/inventory/model/Stock.java) entry in its database and
firing a [`QuantityChanged`](src/main/java/de/codecentric/spring_modulith_example/inventory/QuantityChanged.java) event.
The Catalog module reacts to this latter event in
[`CatalogData.quantityChanged()`](src/main/java/de/codecentric/spring_modulith_example/catalog/model/CatalogData.java)
and updates its data model to store a product's current quantity in stock.
2. [Inventory](src/main/java/de/codecentric/spring_modulith_example/inventory/README.md): This module implements the
[`Stock`](src/main/java/de/codecentric/spring_modulith_example/inventory/model/Stock.java) class which enables the
storage and retrieval of information about products' quantities in stock. This information can be queried and
manipulated by HTTP clients from the
[`InventoryApi`](src/main/java/de/codecentric/spring_modulith_example/inventory/InventoryApi.java). The Inventory module
and the Catalog module interact by means of events. The Inventory module can react to any instances of the
 [`ProductCreated`](src/main/java/de/codecentric/spring_modulith_example/inventory/ProductCreated.java) event interface
and itself emits
[`QuantityChanged`](src/main/java/de/codecentric/spring_modulith_example/inventory/QuantityChanged.java) events. Vice
versa, the Catalog module can react to
[`QuantityChanged`](src/main/java/de/codecentric/spring_modulith_example/inventory/QuantityChanged.java) events and 
itself emits
[`InventoryProductCreated`](src/main/java/de/codecentric/spring_modulith_example/catalog/external_events/InventoryProductCreated.java)
(see above).
3. [Search Optimization](src/main/java/de/codecentric/spring_modulith_example/catalog/nested_modules/search_optimization/package-info.java):
This module enables more complex queries to product information from the
[`SearchOptimizationApi`](src/main/java/de/codecentric/spring_modulith_example/catalog/nested_modules/search_optimization/controller/SearchOptimizationApi.java)
and its primary purpose is to illustrate Spring Modulith's support for nested modules.
4. [Shared](src/main/java/de/codecentric/spring_modulith_example/shared/package-info.java): This module provides global
business and technical code to the other modules. Its primary purpose is to illustrate Spring Modulith's support for
shared modules.