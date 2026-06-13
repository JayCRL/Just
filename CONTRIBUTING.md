# Contributing to Just Language

Thank you for your interest in contributing to Just!

## How to Contribute

### Reporting Bugs
- Use GitHub Issues
- Include example code that reproduces the bug
- Specify your environment (OS, Java version, etc.)

### Suggesting Features
- Open a GitHub Issue with the "enhancement" label
- Describe the feature and its use case
- Consider how it fits with Just's design goals

### Pull Requests
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests if applicable
5. Commit your changes (`git commit -m 'Add amazing feature'`)
6. Push to the branch (`git push origin feature/amazing-feature`)
7. Open a Pull Request

## Development Setup

### Prerequisites
- Java 11 or higher
- GCC or Clang (for testing generated code)

### Building
```bash
cd compiler
javac -d bin -encoding UTF-8 src/just/*.java src/just/**/*.java
```

### Running Tests
```bash
java -cp compiler/bin just.JustCompiler examples/ultimate_demo.just
gcc -o test examples/ultimate_demo.c runtime/runtime.c
./test
```

## Code Style
- Follow existing code style
- Add comments for complex logic
- Keep methods focused and small

## Areas Needing Help
- [ ] Generics implementation
- [ ] Standard library
- [ ] More examples
- [ ] Documentation improvements
- [ ] Performance benchmarks

## Questions?
Open an issue or discussion on GitHub.

Thank you for contributing! 🚀
