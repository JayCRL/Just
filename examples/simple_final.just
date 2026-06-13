// Just minimal syntax final demo

Dog:
    name str
    age int

    say(prefix str):
        print("${prefix}, I am ${name}, ${age} years old")

    grow:
        age = age + 1

    olderThan(limit int) bool:
        age > limit

fn adult(age int) bool:
    age >= 18

main:
    dogs Dog[] = [
        ("Jon", 12)
        ("Bob", 13)
        ("Alice", 20)
    ]

    print("dog count: ${dogs.len}")

    dogs[0].say("first")

    i = 0
    while i < dogs.len:
        dogs[i].say("loop")
        print(dogs[i].olderThan(18))
        dogs[i].age = dogs[i].age + 1
        i = i + 1

    Dog d = Dog("Mini", 1)
    d.grow()
    d.say("after grow")

    msg = "Mini adult? ${adult(d.age)}"
    print(msg)
