/**
 * This module illustrates the usage of Spring Modulith's {@link org.springframework.modulith.ApplicationModule}
 * annotation to explicitly tag packages to comprise modules. Using the annotation, we signal Spring Modulith that the
 * nested {@code search_optimization} package in the Catalog module is not an internal package of the Catalog module but
 * instead contains the Search Optimization module, which is nested within the Catalog module.
 */
@ApplicationModule
package de.codecentric.spring_modulith_example.catalog.nested_modules.search_optimization;

import org.springframework.modulith.ApplicationModule;