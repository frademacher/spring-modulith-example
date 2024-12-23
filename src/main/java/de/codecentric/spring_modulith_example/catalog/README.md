# Catalog Module
This package contains the source code of the Catalog module. It illustrates how Spring Modulith maps Java package
structures to modules:
- All code that is contained in classes which are `public` and directly located in the root package (here:
`de.codecentric.spring_modulith_example.catalog`) is considered to belong to the module's API. That is, other modules
are allowed by Spring Modulith to refer to the API classes and use the contained code.
- Any other code that is contained in non-`public` classes or, independent of its visibility, is contained in
sub-packages such as `controller` or `model` is not allowed by Spring Modulith to be accessed from other modules than
Catalog. Rather, this code is considered module-internal.
- An exception to the previous interpretation of Java package structures by Spring Modulith are sub-packages that
comprise a `package-info.java` file which marks the sub-packages as modules by means of Spring Modulith's
`@ApplicationModule` annotation. Any sub-package with this annotation is considered to represent a nested module of
the parent module that contains the sub-package. Code in nested modules is available to sibling nested modules of the
same parent module and to the parent module itself. Nested modules can themselves leverage code from parent modules,
nested sibling modules, and types exposed from other top-level modules. With Search Optimization, the Catalog module
hosts a nested module as specified in the [`package-info.java`](nested_modules/search_optimization/package-info.java)
file of the `search_optimization` package.