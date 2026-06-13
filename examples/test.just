class Point {
    private int x;
    private int y;

    Point(int x, int y);

    Point operator+(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }

    bool operator==(Point other) {
        return this.x == other.x && this.y == other.y;
    }

    void display() {
        println("Point(${this.x}, ${this.y})");
    }
}

class Point3D extends Point {
    private int z;

    Point3D(int x, int y, int z) {
        super(x, y);
        this.z = z;
    }

    void display() {
        super.display();
        println("z = ${this.z}");
    }
}

class Main {
    void main() {
        println("=== Just 语言测试 ===");

        // 数组
        int[] numbers = [1, 2, 3, 4, 5];
        println("数组元素:");
        for (int n : numbers) {
            println(n);
        }

        // 区间循环
        println("区间 [0, 5):");
        for (int i : [0, 5)) {
            println(i);
        }

        // 对象和操作符重载
        Point p1 = new Point(1, 2);
        Point p2 = new Point(3, 4);
        Point p3 = p1 + p2;

        println("p1:");
        p1.display();
        println("p2:");
        p2.display();
        println("p1 + p2:");
        p3.display();

        // 继承
        Point3D p3d = new Point3D(10, 20, 30);
        println("Point3D:");
        p3d.display();
    }
}
