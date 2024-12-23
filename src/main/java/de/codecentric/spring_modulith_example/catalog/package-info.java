/**
 * This module illustrates the usage of Spring Modulith's {@link org.springframework.modulith.ApplicationModule}
 * annotation to explicitly limit the dependencies of a module. Here, we use the annotation to constrain the Catalog
 * module to only access types from the Inventory module, which then becomes the only module on which the Catalog module
 * is allowed to depend.
 */
@ApplicationModule(
  allowedDependencies = "inventory"
)
package de.codecentric.spring_modulith_example.catalog;

import org.springframework.modulith.ApplicationModule;