/**
 * This package hosts code that influences business or technical behavior which concerns more than one module of the
 * application. Precisely, the {@link de.codecentric.spring_modulith_example.shared.Defaults} class defines
 * application-global constants and the
 * {@link de.codecentric.spring_modulith_example.shared.GlobalControllerExceptionHandler} class maps Java exceptions
 * from the application's HTTP APIs to specific HTTP status code.
 * <br/>
 * Given Spring Modulith's interpretation of Java packages as modules, this package constitutes the Shared module of the
 * application with one exposed type, i.e., {@link de.codecentric.spring_modulith_example.shared.Defaults} which has
 * public visibility.
 */
package de.codecentric.spring_modulith_example.shared;