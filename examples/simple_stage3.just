Dog:
    name str
    age int

    say:
        print("hi, I am ${this.name}, ${this.age} years old")

    isAdult bool:
        this.age >= 18

main:
    dogs Dog[] = [
        ("Jon", 12)
        ("Bob", 13)
        ("Alice", 20)
    ]

    for dog in dogs:
        dog.say()
        print(dog.isAdult())
