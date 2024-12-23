# Inventory Module
This package contains the source code of the Inventory module. By contrast to the
[Catalog module](../catalog/README.md), for the most part the Inventory module relies on Spring Modulith's
interpretation of Java's visibility modifiers to distinguish between module-internal and module-external types. That is,
`public` classes contained the module's base package, i.e., `inventory`, are exposed to other modules, whereas
package-scoped classes on the same level are interpreted as module-internal types.

However, for some types, the Inventory modules resorts to the same modulithic organization strategy as the
[Catalog module](../catalog/README.md). Precisely, the sub-packages `model` and `repository` gather classes with
`public` visibility that need to be visible in tests. As described for the [Catalog module](../catalog/README.md),
Spring Modulith however interprets these `public` classes as module-internal types because they are comprised in
sub-packages of the Inventory module.