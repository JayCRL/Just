fn adult(age int) bool:
    age >= 18

main:
    Dog d
    d.name = "hello"
    d.age = 18

    d.say:
        if this.age >= 18:
            print("hi, I am ${this.age} years old")
        else:
            print("I am young")

    d.isAdult bool:
        this.age >= 18

    d.say()
    print(adult(d.age))
    print(d.isAdult())
