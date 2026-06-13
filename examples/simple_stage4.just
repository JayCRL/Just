Dog:
    name str
    age int

    say(prefix str):
        print("${prefix}, I am ${name}, ${age} years old")

    olderThan(limit int) bool:
        age > limit

    nextAge int:
        next = age + 1
        next

    canEnter(minAge int) bool:
        (age + 1) >= minAge && age % 2 == 0

main:
    dogs Dog[] = [
        ("Jon", 12)
        ("Bob", 13)
        ("Alice", 20)
    ]

    for dog in dogs:
        dog.say("hello")
        print(dog.olderThan(18))
        print(dog.nextAge())
        print(dog.canEnter(18))
