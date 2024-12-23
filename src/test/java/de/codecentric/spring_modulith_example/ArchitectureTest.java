package de.codecentric.spring_modulith_example;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Tests that concern the application's modulithic structure.
 */
class ArchitectureTest {
    /**
     * Holds all {@link ApplicationModules} that Spring Modulith detects for the present application.
     */
    private static final ApplicationModules MODULES = ApplicationModules.of(App.class);

    /**
     * Explicitly verify the correctness of the modulithic structure, e.g., that it does not contain dependency cycles
     * and that modules do not violate visibility-based type reference constraints.
     */
    @Test
    void verifyModulithicArchitecture() {
        MODULES.verify();
    }

    /**
     * Generate PlantUML diagrams which can render the application's modulithic structure into visual representations.
     */
    @Test
    void generatePlantUmlDiagrams() {
        assertDoesNotThrow(() -> new Documenter(MODULES).writeModulesAsPlantUml().writeIndividualModulesAsPlantUml());
    }

    /**
     * Generate AsciiDoc diagrams which can render the application's modulithic structure into visual representations.
     */
    @Test
    @Disabled("Disabled because we prefer PlantUML diagrams for module structure documentation")
    void generateAsciiDoc() {
        assertDoesNotThrow(() -> {
            var diagramOpts = Documenter.DiagramOptions.defaults()
                .withStyle(Documenter.DiagramOptions.DiagramStyle.UML);
            var canvasOpts = Documenter.CanvasOptions.defaults();
		    new Documenter(MODULES).writeDocumentation(diagramOpts, canvasOpts);
        });
    }

    /**
     * Print the application module's as detected by Spring Modulith.
     */
    @Test
    @Disabled("Disabled because we prefer Plant UML diagrams for module documentation")
    void printApplicationModules() {
        assertDoesNotThrow(() -> MODULES.forEach(System.out::println));
    }
}
