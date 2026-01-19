# Next Framework Documentation Rules

- Use Markdown format for all documentation files.
- One subdirectory per module. 
- Use mermaid diagrams for architecture and flowcharts.

## For Each module:

- Include a `README.md` with:
  - Module purpose and overview
  - Key classes/functions and their roles
  - High-level architecture diagram (if applicable)
  - Dependencies on other modules
  - Usage examples

- Include an `API.md` with:
  - Detailed API reference for all public interfaces


## AI instructions for generating documentation:
- You are allowed to run code to verify examples.
  - Project uses Java 8
- You are allowed to create new code -- in a temp location -- to help verify examples.
- Ensure all code examples are accurate and tested.
- If some Java or tool is not installed, let the user know what to install.
- It is implicity context that all modules are part of nextframework and are written in Java. No need to mention this in the documentation.

